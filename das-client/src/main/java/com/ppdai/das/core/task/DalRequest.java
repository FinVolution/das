package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DasVersionInfo;
import com.ppdai.das.core.ResultMerger;

public interface DalRequest<T> {
    /**
     * @return Current calling application Id
     */
    String getAppId();
    
    /**
     * @return The detailed DAS components version information
     */
    DasVersionInfo getVersionInfo();
    
    /**
     * @return Current Hints that user specified.
     */
    Hints getHints();

	/**
	 * Validate request
	 * @throws SQLException
	 */
    void validate() throws SQLException;

	/**
	 * @return true if it is cross shard
	 */
    boolean isCrossShard() throws SQLException;
	
	/**
	 * Create single task for incoming request
	 * @return
	 * @throws SQLException
	 */
    Callable<T> createTask() throws SQLException;
	
	/**
	 * To split by DB shard
	 * @return map of shard id to callable
	 */
    Map<String, Callable<T>> createTasks() throws SQLException;
	
	/**
	 * @return result merge in cross shard case
	 */
    ResultMerger<T> getMerger();

    /**
     * Doing some cleaning up here
     */
    void endExecution() throws SQLException;
}
