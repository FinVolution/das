package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;

public class SingleInsertTask<T> extends InsertTaskAdapter<T> implements SingleTask<T>, KeyHolderAwaredTask {
	
	@Override
	public int execute(Hints hints, Map<String, ?> fields, T rawPojo) throws SQLException {
		List<Map<String, ?>> pojoList = new ArrayList<Map<String,?>>();
		List<T> rawPojos = new ArrayList<>();
		
		pojoList.add(fields);
		rawPojos.add(rawPojo);
		
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, pojoList, rawPojos);
		
		removeUnqualifiedColumns(fields, unqualifiedColumns);

		/*
		 * In case fields is empty, the final sql will be like "insert into tableName () values()".
		 * We do not report error or simply return 0, but just let DB decide what to do.
		 * For MS Sql server, sql like this is illegal, but for mysql, this works however
		 */
		
		String insertSql = buildInsertSql(hints, fields);

		List<Parameter> parameters = new ArrayList<>();
		addParameters(parameters, fields);
		
		return client.update(insertSql, parameters, hints);
	}
	
	private String buildInsertSql(Hints hints, Map<String, ?> fields) throws SQLException {
		Set<String> remainedColumns = fields.keySet();
		String cloumns = combineColumns(remainedColumns, COLUMN_SEPARATOR);
		String values = combine(PLACE_HOLDER, remainedColumns.size(), COLUMN_SEPARATOR);

		return String.format(TMPL_SQL_INSERT, getTableName(hints, fields), cloumns, values);
	}
}
