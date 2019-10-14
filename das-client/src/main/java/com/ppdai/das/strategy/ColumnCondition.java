package com.ppdai.das.strategy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represent table column, operator and value.
 * It has three subclass, single, in and between
 * @author hejiehui
 *
 */
/**
 * @author hejiehui
 *
 */
public class ColumnCondition implements Condition {
    private OperatorEnum operator;
    private String tableName;
    private String columnName;
    private Object value;
    private Object secondValue;
    
    public ColumnCondition(OperatorEnum operator, String tableName, String columnName, Object value) {
        this.operator = operator;
        this.tableName = tableName;
        this.columnName = columnName;
        this.value = value;
    }
    
    public ColumnCondition(OperatorEnum operator, String tableName, String columnName, Object firstValue, Object secondValue) {
        this(operator, tableName, columnName, firstValue);
        this.secondValue = secondValue;
    }
    
    public ColumnCondition(OperatorEnum operator, String tableName, String columnName, List<?> value) {
        this(operator, tableName, columnName, (Object)value);
    }
    
    public ColumnCondition(ColumnCondition template) {
        this(template.operator, template.tableName, template.columnName, template.value, template.secondValue);
    }

    public OperatorEnum getOperator() {
        return operator;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return value;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public List<Object> getValues() {
        return (List<Object>)value;
    }
    
    public Set<String> getTables() {
        Set<String> tables = new HashSet<>();
        tables.add(tableName);
        return tables;
    }

    @Override
    public Condition reverse() {
        operator = operator.getReversed();
        return this;
    }

    public ColumnCondition create(OperatorEnum newOperator) {
        return new ColumnCondition(newOperator, tableName, columnName, value, secondValue);
    }

    public ColumnCondition create(OperatorEnum newOperator, Object value) {
        return new ColumnCondition(newOperator, tableName, columnName, value);
    }
    
    public String toString() {
        return String.format(operator.getTemplate(), tableName+"."+columnName);
    }
}
