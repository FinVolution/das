package com.ppdai.das.client.sqlbuilder;

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.client.Segment;
import com.ppdai.das.strategy.OperatorEnum;

public abstract class AbstractColumn implements Segment {
    public static final String PERIOD = ".";
    public static final String AS = " AS ";

    private String columnName;
    private JDBCType type;
    
    public AbstractColumn(String name, JDBCType type) {
        this.columnName = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public abstract String getTableName(BuilderContext helper);
    protected abstract Optional<String> getAlias();
    
    public String getColumnName() {
        return columnName;
    }

    public JDBCType getType() {
        return type;
    }
    
    /**
     * @return the name used in select xxx segment. Like name, or c.name, or c.name as somename
     */
    public String build(BuilderContext helper) {
        return getAlias().isPresent() ? getReference(helper) + AS + getAlias().get() : getReference(helper);
    }
    
    /**
     * @return table name/table alias + .column name/column alias 
     */
    public String getReference(BuilderContext helper) {
        StringBuilder sb = new StringBuilder();
        return sb.append(getTableName(helper)).append(PERIOD).append(columnName).toString();
    }
    
    public String toString() {
        return build(new DefaultBuilderContext());
    }

    public ColumnOrder asc() {
        return new ColumnOrder(this, true);
    }
    
    public ColumnOrder desc() {
        return new ColumnOrder(this, false);
    }
    
    public Parameter of(Object value) {
        return new Parameter(this, value);
    }
    
    public Parameter of(List<?> values) {
        return Parameter.inParameter(this, values);
    }
    
    public ParameterDefinition var() {
        return ParameterDefinition.var(getColumnName(), type);
    }
    
    public ParameterDefinition inVar() {
        return ParameterDefinition.inVar(getColumnName(), type);
    }
    
    /**
     * For single column expression with/without value or expressions that contains two columns
     * 
     * @param template string with %s and ?, like "%s = ?"
     * @param value can be another column or concrete value
     * @return
     */
    private ColumnExpression create(OperatorEnum operator, Object value) {
        if (value instanceof AbstractColumn)
            return new InterColumnExpression(operator, this, (AbstractColumn)value);
        
        if(value instanceof ParameterDefinition)
            return new ColumnDefinitionExpression(operator.getTemplate(), this, ParameterDefinition.defineByVAR((ParameterDefinition)value, this, false));

        if(value instanceof ParameterDefinition.Builder) {
            ParameterDefinition.Builder builder = (ParameterDefinition.Builder)value;
            return new ColumnDefinitionExpression(operator.getTemplate(), this, builder.of(this));
        }
        
        return new ColumnValueExpression(operator, this, value);
    }
    
    public ColumnExpression equal(Object value) {
        return create(OperatorEnum.EQUAL, value);
    }

    public ColumnExpression notEqual(Object value) {
        return create(OperatorEnum.NOT_EQUAL, value);
    }

    public ColumnExpression greaterThan(Object value) {
        return create(OperatorEnum.GREATER_THAN, value);
    }

    public ColumnExpression greaterThanOrEqual(Object value) {
        return create(OperatorEnum.GREATER_THAN_OR_EQUAL, value);
    }

    public ColumnExpression lessThan(Object value) {
        return create(OperatorEnum.LESS_THAN, value);
    }

    public ColumnExpression lessThanOrEqual(Object value) {
        return create(OperatorEnum.LESS_THAN_OR_EQUAL, value);
    }

    public ColumnExpression eq(Object value) {
        return equal(value);
    }

    public ColumnExpression neq(Object value) {
        return notEqual(value);
    }

    public ColumnExpression gt(Object value) {
        return greaterThan(value);
    }

    public ColumnExpression gteq(Object value) {
        return greaterThanOrEqual(value);
    }

    public ColumnExpression lt(Object value) {
        return lessThan(value);
    }

    public ColumnExpression lteq(Object value) {
        return lessThanOrEqual(value);
    }

    public ColumnExpression between(Object firstValue, Object secondValue) {
        return BetweenExpression.between(this, firstValue, secondValue);
    }

    public ColumnExpression notBetween(Object firstValue, Object secondValue) {
        return BetweenExpression.notBetween(this, firstValue, secondValue);
    }

    public ColumnExpression in(List<?> values) {
        return InExpression.in(this, values);
    }

    public <T> ColumnExpression in(T... values) {
        return InExpression.in(this, Arrays.asList(values));
    }

    public ColumnExpression in(ParameterDefinition def) {
        return InExpression.in(this, ParameterDefinition.defineByVAR(def, this, true));
    }

    public ColumnExpression in(ParameterDefinition.Builder builder) {
        return InExpression.in(this, builder.inValues(true).of(this));
    }
    
    public ColumnExpression in(Parameter inParameter) {
        return InExpression.in(this, inParameter);
    }

    public ColumnExpression notIn(List<?> values) {
        return InExpression.notIn(this, values);
    }

    public ColumnExpression notIn(ParameterDefinition def) {
        return InExpression.notIn(this, ParameterDefinition.defineByVAR(def, this, true));
    }

    public ColumnExpression notIn(ParameterDefinition.Builder builder) {
        return InExpression.notIn(this, builder.inValues(true).of(this));
    }

    public ColumnExpression notIn(Parameter inParameter) {
        return InExpression.notIn(this, inParameter);
    }

    public ColumnExpression like(Object value) {
        return create(OperatorEnum.LIKE, value);
    }

    public ColumnExpression notLike(Object value) {
        return create(OperatorEnum.NOT_LIKE, value);
    }

    public ColumnExpression isNull() {
        return NullExpression.isNull(this);
    }

    public ColumnExpression isNotNull() {
        return NullExpression.isNotNull(this);
    }
}
