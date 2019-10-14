package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;


public class SingleDeleteTask<T> extends TaskAdapter<T> implements SingleTask<T> {
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	@Override
	public int execute(Hints hints, Map<String, ?> fields, T rawPojo) throws SQLException {
		List<Parameter> parameters = new ArrayList<>();
		addParameters(parameters, fields, parser.getPrimaryKeyNames());
		String deleteSql = buildDeleteSql(getTableName(hints, fields));

		return client.update(deleteSql, parameters, hints.setFields(fields));
	}

	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}
}
