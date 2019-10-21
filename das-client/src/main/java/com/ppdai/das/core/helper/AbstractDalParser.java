package com.ppdai.das.core.helper;

import java.sql.JDBCType;

import com.ppdai.das.core.client.DalParser;

public abstract class AbstractDalParser<T> implements DalParser<T> {
	protected String dataBaseName;
	protected String tableName;
	protected String[] columns;
	protected String[] primaryKeyColumns;
	protected JDBCType[] columnTypes;
	protected String[] sensitiveColumnNames;
	protected String versionColumn;
	protected String[] insertableColumnNames;
	protected String[] updatableColumnNames;
	
	public AbstractDalParser(){}
	
	public AbstractDalParser(
			String dataBaseName,
			String tableName,
			String[] columns,
			String[] primaryKeyColumns,
			JDBCType[] columnTypes) {
		this.dataBaseName = dataBaseName;
		this.tableName = tableName;
		this.columns = columns;
		this.updatableColumnNames = columns;
		this.insertableColumnNames = columns;
		this.primaryKeyColumns = primaryKeyColumns;
		this.columnTypes = columnTypes;
	}

	@Override
	public String getDatabaseName() {
		return dataBaseName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String[] getColumnNames() {
		return columns;
	}
	
	@Override
	public String[] getPrimaryKeyNames() {
		return primaryKeyColumns;
	}
	
	@Override
	public JDBCType[] getColumnTypes() {
		return columnTypes;
	}

	@Override
	public String[] getSensitiveColumnNames() {
		return sensitiveColumnNames;
	}
	
	public String getVersionColumn() {
		return versionColumn;
	}
	
	public String[] getUpdatableColumnNames() {
		return updatableColumnNames;
	}
	
	public String[] getInsertableColumnNames() {
		return insertableColumnNames;
	}
}
