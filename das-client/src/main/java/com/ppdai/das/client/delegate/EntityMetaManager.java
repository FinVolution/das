package com.ppdai.das.client.delegate;


import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;

/**
 * This manager is for both table entity and query entity
 * @author hejiehui
 */
public class EntityMetaManager {
    private static ConcurrentHashMap<Class<?>, EntityMeta> registeredTable = new ConcurrentHashMap<>();

    public static EntityMeta extract(Class<?> clazz) {
        if(registeredTable.containsKey(clazz))
            return registeredTable.get(clazz);
        
        String tableName = null;
        TableDefinition td = null;
        Map<String, ColumnDefinition> colMap = new HashMap<>();
        try {
            Field tableDef = clazz.getDeclaredField(clazz.getSimpleName().toUpperCase());
            td = (TableDefinition)tableDef.get(null);
            tableName = td.getName();
        } catch (Throwable e) {
            // It is OK for only the query entity
        }
        if(td != null)
            for(ColumnDefinition colDef: td.allColumns())
                colMap.put(colDef.getColumnName(), colDef);
        
        Field[] allFields = clazz.getDeclaredFields();
        if (null == allFields || allFields.length == 0)
            throw new IllegalArgumentException("The entity[" + clazz.getName() +"] has no fields.");

        Map<String, Field> fieldMap = new HashMap<>();
        Field idField = null;
        List<ColumnMeta> columns = new ArrayList<>();
        for (Field f: allFields) {
            Column column = f.getAnnotation(Column.class);
            Id id =  f.getAnnotation(Id.class);

            // Column must be specified
            if (column == null)
                continue;

            String columnName = column.name().trim().length() == 0 ? f.getName() : column.name();
            if(tableName != null)
                Objects.requireNonNull(colMap.get(columnName), "Column " + columnName + " must be defined in " + tableName);
            
            f.setAccessible(true);
            fieldMap.put(columnName, f);
            
            GeneratedValue generatedValue = f.getAnnotation(GeneratedValue.class);
            boolean autoIncremental = null != generatedValue && 
                    (generatedValue.strategy() == GenerationType.AUTO || generatedValue.strategy() == GenerationType.IDENTITY);
            
            if(idField != null && autoIncremental)
                throw new IllegalArgumentException("Found duplicate Id columns");
            else if (id != null)
                idField = f;
            
            JDBCType type = colMap.containsKey(columnName) ? colMap.get(columnName).getType() : null;
            columns.add(new ColumnMeta(
                    columnName,
                    type,
                    autoIncremental,
                    id != null,
                    column.insertable(),
                    column.updatable(),
                    f.getAnnotation(Version.class) != null));
        }

        if(tableName!= null && columns.size() != colMap.size())
            throw new IllegalArgumentException("The columns defined in table definition does not match the columns defined in class");
        
        EntityMeta tableMeta = new EntityMeta(td, columns.toArray(new ColumnMeta[0]), fieldMap, idField);
        
        registeredTable.putIfAbsent(clazz, tableMeta);
        
        return registeredTable.get(clazz);
    }
}