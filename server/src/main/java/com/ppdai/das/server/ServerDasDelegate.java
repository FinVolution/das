package com.ppdai.das.server;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.client.delegate.local.ClientDasDelegate;
import com.ppdai.das.client.delegate.remote.PPDaiDasProxyParser;

import com.ppdai.das.core.client.DalParser;
import com.ppdai.das.service.ColumnMeta;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;


public class ServerDasDelegate extends ClientDasDelegate {
    private String appId;
    private String logicDbName;
    
    public ServerDasDelegate(String appId, String logicDbName, String customerClientVersion) {
        super(appId, logicDbName, customerClientVersion);
        this.appId = appId;
        this.logicDbName = logicDbName;
    }
    
    protected <T> DalParser<T> getParser(T entity) throws SQLException {
        return getParser(((Entity)entity).getEntityMeta());
    }

    protected <T> DalParser<T> getParser(EntityMeta meta) throws SQLException {
        return (DalParser<T>)new PPDaiDasProxyParser(getAppId(), getLogicDbName(), meta);
    }

    @Override
    protected <T> SqlBuilder countBySampleSqlBuilder(TableDefinition table, T sample) throws SQLException {
        SqlBuilder sqlBuilder = super.countBySampleSqlBuilder(table, sample);
        sqlBuilder.setEntityMeta(((Entity)sample).getEntityMeta());
        return sqlBuilder;
    }

     @Override
    protected <T> SqlBuilder queryByPkSqlBuilder(TableDefinition table, T id, Hints hints) throws SQLException {
         SqlBuilder sb = super.queryByPkSqlBuilder(table, id, hints);
         sb.setEntityMeta(((Entity)id).getEntityMeta());
         return sb;
    }

    @Override
    protected <T> SqlBuilder queryBySampleSqlBuilder(TableDefinition table, T sample) throws SQLException {
        SqlBuilder sb = super.queryBySampleSqlBuilder(table, sample);
        sb.setEntityMeta(((Entity)sample).getEntityMeta());
        return sb;
    }

    @Override
    public <T> List<T> query(SqlBuilder builder) throws SQLException {
        EntityMeta meta = builder.getEntityMeta();

        List list = super.query(builder);
        EntityMeta mapMeta = createMapEntityMeta(list, meta);
        builder.setEntityMeta(mapMeta);
        return list;
    }

    private EntityMeta createMapEntityMeta(List<Map> list, EntityMeta entityMeta) {
        EntityMeta meta = new EntityMeta();
        if(entityMeta == null || !entityMeta.isMapType()){
            return null;
        }
        if(!list.isEmpty()) {
            Map first = list.get(0);
            List<String> colNames = Lists.newArrayList(first.keySet());
            meta.setColumnNames(colNames);

            Map<String,ColumnMeta> metaMap = new HashMap<>();
            for(String name : colNames){
                metaMap.put(name, new ColumnMeta().setType(JDBCType.VARCHAR.getName()));
            }
            meta.setMetaMap(metaMap);
        }
        return meta;
    }

}
