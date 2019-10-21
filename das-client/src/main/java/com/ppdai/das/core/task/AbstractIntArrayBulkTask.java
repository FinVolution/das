package com.ppdai.das.core.task;

import java.util.List;
import java.util.Map;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DasException;


public abstract class AbstractIntArrayBulkTask<T> extends TaskAdapter<T> implements BulkTask<int[], T> {
	public int[] getEmptyValue() {
		return new int[0];
	}
	
	@Override
	public BulkTaskContext<T> createTaskContext(Hints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws DasException{
		return new BulkTaskContext<T>(rawPojos);
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
