package com.ppdai.das.client;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.ppdai.das.client.sqlbuilder.AbstractColumn;
import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.ParameterDefinitionProvider;
import com.ppdai.das.core.enums.ParameterDirection;

public class ParameterDefinition implements Segment, ParameterDefinitionProvider {
    private static final String PLACE_HOLDER = "?";

    protected ParameterDirection direction;

    private String tableName;
    protected String name;
    private JDBCType type;
    protected boolean inValues;

    public ParameterDefinition(){}

    public ParameterDefinition(ParameterDirection direction, String name, JDBCType type, boolean inValues) {
        this.direction = direction;
        this.name = name;
        this.type = type;
        this.inValues = inValues;
    }

    public ParameterDefinition(ParameterDirection direction, AbstractColumn column, boolean inValues) {
        this(direction, column.getColumnName(), column.getType(), inValues);
        this.tableName = column.getTableName(new DefaultBuilderContext());
    }

     public ParameterDirection getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public JDBCType getType() {
        return type;
    }

    public boolean isInValues() {
        return inValues;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public Parameter createParameter(Object value) {
        if(isInValues())
            throw new IllegalArgumentException("To create IN parameter by definition, List<?> must be provided");

        return new Parameter(direction, name, type, value);
    }
    
    public Parameter createParameter(List<?> values) {
        if(!isInValues())
            throw new IllegalArgumentException("To create parameter from definition that is not for IN, Object value must be provided");

        return new Parameter(name, type, values);
    }

    public ParameterDefinition setInValues(boolean inValues) {
        this.inValues = inValues;
        return this;
    }
    
    public static List<Parameter> bind(List<ParameterDefinition> defList, Object[] values) {
        int i = 0;
        List<Parameter> parameters = new ArrayList<>();
        for (ParameterDefinition definition : defList) {
            parameters.add(definition.createParameter(definition.getDirection() == ParameterDirection.Output ? null : values[i++]));
        }

        return Parameter.reindex(parameters);
    }

    /**
     * This is used when user specify VAR as definition and applies to a column
     * @param column
     * @return
     */
    public static ParameterDefinition defineByVAR(ParameterDefinition def, AbstractColumn column, boolean isInValues) {
        if(SegmentConstants.VAR == def)
            return builder().type(column.getType()).name(SegmentConstants.VAR.getName()).inValues(isInValues).build();
        
        if(def.type != column.getType() || def.isInValues() != isInValues)
            throw new IllegalArgumentException("The JDBCType of parameter definition or isInValues does not match the column type");

        return def;
    }

    public static class Builder {
        private ParameterDirection direction = ParameterDirection.Input;
        private String name;
        private JDBCType type;
        private boolean inValues;
        
        public Builder direction(ParameterDirection direction) {
            this.direction = Objects.requireNonNull(direction);
            return this;
        }
        public Builder name(String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }
        public Builder type(JDBCType type) {
            this.type = Objects.requireNonNull(type);
            return this;
        }
        public Builder inValues(boolean inValues) {
            this.inValues = inValues;
            return this;
        }

        public boolean isNameSet() {
            return name != null;
        }

        public boolean isTypeSet() {
            return type != null;
        }

        public ParameterDefinition build() {
            return new ParameterDefinition(direction, name, type, inValues);
        }
        
        public ParameterDefinition of(AbstractColumn column) {
            if(!isNameSet())
                name(column.getColumnName());
            
            if(isTypeSet() && column.getType() != type)
                throw new IllegalArgumentException("The JDBCType of parameter definition is not the same with column type");
            else
                type(column.getType());
            
            return build();
        }
    }
    
    public static ParameterDefinition var(String name, JDBCType type) {return new ParameterDefinition(ParameterDirection.Input, name, type, false);}

    public static ParameterDefinition inVar(String name, JDBCType type) {return new ParameterDefinition(ParameterDirection.Input, name, type, true);}
    
    public static ParameterDefinition bitVar(String name) {return var(name, JDBCType.BIT);}
    
    public static ParameterDefinition  tinyintVar(String name) {return var(name, JDBCType.TINYINT);}
    
    public static ParameterDefinition smallintVar(String name) {return var(name, JDBCType.SMALLINT);}
    
    public static ParameterDefinition integerVar(String name) {return var(name, JDBCType.INTEGER);}
    
    public static ParameterDefinition bigintVar(String name) {return var(name, JDBCType.BIGINT);}
    
    public static ParameterDefinition floatVar(String name) {return var(name, JDBCType.FLOAT);}
    
    public static ParameterDefinition realVar(String name) {return var(name, JDBCType.REAL);}
    
    public static ParameterDefinition doubleVar(String name) {return var(name, JDBCType.DOUBLE);}
    
    public static ParameterDefinition numericVar(String name) {return var(name, JDBCType.NUMERIC);}
    
    public static ParameterDefinition decimalVar(String name) {return var(name, JDBCType.DECIMAL);}
    
    public static ParameterDefinition charVar(String name) {return var(name, JDBCType.CHAR);}
    
    public static ParameterDefinition varcharVar(String name) {return var(name, JDBCType.VARCHAR);}
    
    public static ParameterDefinition longvarcharVar(String name) {return var(name, JDBCType.LONGVARCHAR);}
    
    public static ParameterDefinition dateVar(String name) {return var(name, JDBCType.DATE);}
    
    public static ParameterDefinition timeVar(String name) {return var(name, JDBCType.TIME);}
    
    public static ParameterDefinition timestampVar(String name) {return var(name, JDBCType.TIMESTAMP);}
    
    public static ParameterDefinition binaryVar(String name) {return var(name, JDBCType.BINARY);}
    
    public static ParameterDefinition varbinaryVar(String name) {return var(name, JDBCType.VARBINARY);}
    
    public static ParameterDefinition longvarbinaryVar(String name) {return var(name, JDBCType.LONGVARBINARY);}
    
    public static ParameterDefinition nullVar(String name) {return var(name, JDBCType.NULL);}
    
    public static ParameterDefinition blobVar(String name) {return var(name, JDBCType.BLOB);}
    
    public static ParameterDefinition clobVar(String name) {return var(name, JDBCType.CLOB);}
    
    public static ParameterDefinition booleanVar(String name) {return var(name, JDBCType.BOOLEAN);}
    
    public static ParameterDefinition ncharVar(String name) {return var(name, JDBCType.NCHAR);}
    
    public static ParameterDefinition nvarcharVar(String name) {return var(name, JDBCType.NVARCHAR);}
    
    public static ParameterDefinition longnvarcharVar(String name) {return var(name, JDBCType.LONGNVARCHAR);}
    
    public static ParameterDefinition nclobVar(String name) {return var(name, JDBCType.NCLOB);}
    
    public static ParameterDefinition sqlxmlVar(String name) {return var(name, JDBCType.SQLXML);}
    
    public static ParameterDefinition timeWithTimezoneVar(String name) {return var(name, JDBCType.TIME_WITH_TIMEZONE);}
    
    public static ParameterDefinition timestampWithTimezoneVar(String name) {return var(name, JDBCType.TIMESTAMP_WITH_TIMEZONE);}
    
    // For IN(?) definition with common types

    public static ParameterDefinition integerInVar(String name) {return inVar(name, JDBCType.INTEGER);}
    
    public static ParameterDefinition bigintInVar(String name) {return inVar(name, JDBCType.BIGINT);}
    
    public static ParameterDefinition floatInVar(String name) {return inVar(name, JDBCType.FLOAT);}
    
    public static ParameterDefinition realInVar(String name) {return inVar(name, JDBCType.REAL);}
    
    public static ParameterDefinition doubleInVar(String name) {return inVar(name, JDBCType.DOUBLE);}
    
    public static ParameterDefinition numericInVar(String name) {return inVar(name, JDBCType.NUMERIC);}
    
    public static ParameterDefinition decimalInVar(String name) {return inVar(name, JDBCType.DECIMAL);}
    
    public static ParameterDefinition charInVar(String name) {return inVar(name, JDBCType.CHAR);}
    
    public static ParameterDefinition varcharInVar(String name) {return inVar(name, JDBCType.VARCHAR);}
    
    public static ParameterDefinition ncharInVar(String name) {return inVar(name, JDBCType.NCHAR);}
    
    public static ParameterDefinition nvarcharInVar(String name) {return inVar(name, JDBCType.NVARCHAR);}

    @Override
    public String build(BuilderContext context) {
        return PLACE_HOLDER;
    }

    public String toString() {
        return name + "=?";
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        return Arrays.asList(new ParameterDefinition[] {this});
    }
}
