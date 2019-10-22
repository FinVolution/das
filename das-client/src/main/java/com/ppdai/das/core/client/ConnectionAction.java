package com.ppdai.das.core.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ppdai.das.client.DasClientVersion;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.DasConfigure;
import com.ppdai.das.core.DasException;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.EventEnum;
import com.ppdai.das.core.HintEnum;
import com.ppdai.das.core.LogEntry;

public abstract class ConnectionAction<T> {
    public DasConfigure config;
	public EventEnum operation;
	public String sql;
	public String callString;
	public String[] sqls;
	public List<Parameter> parameters;
	public List<Parameter>[] parametersList;
	public DalCommand command;
	public List<DalCommand> commands;
	public DalConnection connHolder;
	public Set<String> usedDbs = new HashSet<>();
	public Connection conn;
	public Statement statement;
	public PreparedStatement preparedStatement;
	public CallableStatement callableStatement;
	public ResultSet rs;
	public long start;

	public DasLogger logger;
	public LogEntry entry;
	public Throwable e;

	private static final String SQLHIDDENString = "*";
	void populate(EventEnum operation, String sql, List<Parameter> parameters) {
		this.operation = operation;
		this.sql = sql;
		this.parameters = parameters;
	}

	void populate(String[] sqls) {
		this.operation = EventEnum.BATCH_UPDATE;
		this.sqls = sqls;
	}

	void populate(String sql, List<Parameter>[] parametersList) {
		this.operation = EventEnum.BATCH_UPDATE_PARAM;
		this.sql = sql;
		this.parametersList = parametersList;
	}

	void populate(DalCommand command) {
		this.operation = EventEnum.EXECUTE;
		this.command = command;
	}

	void populate(List<DalCommand> commands) {
		this.operation = EventEnum.EXECUTE;
		this.commands = commands;
	}

	void populateSp(String callString, List<Parameter> parameters) {
		this.operation = EventEnum.CALL;
		this.callString = callString;
		this.parameters = parameters;
	}

	void populateSp(String callString, List<Parameter> []parametersList) {
		this.operation = EventEnum.BATCH_CALL;
		this.callString = callString;
		this.parametersList = parametersList;
	}

	public void populateDbMeta() {
		DbMeta meta = null;

		entry.setTransactional(DalTransactionManager.isInTransaction());

		if(DalTransactionManager.isInTransaction()) {
			meta = DalTransactionManager.getCurrentDbMeta();

		} else {
			if(connHolder != null) {
				meta = connHolder.getMeta();
			}
		}

		if(meta != null)
			meta.populate(entry);

		if(connHolder !=null) {
			entry.setMaster(connHolder.isMaster());
			entry.setShardId(connHolder.getShardId());
		}
	}

	public void initLogEntry(String logicDbName, Hints hints) {
	    logger = config.getDasLogger();
		entry = logger.createLogEntry();
		entry.setLogicDbName(logicDbName);
		entry.setDbCategory(config.getDatabaseSet(logicDbName).getDatabaseCategory());
		entry.setClientVersion(DasClientVersion.getVersion());
		entry.setSensitive(hints.is(HintEnum.sensitive));
		entry.setEvent(operation);

		wrapSql();
		entry.setCallString(callString);
		if(sqls != null)
			entry.setSqls(sqls);
		else
			entry.setSqls(sql);

		if (null != parametersList) {
			String[] params = new String[parametersList.length];
			for (int i = 0; i < parametersList.length; i++) {
				params[i] = toLogString(parametersList[i]);
			}
			entry.setPramemters(params);
		} else if (parameters != null) {
			entry.setPramemters(toLogString(parameters));
			hints.setParameters(parameters);
		}
	}

	public String toLogString(List<Parameter> parameters) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (Parameter param : parameters) {
			valuesSb.append(String.format("%s=%s",
					param.getName() == null ? param.getIndex() : param.getName(),
					param.isSensitive() ? SQLHIDDENString : param.getValue()));
			if (++i < parameters.size())
				valuesSb.append(",");
		}
		return valuesSb.toString();
	}

	private void wrapSql() {
		/**
		 * You can not add comments before callString
		 */
		if(sql != null) {
			sql = wrapAPPID(sql);
		}

		if(sqls != null) {
			for(int i = 0; i < sqls.length; i++){
				sqls[i] = wrapAPPID(sqls[i]);
			}
		}

		if(callString != null) {
			// Call can not have comments at the begining
			callString = callString + wrapAPPID("");
		}

	}

	public void start() {
		start = System.currentTimeMillis();
		logger.start(entry);
	}

	public void error(Throwable e) throws SQLException {
		this.e = e;

		// When Db is markdown, there will be no connHolder
		if(connHolder!=null)
			connHolder.error(e);
	}

	public void end(Object result) throws SQLException {
		log(result, e);
		handleException(e);
	}

    public void beginExecute() {
        entry.beginExecute();
    }
    
    public void endExectue() {
        entry.endExectue();
    }
    
    public void beginConnect() {
        entry.beginConnect();
    }
    
    public void endConnect() {
        entry.endConnect();
    }

	private void log(Object result, Throwable e) {
		try {
			entry.setDuration(System.currentTimeMillis() - start);
			if(e == null) {
				logger.success(entry, entry.getResultCount());
			}else{
				logger.fail(entry, e);
			}
		} catch (Throwable e1) {
			logger.error("Can not log", e1);
		}
	}

	public void cleanup() {
		closeResultSet();
		closeStatement();
		closeConnection();
	}

	private void closeResultSet() {
		if(rs != null) {
			try {
				rs.close();
			} catch (Throwable e) {
				logger.error("Close result set failed.", e);
			}
		}
		rs = null;
	}

	private void closeStatement() {
		Statement _statement = statement != null?
				statement : preparedStatement != null?
				preparedStatement : callableStatement;

		statement = null;
		preparedStatement = null;
		callableStatement = null;

		if(_statement != null) {
			try {
				_statement.close();
			} catch (Throwable e) {
				logger.error("Close statement failed.", e);
			}
		}
	}

	private void closeConnection() {
		//do nothing for connection in transaction
		if(DalTransactionManager.isInTransaction())
			return;

		// For list of nested commands, the top level action will not hold any connHolder
		if(connHolder == null)
			return;

		connHolder.close();

		connHolder = null;
		conn = null;
	}

	private void handleException(Throwable e) throws SQLException {
		if(e != null)
			throw e instanceof SQLException ? (SQLException)e : DasException.wrap(e);
	}

	private String wrapAPPID(String sql){
		return "/*" + config.getAppId() + "-" + entry.getCallerInShort() + "*/" + sql;
	}

	public abstract T execute() throws Exception;
}
