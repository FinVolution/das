package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

public class NullExpression extends ColumnExpression implements ConditionProvider {
    private OperatorEnum operator;
    public static NullExpression isNull(AbstractColumn column) {
        return new NullExpression(OperatorEnum.IS_NULL, column);
    }
    
    public static NullExpression isNotNull(AbstractColumn column) {
        return new NullExpression(OperatorEnum.IS_NOT_NULL, column);
    }
    
    NullExpression(OperatorEnum operator, AbstractColumn column) {
        super(operator.getTemplate(), column);
        this.operator = operator;
    }

    /**
     * Not supported
     */
    public ColumnExpression nullable() {
        throw new IllegalStateException("nullable should not be used on IS/IS NOT NULL expression");
    }

    @Override
    public void validate(BuilderContext context) {}

    @Override
    public Condition build() {
        BuilderContext ctx = new DefaultBuilderContext();
        return new ColumnCondition(operator, rightColumn.getTableName(ctx), rightColumn.getColumnName(), null);
    }
}
