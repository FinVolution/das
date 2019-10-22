package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.Segment;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.sqlbuilder.TableReference;
import com.ppdai.das.core.client.DalResultSetExtractor;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.helper.DalListMerger;
import com.ppdai.das.core.helper.DalRowMapperExtractor;
import com.ppdai.das.core.helper.DalSingleResultExtractor;
import com.ppdai.das.core.helper.DalSingleResultMerger;
import com.ppdai.das.core.ShardingManager;
import com.ppdai.das.strategy.ConditionList;

public class SqlBuilderProvider implements StatementConditionProvider {
    private SqlBuilder builder;
    private boolean isQuery;
    private DalResultSetExtractor<?> extractor;
    private Supplier<?> mergerFactory;

    private SqlBuilderProvider(SqlBuilder builder, boolean isQuery, DalResultSetExtractor<?> extractor, Supplier<?> mergerFactory) {
        this.builder = builder;
        this.isQuery = isQuery;
        this.extractor = extractor;
        this.mergerFactory = mergerFactory;
    }

    public static SqlBuilderProvider update(SqlBuilder builder) {
        return new SqlBuilderProvider(builder, false, null, ResultMerger.IntSummary::new);
    }

    public static <T> SqlBuilderProvider queryObject(SqlBuilder builder, Supplier<?> mergerFactory) {
        if(mergerFactory == null)
            mergerFactory = DalSingleResultMerger::new;

        DalResultSetExtractor<T> extractor = new DalSingleResultExtractor<>(StatementConditionProvider.getMapper(builder), true);
        return new SqlBuilderProvider(builder, true, extractor, mergerFactory);
    }

    public static SqlBuilderProvider queryList(SqlBuilder builder) {
        DalResultSetExtractor<?> extractor = new DalRowMapperExtractor<>(StatementConditionProvider.getMapper(builder));
        return new SqlBuilderProvider(builder, true, extractor, DalListMerger::new);
    }

    @Override
    public ConditionList buildConditions() throws SQLException {
        return isQuery ? builder.buildQueryConditions(): builder.buildUpdateConditions();
    }

    @Override
    public SqlBuilder getRawRequest() {
        return builder;
    }
    @Override
    public List<Parameter> buildParameters() throws SQLException {
        return builder.buildParameters();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ResultMerger<T> buildMerger() {
        return (ResultMerger<T>)mergerFactory.get();
    }

    @Override
    public <T> SqlBuilderTask<T> buildTask(String appId, String logicDbName) throws SQLException {
        return (SqlBuilderTask<T>) (isQuery ? new QuerySqlBuilderTask<T>(appId, logicDbName, (DalResultSetExtractor<T>)extractor) :
                new UpdateSqlBuilderTask(appId, logicDbName));
    }

    @Override
    public boolean allowCrossShards() {
        return true;
    }

    @Override
    public boolean allowCrossTableShards() {
        return true;
    }

    @Override
    public Hints getHints() {
        return builder.hints();
    }

    @Override
    public boolean isTableShardDecided(String appId, String logicDbName) {
        int shardTableCount = 0;
        int shardDecidedCount = 0;

        for(Segment entry: builder.getFilteredSegments()) {
            if(!(entry instanceof TableReference))
                continue;

            TableReference tabRef = (TableReference)entry;
            if(ShardingManager.isTableShardingEnabled(appId, logicDbName, tabRef.getName()) == false)
                continue;

            shardTableCount++;

            if(tabRef.getShardId() != null || tabRef.getShardValue() != null)
                shardDecidedCount++;
        }

        return shardTableCount == shardDecidedCount;
    }
}
