package com.ppdai.das.client.sqlbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

/**
 * A column with a concrete value
 * 
 * @author hejiehui
 *
 */
public class ColumnValueExpression extends ColumnExpression implements ParameterProvider, ConditionProvider {
    private OperatorEnum operator;
    private Object value;

    public ColumnValueExpression(OperatorEnum operator, AbstractColumn column, Object value) {
        super(operator.getTemplate(), column);
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void validate(BuilderContext context) {
        Objects.requireNonNull(value, "value for column :" + rightColumn.getReference(context) + 
                " is null. If you want this expression to be removed when value is null, please call nullable()");
    }

    @Override
    public  List<Parameter> buildParameters() {
        if(value instanceof ParameterDefinition)
            throw new IllegalArgumentException("What you specified is definition instead of value");
        
        
        List<Parameter> pl = new ArrayList<>();
        
        Parameter p = value instanceof Parameter ? (Parameter)value : Parameter.input(getRightColumn().getColumnName(), getRightColumn().getType(), value);
            
        pl.add(p);
        return pl;
    }
    
    /**
     * Mark this expression as optional when expression's value is null.
     */
    public ColumnExpression nullable() {
        when(value != null);
        return this;
    }

    @Override
    public Condition build() {
        BuilderContext ctx = new DefaultBuilderContext();
        return new ColumnCondition(operator, rightColumn.getTableName(ctx), rightColumn.getColumnName(), value);
    }
}
