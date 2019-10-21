package com.ppdai.das.core.client;

import java.sql.JDBCType;
import java.util.Map;

public interface DalParser<T> extends DalRowMapper<T> {
    String getAppId();
    
	String getDatabaseName();
	
	String getTableName();
	
	String[] getColumnNames();

	String[] getPrimaryKeyNames();

	JDBCType[] getColumnTypes();

	String[] getSensitiveColumnNames();
	
	/**
	 * Assumption: the auto incremental column is also the primary key. 
	 * @return
	 */
	boolean isAutoIncrement();

	Number getIdentityValue(T pojo);
	
	/**
	 * For building where clause for update/delete operation
	 */
	Map<String, ?> getPrimaryKeys(T pojo);

	/**
	 * For insert/update/delete operation
	 */
	Map<String, ?> getFields(T pojo);
	
	/**
	 * The version column, can be number or time stamp 
	 */
	String getVersionColumn();
	
	/**
	 * Column can be included in update sql
	 */
	String[] getUpdatableColumnNames();
	
	/**
	 * Column can be included in insert sql
	 */
	String[] getInsertableColumnNames();
}
