package com.ppdai.das.strategy;

import java.util.Set;

import com.ppdai.das.client.Hints;

public class TableShardingContext extends ShardingContext {
    //Optional table name, only used when tableShardValue is set.
    //To evaluate table shard id for entity
    private String logicTableName;

    public TableShardingContext(String appId, String logicDbName, String logicTableName, Set<String> allTableShards, Hints hints, ConditionList conditions) {
        super(appId, logicDbName, allTableShards, hints, conditions);
        this.logicTableName = logicTableName;
    }

    public TableConditionContext create(ColumnCondition condition) {
        return new TableConditionContext(this, condition);
    }
    
    public String getDbShard() {
        return getHints().getShard();
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public boolean hasTableShardValue() {
        return getHints().hasTableShardValue();
    }

    public Object getTableShardValue() {
        return getHints().getTableShardValue();
    }
}
