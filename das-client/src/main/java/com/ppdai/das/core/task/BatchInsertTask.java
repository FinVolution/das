package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;

public class BatchInsertTask<T> extends InsertTaskAdapter<T> implements BulkTask<int[], T> {
	private static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	@Override
	public int[] getEmptyValue() {
		return new int[0];
	}	

	@Override
	public BulkTaskContext<T> createTaskContext(Hints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) {
		BulkTaskContext<T> context = new BulkTaskContext<T>(rawPojos);
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, daoPojos, rawPojos);
		context.setUnqualifiedColumns(unqualifiedColumns);
		return context;
	}

	@Override
	public int[] execute(Hints hints, Map<Integer, Map<String, ?>> daoPojos, BulkTaskContext<T> taskContext) throws SQLException {
		List<Parameter>[] parametersList = new List[daoPojos.size()];
		int i = 0;
		
		Set<String> unqualifiedColumns = taskContext.getUnqualifiedColumns();
		
		for (Integer index :daoPojos.keySet()) {
			Map<String, ?> pojo = daoPojos.get(index);
			
			removeUnqualifiedColumns(pojo, unqualifiedColumns);

			List<Parameter> parameters = new ArrayList<>();
			addParameters(parameters, pojo);
			parametersList[i++] = parameters;
		}

		String batchInsertSql = buildBatchInsertSql(hints, unqualifiedColumns);
		int[] result = client.batchUpdate(batchInsertSql, parametersList, hints);
		return result;
	}
	
	private String buildBatchInsertSql(Hints hints, Set<String> unqualifiedColumns) throws SQLException {
		List<String> finalInsertableColumns = buildValidColumnsForInsert(unqualifiedColumns);
		
		String values = combine(PLACE_HOLDER, finalInsertableColumns.size(), COLUMN_SEPARATOR);
		String insertColumns = combineColumns(finalInsertableColumns, COLUMN_SEPARATOR);
		
		return String.format(TMPL_SQL_INSERT, getTableName(hints), insertColumns, values);
	}
	
	@Override
	public BulkTaskResultMerger<int[]> createMerger() {
		return new ShardedIntArrayResultMerger();
	}
}
