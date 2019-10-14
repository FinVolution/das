package com.ppdai.das.client.delegate.local;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ppdai.das.client.delegate.EntityMeta;
import com.ppdai.das.client.delegate.EntityMetaManager;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DalRowMapper;
import com.ppdai.das.core.helper.AbstractDalParser;
import com.ppdai.das.core.helper.CustomizableMapper;

/**
 * TODO check UpdatableEntity
 * @author hejiehui
 *
 * @param <T>
 */
public class PPDaiDalParser<T> extends AbstractDalParser<T> implements CustomizableMapper<T> {
    private String appId;
    private Class<T> clazz;
    private Map<String, Field> fieldsMap;
    private Field identity;
    private boolean autoIncrement;
    private PPDaiDalMapper<T> rowMapper;
    
    private EntityMeta tableMeta;
    
    public PPDaiDalParser(Class<T> clazz) throws SQLException {
        this.clazz = clazz;
        tableMeta = EntityMetaManager.extract(clazz);
        rowMapper = new PPDaiDalMapper<T>(clazz);
        fieldsMap = tableMeta.getFieldMap();

        this.columnTypes = tableMeta.getColumnTypes();
        this.tableName = tableMeta.getTableName();
        this.columns = tableMeta.getColumnNames();
        this.primaryKeyColumns = tableMeta.getPrimaryKeyNames();
        this.autoIncrement = tableMeta.isAutoIncremental();
        this.identity = tableMeta.getIdentityField();
        this.updatableColumnNames = tableMeta.getUpdatableColumnNames();
        this.insertableColumnNames = tableMeta.getInsertableColumnNames();
        this.sensitiveColumnNames = tableMeta.getColumnNames();
        this.versionColumn = tableMeta.getVersionColumn();
    }
    
    public PPDaiDalParser(String appId, String logicDbName, Class<T> clazz) throws SQLException {
        this(clazz);
        this.appId = appId;
        this.dataBaseName = logicDbName;
    }

    @Override
    public T map(ResultSet rs, int rowNum) throws SQLException {
        return rowMapper.map(rs, rowNum);
    }

    @Override
    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    @Override
    public Number getIdentityValue(T pojo) {
        if (pojo.getClass().equals(this.clazz) && identity != null) {
            try {
                Object val = identity.get(pojo);
                if (val instanceof Number)
                    return (Number) val;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public Map<String, ?> getPrimaryKeys(T pojo) {
        return getFields(getPrimaryKeyNames(), pojo);
    }

    @Override
    public Map<String, ?> getFields(T pojo) {
        return getFields(getColumnNames(), pojo);
    }
    
    private Map<String, ?> getFields(String[] columnNames, T pojo) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (String columnName: columnNames) {
            try {
                map.put(columnName, fieldsMap.get(columnName).get(pojo));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    @Override
    public DalRowMapper<T> mapWith(ResultSet rs, Hints hints)
            throws SQLException {
        return rowMapper.mapWith(rs, hints);
    }

    @Override
    public DalRowMapper<T> mapWith(String[] columns) throws SQLException {
        return rowMapper.mapWith(columns);
    }

    @Override
    public String getAppId() {
        return appId;
    }
}
