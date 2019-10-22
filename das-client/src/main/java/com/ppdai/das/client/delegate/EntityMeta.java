package com.ppdai.das.client.delegate;

import com.ppdai.das.client.TableDefinition;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMeta {
    private final String tableName;
    private final TableDefinition tableDefinition;
    private final Map<String, ColumnMeta> metaMap;
    private final String[] columnNames;
    private final JDBCType[] columnTypes;
    private final String[] primaryKeyNames;
    private final boolean autoIncremental;
    private final String versionColumn;
    private final String[] updatableColumnNames;
    private final String[] insertableColumnNames;
    private final Map<String, Field> fieldMap;
    private final Field identityField;

    public EntityMeta(TableDefinition tableDefinition, ColumnMeta[] columns, Map<String, Field> fieldMap, Field identityField) {
        this.tableDefinition = tableDefinition;
        this.tableName = tableDefinition == null ? null: tableDefinition.getName();
        this.fieldMap = fieldMap;
        this.identityField = identityField;
        
        columnNames = new String[columns.length];
        List<JDBCType> columnTypeList = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        List<String> updatableColumns = new ArrayList<>();
        List<String> insertableColumns = new ArrayList<>();
        String version = null;
        boolean autoInc = false;
        Set<String> nameSet = new HashSet<>();

        metaMap = new ConcurrentHashMap<>();
        for(int i = 0; i < columns.length; i++) {
            ColumnMeta meta = columns[i];
            String name = meta.getName();
            if(nameSet.contains(name))
                throw new IllegalArgumentException("Column name " + name + " is already defined");
            
            metaMap.put(name, meta);
            columnNames[i] = name;
            if(meta.getType() != null) {
                columnTypeList.add(meta.getType());
            }
            
            if(meta.isAutoIncremental())
                autoInc = true;
            
            if(meta.isPrimaryKey())
                primaryKeys.add(name);
            
            if(meta.isInsertable())
                insertableColumns.add(name);
            
            if(meta.isUpdatable())
                updatableColumns.add(name);
            
            if(meta.isVersion())
                if(version == null)
                    version = name;
                else
                    throw new IllegalArgumentException("Version column should only be defined once. Please check the column definition of " + version + " and " + name);
        }

        primaryKeyNames = primaryKeys.toArray(new String[0]);
        updatableColumnNames = updatableColumns.toArray(new String[0]);
        insertableColumnNames = insertableColumns.toArray(new String[0]);
        versionColumn = version;
        autoIncremental = autoInc;
        columnTypes = columnTypeList.toArray(new JDBCType[0]);
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }

    public String getTableName() {
        return tableName;
    }

    public ColumnMeta getColumnMeta(String name) {
        return metaMap.get(name);
    }

    public Map<String, ColumnMeta> getMetaMap() {
        return metaMap;
    }

    public String[] getColumnNames() {
        return copyOf(columnNames);
    }

    public String[] getPrimaryKeyNames() {
        return copyOf(primaryKeyNames);
    }

    public JDBCType[] getColumnTypes() {
        return copyOf(columnTypes);
    }

    public boolean isAutoIncremental() {
        return autoIncremental;
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public String[] getUpdatableColumnNames() {
        return copyOf(updatableColumnNames);
    }

    public String[] getInsertableColumnNames() {
        return copyOf(insertableColumnNames);
    }
    
    public Map<String, Field> getFieldMap() {
        return new HashMap<>(fieldMap);
    }

    public Field getIdentityField() {
        return identityField;
    }

    private <T> T[] copyOf(T[] originalValue) {
        return Arrays.copyOf(originalValue, originalValue.length);
    }
}
