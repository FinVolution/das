package com.ppdai.das.strategy;

public class TableConditionContext extends ConditionContext {
    private TableShardingContext tableShardingContext;

    public TableConditionContext(TableShardingContext tableShardingContext, ColumnCondition condition) {
        super(tableShardingContext, condition);
        this.tableShardingContext = tableShardingContext;
    }

    public String getDbShard() {
        return tableShardingContext.getDbShard();
    }

    public TableConditionContext create(OperatorEnum operator) {
        return new TableConditionContext(tableShardingContext, super.create(operator));
    }

    public TableConditionContext create(OperatorEnum operator, Object value) {
        return new TableConditionContext(tableShardingContext, super.create(operator, value));
    }
}
