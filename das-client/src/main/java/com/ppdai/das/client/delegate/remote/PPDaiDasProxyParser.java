package com.ppdai.das.client.delegate.remote;

import com.google.gson.Gson;
import com.ppdai.das.client.sqlbuilder.SqlBuilderSerializer;
import com.ppdai.das.core.client.DalParser;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;


import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ppdai.das.util.ConvertUtils.entity2Map;
import static com.ppdai.das.util.ConvertUtils.map2Entity;

/**
 * For DAS parse
 * @author hejiehui
 *
 */
public class PPDaiDasProxyParser implements DalParser<Entity> {
    private String appId;
    private String logicDbName;
    private EntityMeta tableMeta;
    private JDBCType[] types;

    public PPDaiDasProxyParser(EntityMeta tableMeta) {
        if(tableMeta != null && tableMeta.getTableName() !=null) {
            this.tableMeta = tableMeta;
            types = new JDBCType[tableMeta.getColumnTypes().size()];
            int i = 0;
            for (String t : tableMeta.getColumnTypes())
                types[i++] = JDBCType.valueOf(t);//.getVendorTypeNumber();
        }
    }

    public PPDaiDasProxyParser(String appId, String logicDbName, EntityMeta tableMeta) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.tableMeta = tableMeta;
        types = new JDBCType[tableMeta.getColumnTypes().size()];
        int i = 0;
        for (String t : tableMeta.getColumnTypes())
            types[i++] = JDBCType.valueOf(t);//.getVendorTypeNumber();
    }

    /**
     * TODO need to consider get by index and not same names set see
     * Also need to check if there is missing columns
     * DalDefaultJpaMapper line 139
     * 
     */
    @Override
    public Entity map(ResultSet rs, int rowNum) throws SQLException {
        if(tableMeta == null){//primitive
            //return new Entity().setValue(new Gson().toJson(rs.getObject(1)));
            return new Entity().setValue(SqlBuilderSerializer.serializePrimitive(rs.getObject(1)));
        }
        List<String> columnNames = tableMeta.getColumnNames();
        List<String> partialColumns = partialColumns(columnNames, rs.getMetaData());
        Map<String, Object> values = new LinkedHashMap<>(partialColumns.size());

        for (String col : partialColumns)
            values.put(col, rs.getObject(col));

        if(values.isEmpty()){//primitive
            //return new Entity().setValue(new Gson().toJson(rs.getObject(1)));
            return new Entity().setValue(SqlBuilderSerializer.serializePrimitive(rs.getObject(1)));
        }
        return map2Entity(values, tableMeta);
    }

    private List<String> partialColumns(List<String> columnNames, ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        Set<String> columns = new HashSet<>(columnCount);
        for(int i = 0; i < columnCount; i++) {
            columns.add(metaData.getColumnName(i + 1));
        }
        return columnNames.stream().filter(col -> columns.contains(col)).collect(Collectors.toList());
    }

    @Override
    public String getDatabaseName() {
        return logicDbName;
    }

    @Override
    public String getTableName() {
        return tableMeta.getTableName();
    }

    @Override
    public String[] getColumnNames() {
        List<String> columnNames = tableMeta.getColumnNames();
        return columnNames.toArray(new String[columnNames.size()]);
    }

    @Override
    public String[] getPrimaryKeyNames() {
        List<String> primaryKeyNames = tableMeta.getPrimaryKeyNames();
        return primaryKeyNames.toArray(new String[primaryKeyNames.size()]);
    }

    @Override
    public JDBCType[] getColumnTypes() {
        return types;
    }

    @Override
    public String[] getSensitiveColumnNames() {
        return getColumnNames();
    }

    @Override
    public boolean isAutoIncrement() {
        return tableMeta.isAutoIncremental();
    }

    @Override
    public Number getIdentityValue(Entity pojo) {
        Map<String, Object> asMap = entity2Map(pojo, tableMeta);
        return (Number) asMap.get(getPrimaryKeyNames()[0]);
    }

    @Override
    public Map<String, ?> getPrimaryKeys(Entity pojo) {
        String[] pkNames = getPrimaryKeyNames();
        Map<String, Object> values = entity2Map(pojo, tableMeta);
        Map<String, Object> pks = new LinkedHashMap<>(pkNames.length);
        for (String pk : pkNames)
            pks.put(pk, values.get(pk));
        return pks;
    }

    @Override
    public Map<String, ?> getFields(Entity pojo) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> values = new Gson().fromJson(pojo.getValue(), Map.class);
        this.tableMeta.getColumnNames().stream().forEach(col -> {
            map.put(col,  values.get(col));

        });

        return map;
    }

    @Override
    public String getVersionColumn() {
        return tableMeta.getVersionColumn();
    }

    @Override
    public String[] getUpdatableColumnNames() {
        List<String> updatableColumnNames = tableMeta.getUpdatableColumnNames();
        return updatableColumnNames.toArray(new String[updatableColumnNames.size()]);
    }

    @Override
    public String[] getInsertableColumnNames() {
        List<String> insertableColumnNames = tableMeta.getInsertableColumnNames();
        return insertableColumnNames.toArray(new String[insertableColumnNames.size()]);
    }

    @Override
    public String getAppId() {
        return appId;
    }
}
