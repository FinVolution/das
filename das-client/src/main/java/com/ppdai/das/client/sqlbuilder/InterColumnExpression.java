package com.ppdai.das.client.sqlbuilder;

import java.util.Objects;

import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

public class InterColumnExpression extends ColumnExpression implements ConditionProvider {
    private OperatorEnum operator;
    private AbstractColumn leftColumn;
    
    public InterColumnExpression(OperatorEnum operator, AbstractColumn rightColumn, AbstractColumn leftColumn) {
        super(operator.getTemplate().replace("?", "%s"), rightColumn);
        this.operator = operator;
        this.leftColumn = Objects.requireNonNull(leftColumn);
    }

    public AbstractColumn getLeftColumn() {
        return leftColumn;
    }

    public ColumnExpression nullable() {
        throw new IllegalArgumentException("nullable can not be applied to another column reference");
    }

    @Override
    public String build(BuilderContext helper) {
        String l = leftColumn.getReference(helper);
        return String.format(template, rightColumn.getReference(helper), leftColumn.getReference(helper));
    }

    @Override
    public void validate(BuilderContext context) {}

    @Override
    public Condition build() {
        BuilderContext ctx = new DefaultBuilderContext();
        return new ColumnCondition(operator, rightColumn.getTableName(ctx), rightColumn.getColumnName(), leftColumn);
    }
}
