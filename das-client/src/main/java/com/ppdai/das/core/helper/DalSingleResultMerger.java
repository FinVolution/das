package com.ppdai.das.core.helper;

import java.sql.SQLException;

import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.exceptions.DalException;
import com.ppdai.das.core.exceptions.ErrorCode;

public class DalSingleResultMerger<T> implements ResultMerger<T>{
	private T result;
	
	@Override
	public void addPartial(String shard, T partial) throws SQLException {
		if(partial == null)
			return;
		
		if(result == null)
			result = partial;
		else
			throw new DalException(ErrorCode.AssertSingle);
	}

	@Override
	public T merge() {
		return result;
	}
}
