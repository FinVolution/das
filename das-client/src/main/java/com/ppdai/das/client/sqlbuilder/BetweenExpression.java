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

public class BetweenExpression extends ColumnExpression implements ParameterDefinitionProvider, ParameterProvider, ConditionProvider {
    private OperatorEnum operator;
    private Object firstValue;
    private Object secondValue;
    
    public static BetweenExpression between(AbstractColumn column, Object firstValue, Object secondValue) {
        return new BetweenExpression(OperatorEnum.BEWTEEN, column, firstValue, secondValue);
    }
    
    public static BetweenExpression notBetween(AbstractColumn column, Object firstValue, Object secondValue) {
        return new BetweenExpression(OperatorEnum.NOT_BETWEEN, column, firstValue, secondValue);
    }
    
    private BetweenExpression(OperatorEnum operator, AbstractColumn column, Object firstValue, Object secondValue) {
        super(operator.getTemplate(), column);
        this.operator = operator;
        this.firstValue = checkDefinition(column, firstValue);
        this.secondValue = checkDefinition(column, secondValue);
    }
    
    private Object checkDefinition(AbstractColumn column, Object value) {
        if(value == null) return null;
        
        if(value instanceof ParameterDefinition.Builder)
            return ((ParameterDefinition.Builder)value).of(column);

        if(value instanceof ParameterDefinition)
            return ParameterDefinition.defineByVAR((ParameterDefinition)value, column, false);
        
        return value;
    }
    
    public void validate(BuilderContext context) {
        Objects.requireNonNull(firstValue, "lower value for column :" + rightColumn.getReference(context) + 
                " is null for BETWEEN. If you want this expression to be removed when value is null, please call nullable() before build");

        Objects.requireNonNull(secondValue, "upper value for column :" + rightColumn.getReference(context) + 
                " is null for BETWEEN. If you want this expression to be removed when value is null, please call nullable() before build");
    }
    
    @Override
    public ColumnExpression nullable() {
        when(firstValue != null && secondValue != null);
        return this;
    }

    @Override
    public List<Parameter> buildParameters() {
        List<Parameter> params = new ArrayList<>();
        
        if(firstValue instanceof ParameterDefinition || secondValue instanceof ParameterDefinition)
            throw new IllegalArgumentException("Instance of Parameter or actual values should be used for BETWEEN parameter");
        
        Parameter p1 = firstValue instanceof Parameter ? (Parameter)firstValue : new Parameter(getRightColumn().getColumnName(), getRightColumn().getType(), firstValue);
        Parameter p2 = secondValue instanceof Parameter ? (Parameter)secondValue : new Parameter(getRightColumn().getColumnName(), getRightColumn().getType(), secondValue);
            
        params.add(p1);
        params.add(p2);

        return params;
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        List<ParameterDefinition> defs = new ArrayList<>();

        if(!(firstValue instanceof ParameterDefinition || secondValue instanceof ParameterDefinition))
            throw new IllegalArgumentException("ParameterDefinition should be used for BETWEEN parameter definition");
        
        defs.add((ParameterDefinition)firstValue);
        defs.add((ParameterDefinition)secondValue);
        
        return defs;
    }

    @Override
    public Condition build() {
        BuilderContext ctx = new DefaultBuilderContext();
        return new ColumnCondition(operator, rightColumn.getTableName(ctx), rightColumn.getColumnName(), firstValue, secondValue);
   }
}