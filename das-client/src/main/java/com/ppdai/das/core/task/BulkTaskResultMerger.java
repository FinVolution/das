package com.ppdai.das.core.task;

import com.ppdai.das.core.ResultMerger;

public interface BulkTaskResultMerger<T> extends ResultMerger<T>{
	void recordPartial(String shard, Integer[] partialIndex);
}
