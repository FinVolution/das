package com.ppdai.das.client.delegate.remote;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ppdai.das.client.sqlbuilder.SqlBuilderSerializer;

import com.ppdai.das.core.client.DalParser;
import com.ppdai.das.core.helper.DalColumnMapRowMapper;
import com.ppdai.das.service.ColumnMeta;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ppdai.das.util.ConvertUtils.entity2POJO;
import static com.ppdai.das.util.ConvertUtils.pojo2Entity;

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
    private DalColumnMapRowMapper mapRowMapper = new DalColumnMapRowMapper();

    public PPDaiDasProxyParser(EntityMeta tableMeta) {
        if(tableMeta == null) {
            return;
        }
        this.tableMeta = tableMeta;
        if(tableMeta.isMapType()){
           return;
        } else {
            this.tableMeta = tableMeta;
            types = new JDBCType[tableMeta.getColumnTypes().size()];
            int i = 0;
            for (String t : tableMeta.getColumnTypes())
                types[i++] = JDBCType.valueOf(t);//.getVendorTypeNumber();
        }
       /* if(tableMeta != null && !tableMeta.isMapType()*//* && tableMeta.getTableName() !=null*//*) {
            this.tableMeta = tableMeta;
            types = new JDBCType[tableMeta.getColumnTypes().size()];
            int i = 0;
            for (String t : tableMeta.getColumnTypes())
                types[i++] = JDBCType.valueOf(t);//.getVendorTypeNumber();
        }*/
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
            return new Entity().setValue(new Gson().toJson(rs.getObject(1)));
        }
        if(tableMeta.isMapType()) {
            Map<String, Object> map = mapRowMapper.map(rs, rowNum);
            return new Entity().setValue(SqlBuilderSerializer.serializePrimitive(map));
        }
        List<String> columnNames = tableMeta.getColumnNames();
        List<String> partialColumns = partialColumns(columnNames, rs.getMetaData());
        Map<String, Object> values = new LinkedHashMap<>(partialColumns.size());

        for (String col : partialColumns)
            values.put(col, rs.getObject(col));

        if(values.isEmpty()){//primitive
            return new Entity().setValue(SqlBuilderSerializer.serializePrimitive(rs.getObject(1)));
        }
        return pojo2Entity(values, tableMeta);
    }

    private List<String> partialColumns(List<String> columnNames, ResultSetMetaData metaData) throws SQLException {
        int columnCount = metaData.getColumnCount();
        Set<String> columns = new HashSet<>(columnCount);
        for(int i = 0; i < columnCount; i++) {
            columns.add(metaData.getColumnLabel(i + 1));
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
        Map<String, Object> asMap = entity2POJO(pojo, tableMeta, Map.class);
        return (Number) asMap.get(getPrimaryKeyNames()[0]);
    }

    @Override
    public Map<String, ?> getPrimaryKeys(Entity pojo) {
        String[] pkNames = getPrimaryKeyNames();
        Map<String, Object> values = entity2POJO(pojo, tableMeta, Map.class);
        Map<String, Object> pks = new LinkedHashMap<>(pkNames.length);
        for (String pk : pkNames)
            pks.put(pk, values.get(pk));
        return pks;
    }

    @Override
    public Map<String, ?> getFields(Entity pojo) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        EntityMeta meta = this.tableMeta;
        Map<String,ColumnMeta> origin = this.tableMeta.getMetaMap();
        LinkedHashMap<String,ColumnMeta> sorted = new LinkedHashMap<>();
        this.tableMeta.getColumnNames().forEach(col -> {
            if(origin.containsKey(col)){
                sorted.put(col, origin.get(col));
            }
        });
        new GsonBuilder().registerTypeHierarchyAdapter(Map.class, (JsonDeserializer<Map>) (json, typeOfSrc, context) -> {
            JsonObject obj = (JsonObject) json;
            Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
            sorted.entrySet().forEach(e -> {
                Optional<Map.Entry<String, JsonElement>> f = set.stream().filter(v -> v.getKey().equals(e.getKey())).findFirst();
                if (f.isPresent()) {
                    ColumnMeta columnMeta = e.getValue();
                    String type = columnMeta.getType();
                    Object value = null;
                    switch (type) {
                        case "INTEGER":
                            value = f.get().getValue().getAsInt();
                            break;
                        case "VARCHAR":
                            value = f.get().getValue().getAsString();
                            break;
                        case "TIMESTAMP":
                            value = new java.sql.Timestamp(f.get().getValue().getAsLong());
                            break;
                    }
                    map.put(e.getKey(), value);
                }
            });
            return map;
        }).create().fromJson(pojo.getValue(), Map.class);

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
