package com.ppdai.das.core.task;

import com.ppdai.das.core.DasComponent;
import com.ppdai.das.core.client.DalParser;

/**
 * All tasks should be staeless
 * @author jhhe
 *
 */
public interface TaskFactory extends DasComponent {
	String getProperty(String key);
	
	<T> SingleTask<T> createSingleInsertTask(DalParser<T> parser);
	
	<T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser);

	<T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser);
	
	<T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser);
	
	<T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser);

	<T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser);
}
