package com.ppdai.das.client;

import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.ppdai.das.client.sqlbuilder.AbstractColumn;
import com.ppdai.das.client.sqlbuilder.ParameterProvider;
import com.ppdai.das.core.DalResultSetExtractor;
import com.ppdai.das.core.enums.ParameterDirection;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

public class Parameter extends ParameterDefinition implements Segment, ParameterProvider, Comparable<Parameter>, ConditionProvider {
    private int index;

    private boolean sensitive;

    //Single value and value list
    private Object value;

    private boolean resultsParameter;

    private DalResultSetExtractor<?> resultSetExtractor;

    public Parameter(String name, JDBCType type, Object value) {
        this(ParameterDirection.Input, name, type, value);
    }

    public Parameter(String name, JDBCType type, List<?> values) {
        super(ParameterDirection.Input, name, type, true);
        this.value = values;
    }

    public Parameter(AbstractColumn column, Object value) {
        super(ParameterDirection.Input, column, false);
        this.value = value;
    }

    public Parameter(AbstractColumn column, List<?> values) {
        super(ParameterDirection.Input, column, true);
        this.value = values;
    }

    public Parameter(ParameterDirection direction, String name, JDBCType type, Object value) {
        super(direction, name, type, false);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public Parameter setValues(List<?> values) {
        this.value = values;
        return this;
    }

    /**
     * Only for output value for store procedure call
     * @param value
     */
    public void setOutputValue(Object value) {
        this.value = value;
    }

    public List<?> getValues() {
        if(value != null && value instanceof List){
            return (List)value;
        } else {
            return null;
        }
    }
    
    public static Parameter input(String name, JDBCType type, Object value) {
        return new Parameter(ParameterDirection.Input, name, type, value);
    }

    public static  Parameter output(String name, JDBCType type) {
        return new Parameter(ParameterDirection.Output, name, type, null);
    }

    public static  Parameter inputOutput(String name, JDBCType type, Object value) {
        return new Parameter(ParameterDirection.InputOutput, name, type, value);
    }

    @Override
    public List<Parameter> buildParameters() {
        return Arrays.asList(new Parameter[] {this});
    }

    @Override
    public Condition build() {
        return new ColumnCondition(OperatorEnum.EQUAL, getTableName(), getName(), value);
    }


    public String toString() {
        return name + "=" + value;
    }

    public static Parameter inParameter(AbstractColumn column, List<?> values) {return new Parameter(column, values);}

    public static Parameter inParameter(String name, JDBCType type, List<?> values) {return new Parameter(name, type, values);}
    
    public static Parameter bitOf(String name, Object value) {return input(name, JDBCType.BIT, value);}
    
    public static Parameter  tinyintOf(String name, Object value) {return input(name, JDBCType.TINYINT, value);}
    
    public static Parameter smallintOf(String name, Object value) {return input(name, JDBCType.SMALLINT, value);}
    
    public static Parameter integerOf(String name, Object value) {return input(name, JDBCType.INTEGER, value);}
    
    public static Parameter bigintOf(String name, Object value) {return input(name, JDBCType.BIGINT, value);}
    
    public static Parameter floatOf(String name, Object value) {return input(name, JDBCType.FLOAT, value);}
    
    public static Parameter realOf(String name, Object value) {return input(name, JDBCType.REAL, value);}
    
    public static Parameter doubleOf(String name, Object value) {return input(name, JDBCType.DOUBLE, value);}
    
    public static Parameter numericOf(String name, Object value) {return input(name, JDBCType.NUMERIC, value);}
    
    public static Parameter decimalOf(String name, Object value) {return input(name, JDBCType.DECIMAL, value);}
    
    public static Parameter charOf(String name, Object value) {return input(name, JDBCType.CHAR, value);}
    
    public static Parameter varcharOf(String name, Object value) {return input(name, JDBCType.VARCHAR, value);}
    
    public static Parameter longvarcharOf(String name, Object value) {return input(name, JDBCType.LONGVARCHAR, value);}
    
    public static Parameter dateOf(String name, Object value) {return input(name, JDBCType.DATE, value);}
    
    public static Parameter timeOf(String name, Object value) {return input(name, JDBCType.TIME, value);}
    
    public static Parameter timestampOf(String name, Object value) {return input(name, JDBCType.TIMESTAMP, value);}
    
    public static Parameter binaryOf(String name, Object value) {return input(name, JDBCType.BINARY, value);}
    
    public static Parameter varbinaryOf(String name, Object value) {return input(name, JDBCType.VARBINARY, value);}
    
    public static Parameter longvarbinaryOf(String name, Object value) {return input(name, JDBCType.LONGVARBINARY, value);}
    
    public static Parameter nullOf(String name, Object value) {return input(name, JDBCType.NULL, value);}
    
    public static Parameter blobOf(String name, Object value) {return input(name, JDBCType.BLOB, value);}
    
    public static Parameter clobOf(String name, Object value) {return input(name, JDBCType.CLOB, value);}
    
    public static Parameter booleanOf(String name, Object value) {return input(name, JDBCType.BOOLEAN, value);}
    
    public static Parameter ncharOf(String name, Object value) {return input(name, JDBCType.NCHAR, value);}
    
    public static Parameter nvarcharOf(String name, Object value) {return input(name, JDBCType.NVARCHAR, value);}
    
    public static Parameter longnvarcharOf(String name, Object value) {return input(name, JDBCType.LONGNVARCHAR, value);}
    
    public static Parameter nclobOf(String name, Object value) {return input(name, JDBCType.NCLOB, value);}
    
    public static Parameter sqlxmlOf(String name, Object value) {return input(name, JDBCType.SQLXML, value);}
    
    public static Parameter timeWithTimezoneOf(String name, Object value) {return input(name, JDBCType.TIME_WITH_TIMEZONE, value);}
    
    public static Parameter timestampWithTimezoneOf(String name, Object value) {return input(name, JDBCType.TIMESTAMP_WITH_TIMEZONE, value);}
    
    // For IN(?) definition with common types

    public static Parameter integerOf(String name, List<?> values) {return inParameter(name, JDBCType.INTEGER, values);}
    
    public static Parameter bigintOf(String name, List<?> values) {return inParameter(name, JDBCType.BIGINT, values);}
    
    public static Parameter floatOf(String name, List<?> values) {return inParameter(name, JDBCType.FLOAT, values);}
    
    public static Parameter realOf(String name, List<?> values) {return inParameter(name, JDBCType.REAL, values);}
    
    public static Parameter doubleOf(String name, List<?> values) {return inParameter(name, JDBCType.DOUBLE, values);}
    
    public static Parameter numericOf(String name, List<?> values) {return inParameter(name, JDBCType.NUMERIC, values);}
    
    public static Parameter decimalOf(String name, List<?> values) {return inParameter(name, JDBCType.DECIMAL, values);}
    
    public static Parameter charOf(String name, List<?> values) {return inParameter(name, JDBCType.CHAR, values);}
    
    public static Parameter varcharOf(String name, List<?> values) {return inParameter(name, JDBCType.VARCHAR, values);}
    
    public static Parameter ncharOf(String name, List<?> values) {return inParameter(name, JDBCType.NCHAR, values);}
    
    public static Parameter nvarcharOf(String name, List<?> values) {return inParameter(name, JDBCType.NVARCHAR, values);}

    public Parameter clone() {
        Parameter clone = isInValues() ? new Parameter(getName(), getType(), getValues()):new Parameter(getName(), getType(), getValue());
        clone.direction = direction;
        clone.index = index;
        return clone;
        
    }

    /**
     * Expand in parameters if necessary. This must be executed before execution
     */
    public static void compile(List<Parameter> parameters) {
        if(!containsInParameter(parameters))
            return;

        //To be safe, order parameters by original index
        Collections.sort(parameters);

        // Make a copy of original parameters
        List<Parameter> tmpParameters = new LinkedList<>(parameters);

        // The change will be made into original parameters
        int i = 0;
        for(Parameter p: tmpParameters) {
            if(p.isInParam()) {
                // Remove the original
                parameters.remove(p);
                List<?> values = (List)p.getValue();
                for(Object val : values) {
                    parameters.add(i, new Parameter(p.getName(), p.getType(), val).setIndex(++i));
                }
            }else {
                p.setIndex(++i);
            }
        }
    }
    
    public static List<Parameter> reindex(List<Parameter> parameters) {
        int index = 1;
        for (Parameter parameter : parameters)
            parameter.setIndex(index++);
        return parameters;
    }

    public static List<Parameter> duplicate(List<Parameter> parameters) {
        return parameters.stream().map(para -> para.clone()).collect(Collectors.toList());
    }

    public static boolean containsInParameter(List<Parameter> parameters) {
        for(Parameter p: parameters)
            if(p.isInParam())
                return true;
        return false;
    }

    @Override
    public int compareTo(Parameter o) {
        return this.index - o.index;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public boolean isInParam() {
        return inValues;
    }

    public boolean isResultsParameter() {
        return resultsParameter;
    }

    public DalResultSetExtractor<?> getResultSetExtractor() {
        return resultSetExtractor;
    }

    public boolean isInputParameter() {
        if(isResultsParameter())
            return false;
        return direction == ParameterDirection.Input || direction == ParameterDirection.InputOutput;
    }

    public boolean isOutParameter() {
        if(resultsParameter || direction == null)
            return false;
        return direction == ParameterDirection.Output || direction == ParameterDirection.InputOutput;
    }

    public Parameter setDirection(ParameterDirection direction) {
        this.direction = direction;
        return this;
    }

    public Parameter setName(String name) {
        this.name = name;
        return this;
    }

    public Parameter setIndex(int index) {
        this.index = index;
        return this;
    }

    public Parameter setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
        return this;
    }

    public Parameter setValue(Object value) {
        this.value = value;
        return this;
    }
}
