package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ppdai.das.client.Hints;

public interface BulkTask<K, T> extends DaoTask<T> {
	K getEmptyValue();
	
	BulkTaskContext<T> createTaskContext(Hints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws SQLException;
	
	K execute(Hints hints, Map<Integer, Map<String, ?>> shaffled, BulkTaskContext<T> taskContext) throws SQLException;
	
	//Merger factory, always return a new merger instance
	BulkTaskResultMerger<K> createMerger();
}
