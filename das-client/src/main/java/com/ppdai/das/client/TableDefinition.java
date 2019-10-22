package com.ppdai.das.client;

import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.TableReference;

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Immutable definition of table. 
 * IMPORTANT NOTE:
 * 
 * Calling as will create a new definition but with the given alias. The original definition will not be changed
 * 
 * @author hejiehui
 *
 */
public class TableDefinition implements TableReference, Segment {
    /**
     * Table name
     */
    private final String name;
    
    private ColumnDefinition[] columnDefinitions;
    private Map<String, ColumnDefinition> columnDefinitionMap = new ConcurrentHashMap<>();
    
    private String alias;
    private String shardId;
    private String shardValue;

    public TableDefinition(String name) {
        this.name = name;
    }

    public void setColumnDefinitions(ColumnDefinition...columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
        columnDefinitionMap.clear();
        for(ColumnDefinition def: columnDefinitions)
            columnDefinitionMap.put(def.getColumnName(), def);
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }
    
    public String getShardId() {
        return shardId;
    }

    public String getShardValue() {
        return shardValue;
    }

    public ColumnDefinition[] allColumns() {
        return Arrays.copyOf(columnDefinitions, columnDefinitions.length);
    }
    
    public ColumnDefinition getColumnDefinition(String columnName) {
        return columnDefinitionMap.get(columnName);
    }

    protected ColumnDefinition column(String name, JDBCType type) {
        return new ColumnDefinition(this, name, type);
    }

    @SuppressWarnings("unchecked")
    protected TableDefinition copy() {
        TableDefinition newTable;
        try {
            newTable = this.getClass().newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException("Error create new instance of " + this.getClass());
        }

        newTable.alias = alias;
        newTable.shardId = shardId;
        newTable.shardValue = shardValue;
        
        return newTable;
    }
    

    @SuppressWarnings("unchecked")
    protected <K> K _as(String alias) {
        TableDefinition newTable = copy();
        newTable.alias = alias;
        return (K)newTable;
    }
    
    protected <K> K _inShard(String shardId) {
        TableDefinition newTable = copy();
        newTable.shardId = shardId;
        return (K)newTable;
    }
    
    protected <K> K _shardBy(String shardValue) {
        TableDefinition newTable = copy();
        newTable.shardValue = shardValue;
        return (K)newTable;
    }

    public String getReference(BuilderContext helper) {
        return alias == null ? helper.locateTableName(this) : alias;
    }

    @Override
    public String build(BuilderContext helper) {
        String realName = helper.locateTableName(this);
        return alias == null ? realName : realName + " " + alias;
    }
    
    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
