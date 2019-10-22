package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.EntityMetaManager;
import com.ppdai.das.client.delegate.local.PPDaiDalMapper;
import com.ppdai.das.client.delegate.remote.PPDaiDasProxyParser;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.client.DalRowMapper;
import com.ppdai.das.core.helper.DalColumnMapRowMapper;
import com.ppdai.das.core.helper.DalObjectRowMapper;
import com.ppdai.das.strategy.ConditionList;


public interface StatementConditionProvider {
    Hints getHints();

    List<Parameter> buildParameters() throws SQLException;

    ConditionList buildConditions() throws SQLException;

    /**
     * @return if table shard id is set for referneced tables
     */
    boolean isTableShardDecided(String appId, String logicDbName);

    <T> ResultMerger<T> buildMerger();

    <T> SqlBuilderTask<T> buildTask(String appId, String logicDbName) throws SQLException;

    boolean allowCrossShards();

    boolean allowCrossTableShards();

    <T> T getRawRequest();

    static DalRowMapper getMapper(SqlBuilder sqlBuilder) {
        if(sqlBuilder.getEntityType() == com.ppdai.das.service.Entity.class) {
            return new PPDaiDasProxyParser(sqlBuilder.getEntityMeta());
        }

        return getMapper(sqlBuilder.getEntityType());
    }

    static DalRowMapper getMapper(Class type) {
        Objects.requireNonNull(type, "The entity type is missing, you should specify entity type using one of the SqlBuilder.into() method");

        if(type == Object.class)
            return new DalObjectRowMapper();

        if(type == Map.class)
            return new DalColumnMapRowMapper();

        if(EntityMetaManager.extract(type).getColumnNames().length == 0)
            return new DalObjectRowMapper();
        else
            return new PPDaiDalMapper(type);
    }
}
