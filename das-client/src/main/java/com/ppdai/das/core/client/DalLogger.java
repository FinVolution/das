package com.ppdai.das.core.client;

import com.ppdai.das.core.configure.DalComponent;
import com.ppdai.das.core.markdown.MarkDownInfo;
import com.ppdai.das.core.markdown.MarkupInfo;
import com.ppdai.das.core.task.DalRequest;
import com.ppdai.das.service.DasRequest;

/**
 * For non cross shard execution
 * start
 *      singleTaskCreated
 *      startTask
 *          start(LogEntry)
 *              startStatement
 *              endStatement
 *          success/fail
 *      endTask
 * end
 * 
 * For non cross shard execution
 * start
 *      crossShardTaskCreated
 *      startCrossShardTasks
 *          startTask
 *              start(LogEntry)
 *                  startStatement
 *                  endStatement
 *              success/fail
 *          endTask
 *      endCrossShards
 * end
 * 
 * @author jhhe
 *
 */
public interface DalLogger extends DalComponent {
	void info(String msg);
	
	void warn(String msg);
	
	void error(String msg, Throwable e);
	
	/**
	 * Fail on getting connections fro the given logic DB
	 * @param dbName
	 * @param e
	 */
	void getConnectionFailed(String dbName, Throwable e);
	
	/**
	 * Start request processing. This will happen at DAO level.
	 * User can chose to pass a customized log context to better track the process
	 * 
	 * @param request
	 * @return log context. It can be null
	 */
	<T> LogContext start(DalRequest<T> request);
	
    /**
     * End request processing
     * @param request
     * 
     */
    void end(LogContext logContext, Throwable e);
    
    /**
     * Start cross shard tasks execution
     * @return Customized log contect
     */
    void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution);
    
    /**
     * Start cross shard tasks execution
     * @param e any exception happened
     */
    void endCrossShards(LogContext logContext, Throwable e);
    
    /**
     * Start task execution
     * @param request
     */
    void startTask(LogContext logContext, String shard);
    
    /**
     * End task execution
     * @param request
     */
    void endTask(LogContext logContext, String shard, Throwable e);
    	
	/**
	 * To create a log entry for current DB operation 
	 * @return
	 */
	LogEntry createLogEntry();
	
	/**
	 * Start the DB operation
	 * @param entry
	 */
	void start(LogEntry entry);
	
	void startStatement(LogEntry entry);
	
	void endStatement(LogEntry entry, Throwable e);
	
	/**
	 * The DB operation is completed successfully
	 */
	void success(LogEntry entry, int count);
	
	/**
	 * The DB operation is fail
	 */
	void fail(LogEntry entry, Throwable e);
	
	/**
	 * The DB is marked down because of error count threshold is reached
	 * @param markdown
	 */
	void markdown(MarkDownInfo markdown);
	
	/**
	 * The DB is marked down because of success count threshold is reached
	 * @param markup
	 */
	void markup(MarkupInfo markup);
	
	/**
	 * The system is going to be shutdown
	 */
	void shutdown();

	/**
	 * Start a remote request from client side
	 * @param dasRequest
	 * @return LogContext
	 */
	LogContext startRemoteRequest(DasRequest dasRequest);

	/**
	 * Complete a remote request from client side
	 * @param logContext
	 * @param e potential exception
	 */
	void completeRemoteRequest(LogContext logContext, Throwable e);

	/**
	 * Receive request from client at server side
	 * @param dasRequest
	 * @return LogContext
	 */
	LogContext receiveRemoteRequest(DasRequest dasRequest);

    /**
     * Complete request from client at server side
     * @param logContext
	 * @param e potential exception
     */
	void finishRemoteRequest(LogContext logContext, Throwable e);

	/**
	 * Log transaction at server side
	 * @param type
	 * @param name
	 * @return
	 */
	LogContext logTransaction(String type, String name);

	/**
	 * Complete transaction at server side
	 * @param logContext
	 * @param throwable
	 */
	void completeTransaction(LogContext logContext, Throwable throwable);

}
