package com.ppdai.das.core.task;

import static com.ppdai.das.core.ShardingManager.buildTableName;
import static com.ppdai.das.core.ShardingManager.getDatabaseSet;
import static com.ppdai.das.core.ShardingManager.isTableShardingEnabled;
import static com.ppdai.das.core.ShardingManager.locateTableShardId;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.HintEnum;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.UpdatableEntity;
import com.ppdai.das.core.client.DalClient;
import com.ppdai.das.core.client.DalParser;
import com.ppdai.das.core.enums.DatabaseCategory;

public class TaskAdapter<T> implements DaoTask<T> {
	public static final String GENERATED_KEY = "GENERATED_KEY";

	//public static final String TMPL_SQL_FIND_BY = "SELECT * FROM %s WHERE %s";

	protected static final String COLUMN_SEPARATOR = ", ";
	protected static final String PLACE_HOLDER = "?";
	protected static final String TMPL_SET_VALUE = "%s=?";
	protected static final String AND = " AND ";
	protected static final String OR = " OR ";
	protected static final String TMPL_CALL = "{call %s(%s)}";

	public static String findtmp = "SELECT * FROM %s WHERE %s";
	
	protected DalClient client;
	protected DalParser<T> parser;

	protected String appId;
	protected String logicDbName;
	protected DatabaseCategory dbCategory;
	protected String pkSql;
	protected Set<String> pkColumns;
	protected Set<String> sensitiveColumns;
	protected Map<String, JDBCType> columnTypes = new HashMap<String, JDBCType>();
	
	protected String updateCriteriaTmpl;
	protected String setValueTmpl;
	protected String setVersionValueTmpl;
	protected boolean hasVersion;
	protected boolean isVersionUpdatable;
	protected Set<String> defaultUpdateColumnNames;

	
	public boolean tableShardingEnabled;
	protected String rawTableName;

	public void initialize(DalParser<T> parser) {
	    this.appId = parser.getAppId();
        this.logicDbName = parser.getDatabaseName();
		this.client = DasConfigureFactory.getClient(appId, logicDbName);
		this.parser = parser;

		rawTableName = parser.getTableName();
		tableShardingEnabled = isTableShardingEnabled(parser.getAppId(), logicDbName, rawTableName);
		initColumnTypes();
		
		dbCategory = getDatabaseSet(parser.getAppId(), logicDbName).getDatabaseCategory();
		initDbSpecific();
		initSensitiveColumns();
	}
	
	public void initDbSpecific() {
		pkSql = initPkSql();
		initUpdateColumns();
		setValueTmpl = dbCategory.getNullableUpdateTpl();
		initVersionColumnUpdateTemplate();
	}
	
	private void initUpdateColumns() {
		defaultUpdateColumnNames = new LinkedHashSet<>(Arrays.asList(parser.getUpdatableColumnNames()));
		
		for (String column : parser.getPrimaryKeyNames()) {
			defaultUpdateColumnNames.remove(column);
		}
		
		hasVersion = parser.getVersionColumn() != null;
		isVersionUpdatable = hasVersion ? defaultUpdateColumnNames.contains(parser.getVersionColumn()) : false;

		// Remove Version from updatable columns
		if(hasVersion)
			defaultUpdateColumnNames.remove(parser.getVersionColumn());
	}
	
	/**
	 * If there is version column and it is updatable, the column can not be null and it will always use the update version template.
	 */
	private void initVersionColumnUpdateTemplate() {
		String versionColumn = parser.getVersionColumn();
		updateCriteriaTmpl = pkSql;

		if(versionColumn == null)
			return;
		
		String quotedVersionColumn = quote(parser.getVersionColumn());
		updateCriteriaTmpl += AND + String.format(TMPL_SET_VALUE, quotedVersionColumn);
		
		if(!isVersionUpdatable)
			return;

		JDBCType versionType = getColumnType(versionColumn);

		String valueTmpl = null;
		if(versionType == JDBCType.TIMESTAMP){
			valueTmpl = dbCategory.getTimestampExp();
		}else{
			valueTmpl = quote(parser.getVersionColumn()) + "+1";
		}
		
		setVersionValueTmpl = quotedVersionColumn + "=" + valueTmpl;
	}
	
	public String getTableName(Hints hints) throws SQLException {
	    String tableName = tableShardingEnabled ?
	            buildTableName(appId, logicDbName, rawTableName, locateTableShardId(appId, logicDbName, hints)):
	                rawTableName;
        
        return quote(tableName);
	}
	
	public String getTableName(Hints hints, Map<String, ?> fields) throws SQLException {
        String tableName = tableShardingEnabled ?
                buildTableName(appId, logicDbName, rawTableName, locateTableShardId(appId, logicDbName, rawTableName, hints.cleanUp(), fields)):
                    rawTableName;
        
        return quote(tableName);
	}
	
	/**
	 * Add all the entries into the parameters by index. The parameter index
	 * will depends on the index of the entry in the entry set, value will be
	 * entry value. The value can be null.
	 * 
	 * @param parameters A container that holds all the necessary parameters
	 * @param entries Key value pairs to be added into parameters
	 */
	public void addParameters(List<Parameter> parameters,
			Map<String, ?> entries) {
		int index = parameters.size() + 1;
		for (Map.Entry<String, ?> entry : entries.entrySet()) {
			addParameter(parameters, index++, entry.getKey(), entry.getValue());
		}
	}

	public void addParameters(List<Parameter> parameters,
			Map<String, ?> entries, String[] validColumns) {
		int index = parameters.size() + 1;
		for(String column : validColumns){
			addParameter(parameters, index++, column, entries.get(column));
		}
	}
	
	public int addParameters(int start, List<Parameter> parameters,
			Map<String, ?> entries, List<String> validColumns) {
		int count = 0;
		for(String column : validColumns){
			addParameter(parameters, count + start, column, entries.get(column));
			count++;
		}
		return count;
	}

	public void addParameter(List<Parameter> parameters, int index, String columnName, Object value) {
		if(isSensitive(columnName))
			setSensitive(parameters, index, columnName, getColumnType(columnName), value);
		else
			set(parameters, index, columnName, getColumnType(columnName), value);
	}

	private List<Parameter> setSensitive(List<Parameter> parameters, int index, String name, JDBCType type, Object value) {
		parameters.add(new Parameter(name, type, value).setSensitive(true).setIndex(index));
		return parameters;
	}

	private List<Parameter> set(List<Parameter> parameters, int index, String name, JDBCType type, Object value) {
		parameters.add(new Parameter(name, type, value).setIndex(index));
		return parameters;
	}

	/**
	 * Get the column type defined in java.sql.Types.
	 * 
	 * @param columnName The column name of the table
	 * @return value defined in java.sql.Types
	 */
	public JDBCType getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}

	public Set<String> getUpdatedColumns(T rawPojo) {
		return rawPojo instanceof UpdatableEntity ?
				((UpdatableEntity)rawPojo).getUpdatedColumns() :
					(Set<String>)Collections.EMPTY_SET;
	}

	public Set<String> filterColumns(Hints hints) {
		Set<String> qulifiedColumns = new HashSet<>(defaultUpdateColumnNames);
		if(hints.is(HintEnum.includedColumns))
			qulifiedColumns.retainAll(hints.getIncluded());
			
		if(hints.is(HintEnum.excludedColumns))
			qulifiedColumns.removeAll(hints.getExcluded());
			
		return qulifiedColumns;
	}

	public Map<String, ?> removeAutoIncrementPrimaryFields(Map<String, ?> fields){
		// This is bug here, for My Sql, auto incremental id can be part of the joint primary key.
		// But for Ctrip, a table must have a pk defined by single column as mandatory, so we don't have problem here
		if(parser.isAutoIncrement())
			fields.remove(parser.getPrimaryKeyNames()[0]);
		return fields;
	}

	public List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}

	public boolean isPrimaryKey(String fieldName){
		return pkColumns.contains(fieldName);
	}
	
	public boolean isSensitive(String fieldName){
		if(sensitiveColumns.isEmpty())
			return false;
		
		return sensitiveColumns.contains(fieldName);
	}
	
	public String initPkSql() {
		pkColumns = new HashSet<String>();
		Collections.addAll(pkColumns, parser.getPrimaryKeyNames());

		// Build primary key template
		String template = combine(TMPL_SET_VALUE, parser.getPrimaryKeyNames().length, AND);

		return String.format(template, (Object[]) quote(parser.getPrimaryKeyNames()));
	}

	public void initSensitiveColumns() {
		sensitiveColumns = new HashSet<String>();
		if(parser.getSensitiveColumnNames() != null)
			Collections.addAll(sensitiveColumns, parser.getSensitiveColumnNames());
	}

	// Build a lookup table
	public void initColumnTypes() {
		String[] cloumnNames = parser.getColumnNames();
		JDBCType[] columnsTypes = parser.getColumnTypes();
		for (int i = 0; i < cloumnNames.length; i++) {
			columnTypes.put(cloumnNames[i], columnsTypes[i]);
		}
	}
	
	public Map<String, ?> getPrimaryKeys(Map<String, ?> fields) {
		Map<String, Object> pks = new LinkedHashMap<>();
		for(String pkName: parser.getPrimaryKeyNames())
			pks.put(pkName, fields.get(pkName));
		return pks;
	}

	public String combineColumns(Collection<String> values, String separator) {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (String value : values) {
			quote(valuesSb, value);
			if (++i < values.size())
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}

	public String combine(String value, int count, String separator) {
		StringBuilder valuesSb = new StringBuilder();

		for (int i = 1; i <= count; i++) {
			valuesSb.append(value);
			if (i < count)
				valuesSb.append(separator);
		}
		return valuesSb.toString();
	}
	
	public String quote(String column) {
		return dbCategory.quote(column);
	}

	public StringBuilder quote(StringBuilder sb, String column) {
		return sb.append(dbCategory.quote(column));
	}
	
	public Object[] quote(Set<String> columns) {
		Object[] quatedColumns = columns.toArray();
		for(int i = 0; i < quatedColumns.length; i++)
			quatedColumns[i] = quote((String)quatedColumns[i]);
		return quatedColumns;
	}
	
	public String[] quote(String[] columns) {
		String[] quatedColumns = new String[columns.length];
		for(int i = 0; i < columns.length; i++)
			quatedColumns[i] = quote(columns[i]);
		return quatedColumns;
	}

    @Override
    public DalParser<T> getParser() {
        return parser;
    }

    @Override
    public Map<String, ?> getPojoFields(T daoPojo) {
        return parser.getFields(daoPojo);
    }
}
