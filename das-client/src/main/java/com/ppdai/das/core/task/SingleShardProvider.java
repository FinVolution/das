package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.CallBuilder;
import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.strategy.ConditionList;

public class SingleShardProvider implements StatementConditionProvider {
    private Object rawBuilder;
    private BiFunction<String, String, ?> taskFactory;
    private Hints hints;
    
    private <T> SingleShardProvider(T rawBuilder, Hints hints, BiFunction<String, String, ?> taskFactory) {
        this.rawBuilder = rawBuilder;
        this.taskFactory = taskFactory;
        this.hints = hints;
    }

    public static SingleShardProvider create(BatchUpdateBuilder batchUpdateBuilder) {
        return new SingleShardProvider(batchUpdateBuilder, batchUpdateBuilder.hints(), (appId, logicDbName)->new BatchUpdateBuilderTask(appId, logicDbName));
    }

    public static SingleShardProvider create(CallBuilder callBuilder) {
        return new SingleShardProvider(callBuilder, callBuilder.hints(), (appId, logicDbName)->new CallBuilderTask(appId, logicDbName));
    }
    
    public static SingleShardProvider create(BatchCallBuilder batchCallBuilder) {
        return new SingleShardProvider(batchCallBuilder, batchCallBuilder.hints(), (appId, logicDbName)->new BatchCallBuilderTask(appId, logicDbName));
    }
    
    public static <T> SingleShardProvider create(CallableTransaction<T> transaction, Hints hints) {
        return new SingleShardProvider(transaction, hints, (appId, logicDbName)->new TransactionTask<T>());
    }
    
    @Override
    public List<Parameter> buildParameters() throws SQLException {
        return new ArrayList<Parameter>();
    }

    @Override
    public ConditionList buildConditions() throws SQLException {
        return ConditionList.andList();
    }

    @Override
    public <T> ResultMerger<T> buildMerger() {
        //For single shard operation, there is no need for a result merger
        return null;
    }

    @Override
    public <T> SqlBuilderTask<T> buildTask(String appId, String logicDbName) throws SQLException {
        return (SqlBuilderTask<T>)taskFactory.apply(appId, logicDbName);
    }

    @Override
    public boolean allowCrossShards() {
        return false;
    }

    @Override
    public boolean allowCrossTableShards() {
        return false;
    }

    @Override
    public <T> T getRawRequest() {
        return (T)rawBuilder;
    }

    @Override
    public Hints getHints() {
        return hints;
    }

    @Override
    public boolean isTableShardDecided(String appId, String logicDbName) {
        return true;
    }
}
