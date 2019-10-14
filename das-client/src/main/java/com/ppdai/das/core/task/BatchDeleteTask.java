package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;


public class BatchDeleteTask<T> extends AbstractIntArrayBulkTask<T> {
	private static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";

	@Override
	public int[] execute(Hints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext) throws SQLException {
		List<Parameter>[] parametersList = new List[daoPojos.size()];
		List<String> pkNames = Arrays.asList(parser.getPrimaryKeyNames());

		int i = 0;
		for (Integer index :daoPojos.keySet()) {
			List<Parameter> parameters = new ArrayList<>();
			addParameters(1, parameters, daoPojos.get(index), pkNames);
			parametersList[i++] = parameters;
		}
		
		String deleteSql = buildDeleteSql(getTableName(hints));
		int[] result = client.batchUpdate(deleteSql, parametersList, hints);
		return result;
	}
	
	private String buildDeleteSql(String tableName) {
		return String.format(TMPL_SQL_DELETE, tableName, pkSql);
	}
}
