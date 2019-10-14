package com.ppdai.das.core.enums;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.markdown.ErrorContext;

public enum DatabaseCategory {
	MySql(
			"%s=IFNULL(?,%s) ",
			"CURRENT_TIMESTAMP",
			new int[]{1043,1159,1161},
			new int[]{1021,1037,1038,1039,1040,1041,1154,1158,1160,1189,1190,1205,1218,1219,1220}
	){
		public String quote(String fieldName){
			return "`" + fieldName + "`";
		}

		public boolean isTimeOutException(ErrorContext ctx){
			return ctx.getExType().toString().equalsIgnoreCase(MySQLTimeoutException.class.toString());
		}

		public String buildList(String effectiveTableName, String columns, String whereExp){
			return String.format("SELECT %s FROM %s WHERE %s", columns, effectiveTableName, whereExp);
		}

		public String buildTop(String effectiveTableName, String columns, String whereExp, int count){
			return String.format("SELECT %s FROM %s WHERE %s LIMIT %d", columns, effectiveTableName, whereExp, count);
		}

		public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count){
			return String.format("SELECT %s FROM %s WHERE %s LIMIT %d, %d", columns, effectiveTableName, whereExp, start, count);
		}

		public String buildPage(String selectSqlTemplate, int start, int count){
			return String.format(selectSqlTemplate + " limit %d, %d", start, count);
		}
	},

	SqlServer(
			"%s=ISNULL(?,%s) ",
			"getDate()",
			new int[]{-2,233,845,846,847,1421},
			new int[]{2,53,701,802,945,1204,1222}
	){

		public String quote(String fieldName){
			return "[" + fieldName + "]";
		}

		public String buildList(String effectiveTableName, String columns, String whereExp){
			return String.format("SELECT %s FROM %s WITH (NOLOCK) WHERE %s", columns, effectiveTableName, whereExp);
		}

		public boolean isTimeOutException(ErrorContext ctx){
			return ctx.getMsg().startsWith("The query has timed out") || ctx.getMsg().startsWith("查询超时");
		}

		public String buildTop(String effectiveTableName, String columns, String whereExp, int count){
			return String.format("SELECT TOP %d %s FROM %s WITH (NOLOCK) WHERE %s", count, columns, effectiveTableName, whereExp);
		}

		public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count){
			return String.format("SELECT %s FROM %s WITH (NOLOCK) WHERE %s OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", columns, effectiveTableName, whereExp, start, count);
		}

		public String buildPage(String selectSqlTemplate, int start, int count){
			return String.format(selectSqlTemplate + " OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", start, count);
		}

		public void setObject(CallableStatement statement, Parameter parameter) throws SQLException{
			if(parameter.getValue() != null && getSqlType(parameter) == SQL_SERVER_TYPE_TVP){
				SQLServerCallableStatement sqlsvrStatement = (SQLServerCallableStatement)statement;
				sqlsvrStatement.setStructured(parameter.getIndex(), parameter.getName(), (SQLServerDataTable)parameter.getValue());
			}else{
				super.setObject(statement, parameter);
			}
		}
	},

	Oracle(
			"%s=NVL(?,%s) ",
			"SYSTIMESTAMP",
			new int[]{-1},
			new int[]{-1}
	){
		public String quote(String fieldName){
			return fieldName;//"\"" + fieldName + "\"";
		}

		public boolean isTimeOutException(ErrorContext ctx){
			return false;
		}

		public String buildList(String effectiveTableName, String columns, String whereExp){
			return String.format("SELECT %s FROM %s WHERE %s", columns, effectiveTableName, whereExp);
		}

		public String buildTop(String effectiveTableName, String columns, String whereExp, int count){
			return String.format("SELECT * FROM (SELECT %s FROM %s WHERE %s) WHERE ROWNUM <= %d", columns, effectiveTableName, whereExp, count);
		}

		public String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count){
			return String.format(
					"SELECT * FROM (SELECT ROWNUM RN, T1.* FROM (SELECT %s FROM %s WHERE %s)T1 WHERE ROWNUM <= %d)T2 WHERE T2.RN >=%d",
					columns, effectiveTableName, whereExp, start+count, start);
		}

		public String buildPage(String selectSqlTemplate, int start, int count){
			return String.format(
					"SELECT * FROM (SELECT ROWNUM RN, T1.* FROM (%s)T1 WHERE ROWNUM <= %d)T2 WHERE T2.RN >=%d",
					selectSqlTemplate, start+count, start);
		}
	};

	private String nullableUpdateTpl;
	private String timestampExp;
	private Set<Integer> retriableCodeSet;
	private Set<Integer> failOverableCodeSet;

	public static final String SQL_PROVIDER = "sqlProvider";
	public static final String MYSQL_PROVIDER = "mySqlProvider";
	public static final String ORACLE_PROVIDER = "oracleProvider";

	public static final int SQL_SERVER_TYPE_TVP = -1000;

	public static DatabaseCategory matchWith(String provider) {
		if(provider == null || provider.trim().length() == 0)
			throw new RuntimeException("The provider value can not be NULL or empty!");

		provider = provider.trim();
		if(provider.equalsIgnoreCase(SQL_PROVIDER))
			return DatabaseCategory.SqlServer;

		if(provider.equalsIgnoreCase(MYSQL_PROVIDER))
			return DatabaseCategory.MySql;

		if(provider.equalsIgnoreCase(ORACLE_PROVIDER))
			return DatabaseCategory.Oracle;

		throw new RuntimeException("The provider: " + provider + " can not be recoganized");
	}

	public Set<Integer> getDefaultRetriableErrorCodes() {
		return new TreeSet<Integer>(retriableCodeSet);
	}

	public Set<Integer> getDefaultFailOverableErrorCodes() {
		return new TreeSet<Integer>(failOverableCodeSet);
	}

	public Set<Integer> getDefaultErrorCodes() {
		Set<Integer> errorCodes = getDefaultRetriableErrorCodes();
		errorCodes.addAll(retriableCodeSet);
		errorCodes.addAll(failOverableCodeSet);
		return errorCodes;
	}

	public boolean isDisconnectionError(String sqlState) {
		if (sqlState == null)
			return false;

		switch (this) {
			case MySql:
				//SQLError.SQL_STATE_COMMUNICATION_LINK_FAILURE
				return sqlState.equals("08S01");
			case SqlServer:
				//SQLServerException.EXCEPTION_XOPEN_CONNECTION_FAILURE
				return sqlState.equals("08S01") || sqlState.equals("08006");
			default:
				// The default connection related error codes are start with "08"
				return sqlState.startsWith("08");
		}
	}

	public String getTimestampExp() {
		return timestampExp;
	}

	/**
	 * This is for compatible with code generated for dal 1.4.1 and previouse version. Such code is like:
	 *
	 * 	SelectSqlBuilder builder = new SelectSqlBuilder("person", dbCategory, true);
	 * 	...
	 *	int index =  builder.getStatementParameterIndex();
	 *	parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize);
	 *  parameters.set(index++, Types.INTEGER, pageSize);
	 *	return queryDao.query(sql, parameters, hints, parser);
	 */
	public String getPageSuffixTpl() {
		switch (this) {
			case MySql:
				return " limit ?, ?";
			case SqlServer:
				return " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
			default:
				return null;
		}
	}

	public abstract boolean isTimeOutException(ErrorContext ctx);

	public abstract String quote(String fieldName);

	public abstract String buildList(String effectiveTableName, String columns, String whereExp);

	public abstract String buildTop(String effectiveTableName, String columns, String whereExp, int count);

	public abstract String buildPage(String effectiveTableName, String columns, String whereExp, int start, int count);

	public abstract String buildPage(String selectSqlTemplate, int start, int count);

	public void setObject(PreparedStatement statement, Parameter parameter) throws SQLException{
	    if(parameter.getValue() == null) {
            statement.setNull(parameter.getIndex(), getSqlType(parameter));
        }else{
            statement.setObject(parameter.getIndex(), parameter.getValue(), getSqlType(parameter));
		}
	}

	public void setObject(CallableStatement statement, Parameter parameter) throws SQLException{
		if(parameter.getValue() == null) {
			statement.setNull(parameter.getIndex(), getSqlType(parameter));
		}else{
			statement.setObject(parameter.getIndex(), parameter.getValue(), getSqlType(parameter));
		}
	}

	public String getNullableUpdateTpl() {
		return nullableUpdateTpl;
	}

	private DatabaseCategory(String nullableUpdateTpl, String timestampExp, int[] retriableCodes, int[] failOverableCodes) {
		this.nullableUpdateTpl = nullableUpdateTpl;
		this.timestampExp = timestampExp;
		this.retriableCodeSet = parseErrorCodes(retriableCodes);
		this.failOverableCodeSet = parseErrorCodes(failOverableCodes);
	}

	private Set<Integer> parseErrorCodes(int[] codes){
		Set<Integer> temp = new TreeSet<Integer>();
		for(int value: codes)
			temp.add(value);

		return temp;
	}
	
	private static int getSqlType(Parameter parameter) {
	    return parameter.getType().getVendorTypeNumber();
	}
}
