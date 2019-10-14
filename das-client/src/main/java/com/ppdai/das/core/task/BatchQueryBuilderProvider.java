package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.ppdai.das.client.BatchQueryBuilder;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.core.DalResultSetExtractor;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.helper.DalListMerger;
import com.ppdai.das.core.helper.DalRowMapperExtractor;
import com.ppdai.das.core.helper.MultipleResultMerger;
import com.ppdai.das.strategy.ConditionList;

public class BatchQueryBuilderProvider implements StatementConditionProvider {
    private BatchQueryBuilder builder;
    
    public BatchQueryBuilderProvider(BatchQueryBuilder builder) {
        this.builder = builder;
    }

    @Override
    public ConditionList buildConditions() throws SQLException {
        ConditionList conditions = ConditionList.andList();
        for(SqlBuilder entry: builder.getQueries()) {
            conditions.add(entry.buildQueryConditions());
        }
        return conditions;
    }

    @Override
    public BatchQueryBuilder getRawRequest() {
        return builder;
    }

    @Override
    public List<Parameter> buildParameters() throws SQLException {
        List<Parameter> params = new LinkedList<Parameter>();
        for(SqlBuilder entry: builder.getQueries()) {
            params.addAll(entry.buildParameters());
        }

        return Parameter.reindex(params);
    }

    @Override
    public <T> ResultMerger<T> buildMerger() {
        MultipleResultMerger mergers = new MultipleResultMerger();
        for(SqlBuilder entry: builder.getQueries()) {
            mergers.add(new DalListMerger<>());
        }

        return (ResultMerger<T>)mergers;
    }

    @Override
    public <T> SqlBuilderTask<T> buildTask(String appId, String logicDbName) throws SQLException {
        List<DalResultSetExtractor<?>> extractors = new LinkedList<>();
        for(SqlBuilder entry: builder.getQueries()) {
            extractors.add(new DalRowMapperExtractor(StatementConditionProvider.getMapper(entry.getEntityType())));
        }

        return (SqlBuilderTask<T>)new BatchQueryBuilderTask(appId, logicDbName, extractors);
    }

    @Override
    public boolean allowCrossShards() {
        return true;
    }

    @Override
    public boolean allowCrossTableShards() {
        // For now we do not allow cross multiple table shards query for batch
        return false;
    }

    @Override
    public Hints getHints() {
        return builder.hints();
    }

    @Override
    public boolean isTableShardDecided(String appId, String logicDbName) {
        return true;
    }
}
