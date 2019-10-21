package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.client.DalClient;
import com.ppdai.das.client.Hints;

public interface SqlTask<T> {
	T execute(DalClient client, String sql, List<Parameter> parameters, Hints hints) throws SQLException;
}
