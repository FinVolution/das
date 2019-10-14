package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.Map;

import com.ppdai.das.client.Hints;

public interface SingleTask<T> extends DaoTask<T> {
	int execute(Hints hints, Map<String, ?> daoPojo, T rawPojo) throws SQLException;
}
