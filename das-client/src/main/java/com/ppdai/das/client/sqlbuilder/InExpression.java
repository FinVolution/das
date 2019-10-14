package com.ppdai.das.client.sqlbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

public class InExpression extends ColumnExpression implements ParameterDefinitionProvider, ParameterProvider, ConditionProvider {
    private OperatorEnum operator;
    private List<?> values;
    private ParameterDefinition definition;
    private Parameter parameter;
    private static final int NO_LIMIT = -1;
    private static volatile int inThreshold = NO_LIMIT;

    public static InExpression in(AbstractColumn column, List<?> values) {
        checkInValueSize(values);
        return new InExpression(OperatorEnum.IN, column, values);
    }
    
    public static InExpression notIn(AbstractColumn column, List<?> values) {
        checkInValueSize(values);
        return new InExpression(OperatorEnum.NOT_IN, column, values);
    }

    private static void checkInValueSize(List<?> values){
        if(inThreshold != NO_LIMIT && values.size() >= inThreshold) {
            throw new IllegalArgumentException(
                    "Size of IN values exceed threshold. size: " + values.size() + ", threshold: " + inThreshold);
        }
    }

    public static InExpression in(AbstractColumn column, ParameterDefinition definition) {
        return new InExpression(OperatorEnum.IN, column, definition);
    }
    
    public static InExpression notIn(AbstractColumn column, ParameterDefinition definition) {
        return new InExpression(OperatorEnum.NOT_IN, column, definition);
    }
    
    public static InExpression in(AbstractColumn column, Parameter parameter) {
        return new InExpression(OperatorEnum.IN, column, parameter);
    }
    
    public static InExpression notIn(AbstractColumn column, Parameter parameter) {
        return new InExpression(OperatorEnum.NOT_IN, column, parameter);
    }
    
    private InExpression(OperatorEnum operator, AbstractColumn column, List<?> values) {
        super(operator.getTemplate(), column);
        this.operator = operator;
        this.values = values;
    }
    
    private InExpression(OperatorEnum operator, AbstractColumn column, ParameterDefinition definition) {
        super(operator.getTemplate(), column);
        this.operator = operator;
        this.definition = ParameterDefinition.defineByVAR(definition, column, true);
    }
    
    private InExpression(OperatorEnum operator, AbstractColumn column, Parameter parameter) {
        super(operator.getTemplate(), column);
        this.operator = operator;
        this.parameter = parameter;
    }
    
    public static boolean isNullInParams(List<?> values) {
        if(null == values || values.size() == 0){
            return true;
        }
        
        Iterator<?> ite = values.iterator();
        while(ite.hasNext()){
            if(ite.next()==null){
                ite.remove();
            }
        }
        
        if(values.size() == 0){
            return true;
        }
        
        return false;
    }

    private List<?> getValues() {
        return parameter == null ? values : parameter.getValues();
    }
    
    @Override
    public void validate(BuilderContext context) {
        if(isNullInParams(getValues()))
            throw new IllegalArgumentException("All values for column :" + rightColumn.getReference(context) + 
                " is null for IN clause. If you want this expression to be removed when value is null, please call nullable() before build");
    }

    @Override
    public InExpression nullable() {
        when(!isNullInParams(getValues()));
        return this;
    }

    @Override
    public List<Parameter> buildParameters() {
        List<Parameter> params = new ArrayList<>();
        
        Parameter p = parameter != null ? parameter : new Parameter(getRightColumn().getColumnName(), getRightColumn().getType(), values);
            
        params.add(p);
        return params;
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        List<ParameterDefinition> defs = new ArrayList<>();
        defs.add(definition);
        return defs;
    }

    @Override
    public Condition build() {
        BuilderContext ctx = new DefaultBuilderContext();
        return new ColumnCondition(operator, rightColumn.getTableName(ctx), rightColumn.getColumnName(), getValues());
    }

    public static void setInThreshold(int inThreshold) {
        InExpression.inThreshold = inThreshold;
    }
}
