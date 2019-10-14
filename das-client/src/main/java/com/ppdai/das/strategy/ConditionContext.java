package com.ppdai.das.strategy;

import com.ppdai.das.client.Hints;

import java.util.Set;


public class ConditionContext extends ColumnCondition {
    private ShardingContext shardingContext;

    public ConditionContext(ShardingContext shardingContext, ColumnCondition condition) {
        super(condition);
        this.shardingContext = shardingContext;
    }

    public String getLogicDbName() {
        return shardingContext.getLogicDbName();
    }

    public Set<String> getAllShards() {
        return shardingContext.getAllShards();
    }

    public Hints getHints() {
        return shardingContext.getHints();
    }
    
    public ConditionContext create(OperatorEnum operator) {
        return new ConditionContext(shardingContext, super.create(operator));
    }

    public ConditionContext create(OperatorEnum operator, Object value) {
        return new ConditionContext(shardingContext, super.create(operator, value));
    }
}
