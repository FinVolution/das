package com.ppdai.das.core.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.HintEnum;
import com.ppdai.das.core.KeyHolder;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.enums.DatabaseCategory;
import com.ppdai.das.core.status.StatusManager;
import com.ppdai.das.core.status.TimeoutMarkdown;

public class DalStatementCreator {
	private static final int DEFAULT_RESULT_SET_TYPE = ResultSet.TYPE_FORWARD_ONLY;
	private static final int DEFAULT_RESULT_SET_CONCURRENCY = ResultSet.CONCUR_READ_ONLY;
	
	private DatabaseCategory dbCategory;
	public DalStatementCreator(DatabaseCategory dbCategory) {
	    this.dbCategory = dbCategory;
	}
	
	public Statement createStatement(Connection conn, Hints hints) throws Exception {
		Statement statement = conn.createStatement(getResultSetType(hints), getResultSetConcurrency(hints));
		
		applyHints(statement, hints);
		
		return statement;
	}

	public PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter> parameters, Hints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql, getResultSetType(hints), getResultSetConcurrency(hints));
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		
		return statement;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter> parameters, Hints hints, KeyHolder keyHolder) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		
		return statement;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, List<Parameter>[] parametersList, Hints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql, getResultSetType(hints), getResultSetConcurrency(hints));
		
		applyHints(statement, hints);
		for(List<Parameter> parameters: parametersList) {
			setParameter(statement, parameters);
			statement.addBatch();
		}
		
		return statement;
	}
	
	public CallableStatement createCallableStatement(Connection conn,  String sql, List<Parameter> parameters, Hints hints) throws Exception {
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		registerOutParameters(statement, parameters);

		return statement;
	}
	
	public CallableStatement createCallableStatement(Connection conn,  String sql, List<Parameter>[] parametersList, Hints hints) throws Exception {
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hints);
		
		for(List<Parameter> parameters: parametersList) {
			setParameter(statement, parameters);
			statement.addBatch();
		}

		return statement;
	}

	private void setParameter(PreparedStatement statement, List<Parameter> parameters) throws Exception {
		for (Parameter parameter: parameters) {
			if(parameter.isInputParameter())
			    dbCategory.setObject(statement, parameter);
		}
	}
	
	private void setParameter(CallableStatement statement, List<Parameter> parameters) throws Exception {
		for (Parameter parameter: parameters) {
			if(parameter.isInputParameter()) {
			    dbCategory.setObject(statement, parameter);
			}
		}
	}

	private void registerOutParameters(CallableStatement statement, List<Parameter> parameters) throws Exception {
		for (Parameter parameter: parameters) {
			if(parameter.isOutParameter()) {
				if(parameter.getName() == null)
					statement.registerOutParameter(parameter.getIndex(), getSqlType(parameter));
				else
					statement.registerOutParameter(parameter.getName(), getSqlType(parameter));
			}
		}
	}
	
	private void applyHints(Statement statement, Hints hints) throws SQLException {
		Integer fetchSize = (Integer)hints.get(HintEnum.fetchSize);
		
		if(fetchSize != null && fetchSize > 0)
			statement.setFetchSize(fetchSize);

		Integer maxRows = (Integer)hints.get(HintEnum.maxRows);
		if (maxRows != null && maxRows > 0)
			statement.setMaxRows(maxRows);

        Integer timeout = (Integer)hints.get(HintEnum.timeout);
        if (timeout == null || timeout < 0)
            timeout = StatusManager.getTimeoutMarkdown().getTimeoutThreshold();

		statement.setQueryTimeout(timeout);
	}
	
	private int getResultSetType(Hints hints) {
		return hints.getInt(HintEnum.resultSetType, DEFAULT_RESULT_SET_TYPE);
	}

	private int getResultSetConcurrency(Hints hints) {
		return hints.getInt(HintEnum.resultSetConcurrency, DEFAULT_RESULT_SET_CONCURRENCY);
	}

	private int getSqlType(Parameter parameter) {
        return parameter.getType().getVendorTypeNumber();
    }
}
