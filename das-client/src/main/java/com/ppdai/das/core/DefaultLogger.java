package com.ppdai.das.core;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ppdai.das.client.DasClient;
import com.ppdai.das.client.DasClientVersion;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.helper.DalBase64;
import com.ppdai.das.core.helper.LoggerHelper;
import com.ppdai.das.core.markdown.MarkDownInfo;
import com.ppdai.das.core.markdown.MarkupInfo;
import com.ppdai.das.core.task.DalRequest;
import com.ppdai.das.service.DasRequest;

/**
 * logger that is based on log4j. It is useful when you want to quick start your DAL project without
 * a specified logger
 * @author jhhe
 *
 */
public class DefaultLogger extends LoggerAdapter implements DasLogger {
	
	private Logger logger = LoggerFactory.getLogger(DasClientVersion.getLoggerName());
	
	private static final String LINESEPARATOR = System.lineSeparator();

	private static final ThreadLocal<DasDiagnose> diagnoseHolder = new ThreadLocal<>();

	@Override
	public void info(final String desc) {
	    call(new Runnable() {public void run() {
			logger.info(desc);
	    }});
	}

	@Override
	public void warn(final String desc) {
        call(new Runnable() {public void run() {
            logger.warn(desc);
        }});
	}

	@Override
	public void error(final String desc, final Throwable e) {
        call(new Runnable() {public void run() {
            logger.error(desc, e);
        }});
	}
	
	private void infoOrError(final String desc, final Throwable e) {
        if(e == null)
            info(desc);
        else
            error(desc, e);

	}

	@Override
	public void getConnectionFailed(final String logicDb, final Throwable e) {
        call(new Runnable() {public void run() {
            logConnectionFailed(logicDb, e);
        }});
	}
	
	private void logConnectionFailed(String realDbName, Throwable e) {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append(String.format("Log Name: %s" + System.lineSeparator(), "Get connection"));
		sbuffer.append(String.format("Event: %s" + System.lineSeparator(), 
				EventEnum.CONNECTION_FAILED.getEventId()));
		
		String msg= "Connectiing to " + realDbName + " database failed." + System.lineSeparator();

		sbuffer.append(String.format("Message: %s " + System.lineSeparator(), msg));
		
		logError(sbuffer.toString(), e);
	}
	
	private void logError(String desc, Throwable e) {
		try {
			String msg = LoggerHelper.getExceptionStack(e);

			String logMsg = desc + System.lineSeparator()
					+ System.lineSeparator()
					+ "********** Exception Info **********"
					+ System.lineSeparator() + msg;
			logger.error(logMsg);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public LogEntry createLogEntry() {
		LogEntry logEntry = new LogEntry();

		DasDiagnose taskDiagnose = diagnoseHolder.get();
		if (taskDiagnose != null) {
			DasDiagnose diagnose = taskDiagnose.spawn("statement");
			diagnose.append("statement", diagnose.hashCode());
			DasDiagnose statementInfoDiagnose = diagnose.spawn("statement-info");
			logEntry.setDasDiagnose(statementInfoDiagnose);
		}

		return logEntry;
	}

	@Override
	public void start(LogEntry entry) {
		return;
	}

	@Override
	public void success(final LogEntry entry, final int count) {
		recordStatementResult(entry, null);
		if (samplingLogging && !validate(entry) )
			return;
        call(()-> recordSuccess(entry, count));
	}

	private void recordStatementResult(LogEntry logEntry, Throwable e) {
		// 使用DasDiagnose记录shard信息
		DasDiagnose dasDiagnose = logEntry.getDasDiagnose();
		if (dasDiagnose != null) {
			//String tracingId = dasDiagnose.getName();
			dasDiagnose.append("success", e == null);
			dasDiagnose.append("exception", e != null ? e : "");
			dasDiagnose.append("cost", logEntry.getCostDetail());
			dasDiagnose.append("databaseName", logEntry.getDatabaseName());
			dasDiagnose.append("databaseKeyName", logEntry.getDataBaseKeyName());
			dasDiagnose.append("logicDbName", logEntry.getLogicDbName());
			dasDiagnose.append("isMaster", String.valueOf(logEntry.isMaster()));
			dasDiagnose.append("dbCategory", logEntry.getDbCategory().name());
			dasDiagnose.append("dbConnectString", logEntry.getDbUrl());

			String[] sqls = logEntry.getSqls();
			for (int i = 0; i < sqls.length; i++) {
				dasDiagnose.append("sql[" + i + "]", sqls[i]);
			}
		}

	}

	private void recordSuccess(final LogEntry entry, final int count) {
		try {
			StringBuilder msg = new StringBuilder("success info \n");
			msg.append("\t").append("DAS.Client.version : java-").append(entry.getClientVersion()).append(LINESEPARATOR);
			msg.append("\t").append("source : ").append(entry.getSource()).append(LINESEPARATOR);
			String sql = "*";
			if (!entry.isSensitive()) {
				sql = LoggerHelper.getSqlTpl(entry);
			}
			msg.append("\t").append("sql: ").append(sql).append(LINESEPARATOR);
			if (entry.getPramemters() != null) {
				msg.append("\t").append("parameters : ").append(getEncryptParameters(encryptLogging, entry)).append(LINESEPARATOR);
			} else {
				msg.append("\t").append("parameters : ").append(LINESEPARATOR);
			}
			msg.append("\t").append("CostDetail : ").append(entry.getCostDetail()).append(LINESEPARATOR);
			msg.append("\t").append("SQL.database : ").append(entry.getDbUrl()).append(LINESEPARATOR);
			logger.info(msg.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private String getEncryptParameters(boolean encryptLogging, LogEntry entry){
		String params = "";
		if(encryptLogging){
			try {
				params = new String(DalBase64.encodeBase64(LoggerHelper.getParams(entry).getBytes()));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			params = LoggerHelper.getParams(entry);
		}
		return params;
	}
	
	@Override
	public void fail(final LogEntry entry, final Throwable e) {
        error(e.getMessage(), e);
		recordStatementResult(entry, e);
	}
	
	@Override
	public void markdown(final MarkDownInfo markdown) {
	    info(logMarkdown(markdown));
	}
	
	private String logMarkdown(MarkDownInfo markdown) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markdown.info").append(LINESEPARATOR);
		msg.append("\t total:").append(markdown.getTotal()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markdown.getDbKey()).append(LINESEPARATOR);
		msg.append("\t MarkDownPolicy:").append(markdown.getPolicy().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Status:").append(markdown.getStatus()).append(LINESEPARATOR);
		msg.append("\t SamplingDuration:").append(markdown.getDuration().toString()).append(LINESEPARATOR);
		msg.append("\t Reason:").append(markdown.getReason().toString().toLowerCase()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markdown.getVersion()).append(LINESEPARATOR);
		return msg.toString();
	}

	@Override
	public void markup(final MarkupInfo markup) {
        info(logMarkup(markup));
	}
	
	private String logMarkup(MarkupInfo markup) {
		StringBuilder msg = new StringBuilder();
		msg.append("arch.dal.markup.info").append(LINESEPARATOR);
		msg.append("\t Qualifies:").append(markup.getQualifies()).append(LINESEPARATOR);
		msg.append("\t AllInOneKey:").append(markup.getDbKey()).append(LINESEPARATOR);
		msg.append("\t Client:").append(markup.getVersion()).append(LINESEPARATOR);
		return msg.toString();
	}

	@Override
	public void shutdown() {
		logger.info("shutdown DefaultLogger.");
		super.shutdown();
	}

	@Override
	public LogContext startRemoteRequest(DasRequest dasRequest) {
		return null;
	}

	@Override
	public void completeRemoteRequest(LogContext logContext, Throwable e) {

	}

	@Override
	public LogContext receiveRemoteRequest(DasRequest dasRequest) {
		return null;
	}

	@Override
	public void finishRemoteRequest(LogContext logContext, Throwable e) {

	}

	@Override
	public LogContext logTransaction(String type, String name) {
		return null;
	}

	@Override
	public void completeTransaction(LogContext logContext, Throwable throwable) {

	}

	@Override
    public <T> LogContext start(DalRequest<T> request) {
        logger.info("start request");
		LogContext logContext = new LogContext();
		logContext.setAppId(request.getAppId());

		Hints dalHints = request.getHints();
		if (dalHints.get(HintEnum.userDefined1) != null) {
			DasDiagnose dasDiagnose = (DasDiagnose) dalHints.get(HintEnum.userDefined1);
			logContext.setDasDiagnose(dasDiagnose);
		}

		recordStartRequest(logContext, request.getVersionInfo());
		return logContext;
    }

	private void recordStartRequest(LogContext logContext, DasVersionInfo versionInfo) {
		List<StackTraceElement> callers = getDasClientCaller();
		StackTraceElement caller = Iterables.getFirst(callers, null);
		logContext.setCaller(caller);

		// 使用DasDiagnose记录版本信息
		DasDiagnose dasDiagnose = logContext.getDasDiagnose();
		if (dasDiagnose != null) {
			dasDiagnose.append("das.caller", caller.toString());
			dasDiagnose.append("das.clientMethod", caller.toString());
			dasDiagnose.append("das.version", versionInfo.getDasClientVersion());
			dasDiagnose.append("das.ppdai.version", versionInfo.getCustomerClientVersion());
			dasDiagnose.append("das.server.version", versionInfo.getDasServerVersion());
			dasDiagnose.append("das.ppdai.server.version", versionInfo.getCustomerServerVersion());
		}
	}

	private List<StackTraceElement> getDasClientCaller() {
		StackTraceElement[] callers = Thread.currentThread().getStackTrace();
		for (int i = callers.length -1 ; i >=0; i--) {
			if (callers[i].getClassName().equals(DasClient.class.getName())) {
				if (i + 1 < callers.length) {
					return Lists.newArrayList(callers[i + 1], callers[i]);
				}
			}
		}
		return Collections.nCopies(2, new StackTraceElement("N/A", "N/A", "N/A", -1));
	}

    @Override
    public void end(LogContext logContext, Throwable e) {
        infoOrError("end request", e);
    }

    @Override
    public void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution) {
        info("Start Cross Shard Tasks");
		recordStartCrossShardTasks(logContext);
    }

	private void recordStartCrossShardTasks(LogContext logContext) {
			Set<String> shardSet = logContext.getShards();
			if (shardSet != null) {

				// 使用DasDiagnose记录shard信息
				DasDiagnose dasDiagnose = logContext.getDasDiagnose();
				if (dasDiagnose != null) {
					dasDiagnose.append("das.totoalShard", shardSet.size());
					dasDiagnose.append("das.shards", shardSet.toString());

					for (String shard : shardSet) {
						DasDiagnose shardDiagnose = dasDiagnose.spawn("shard-" + shard);
						shardDiagnose.append("shard", shard);
						DasDiagnose taskDiagnose = shardDiagnose.spawn("task-" + shard);
						taskDiagnose.append("task.type", "cross shard task");
						taskDiagnose.append("task.shard", shard);
						logContext.putTaskDiagnose("task-" + shard, taskDiagnose);
					}
				}
			}

	}

    @Override
    public void endCrossShards(LogContext logContext, Throwable e) {
        infoOrError("End Cross Shards", e);
    }

    @Override
    public void startTask(LogContext logContext, String shard) {
        info("Start Task: " +shard);

		recordStartTask(logContext, shard);
		diagnoseHolder.set(logContext.getTaskDiagnose("task-" + shard));
    }

	private void recordStartTask(LogContext logContext, String shard) {
		DasDiagnose dasDiagnose = logContext.getDasDiagnose();
		if (dasDiagnose != null) {
			DasDiagnose taskDiagnose = logContext.getTaskDiagnose("task-" + shard);
			if (taskDiagnose == null) {
				taskDiagnose = dasDiagnose.spawn("task-" + shard);
				taskDiagnose.append("task.type", "single task");
				logContext.putTaskDiagnose("task-" + shard, taskDiagnose);
			}
		}
	}

    @Override
    public void endTask(LogContext logContext, String shard, Throwable e) {
        infoOrError("End Task: " + shard, e);
		diagnoseHolder.remove();
    }

    @Override
    public void startStatement(LogEntry entry) {
        info("Start Statement");
    }

    @Override
    public void endStatement(LogEntry entry, Throwable e) {
        infoOrError("End Statement", e);
    }
}