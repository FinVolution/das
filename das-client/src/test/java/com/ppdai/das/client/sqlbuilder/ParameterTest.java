package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.core.enums.ParameterDirection;

public class ParameterTest {
    @Test
    public void testConstructor( ) {
        String name = "name";
        ParameterDirection dir = ParameterDirection.Input;
        JDBCType type = JDBCType.INTEGER;
        
        Parameter p = new Parameter(dir, name, type, 1);
        assertX(p, dir, name, type, 1, false);
        
        p = new Parameter(ParameterDirection.Output, name, type, 1);
        assertX(p, ParameterDirection.Output, name, type, 1, false);
        
        p = new Parameter(ParameterDirection.InputOutput, name, type, 1);
        assertX(p, ParameterDirection.InputOutput, name, type, 1, false);
        
        List<Integer> vl = new ArrayList<>();
        
        p = new Parameter(name, type, vl);
        assertX(p, dir, name, type, vl, true);
    }

    @Test
    public void testStatic( ) {
        String name = "name";
        ParameterDirection dir = ParameterDirection.Input;
        JDBCType type = JDBCType.INTEGER;
        
        assertX(Parameter.input(name, type, 1), ParameterDirection.Input, name, type, 1, false);
        assertX(Parameter.inputOutput(name, type, 1), ParameterDirection.InputOutput, name, type, 1, false);
        assertX(Parameter.output(name, type), ParameterDirection.Output, name, type, null, false);
        
    }
    
    @Test
    public void testVarX() {
        String name = "name";
        Object value = 10;
        List<?> values = new ArrayList<>();;
        
        assertX(Parameter.bitOf(name, value), name, JDBCType.BIT, value);
        
        assertX(Parameter. tinyintOf(name, value), name, JDBCType.TINYINT, value);
        
        assertX(Parameter.smallintOf(name, value), name, JDBCType.SMALLINT, value);
        
        assertX(Parameter.integerOf(name, value), name, JDBCType.INTEGER, value);
        
        assertX(Parameter.bigintOf(name, value), name, JDBCType.BIGINT, value);
        
        assertX(Parameter.floatOf(name, value), name, JDBCType.FLOAT, value);
        
        assertX(Parameter.realOf(name, value), name, JDBCType.REAL, value);
        
        assertX(Parameter.doubleOf(name, value), name, JDBCType.DOUBLE, value);
        
        assertX(Parameter.numericOf(name, value), name, JDBCType.NUMERIC, value);
        
        assertX(Parameter.decimalOf(name, value), name, JDBCType.DECIMAL, value);
        
        assertX(Parameter.charOf(name, value), name, JDBCType.CHAR, value);
        
        assertX(Parameter.varcharOf(name, value), name, JDBCType.VARCHAR, value);
        
        assertX(Parameter.longvarcharOf(name, value), name, JDBCType.LONGVARCHAR, value);
        
        assertX(Parameter.dateOf(name, value), name, JDBCType.DATE, value);
        
        assertX(Parameter.timeOf(name, value), name, JDBCType.TIME, value);
        
        assertX(Parameter.timestampOf(name, value), name, JDBCType.TIMESTAMP, value);
        
        assertX(Parameter.binaryOf(name, value), name, JDBCType.BINARY, value);
        
        assertX(Parameter.varbinaryOf(name, value), name, JDBCType.VARBINARY, value);
        
        assertX(Parameter.longvarbinaryOf(name, value), name, JDBCType.LONGVARBINARY, value);
        
        assertX(Parameter.nullOf(name, value), name, JDBCType.NULL, value);
        
        assertX(Parameter.blobOf(name, value), name, JDBCType.BLOB, value);
        
        assertX(Parameter.clobOf(name, value), name, JDBCType.CLOB, value);
        
        assertX(Parameter.booleanOf(name, value), name, JDBCType.BOOLEAN, value);
        
        assertX(Parameter.ncharOf(name, value), name, JDBCType.NCHAR, value);
        
        assertX(Parameter.nvarcharOf(name, value), name, JDBCType.NVARCHAR, value);
        
        assertX(Parameter.longnvarcharOf(name, value), name, JDBCType.LONGNVARCHAR, value);
        
        assertX(Parameter.nclobOf(name, value), name, JDBCType.NCLOB, value);
        
        assertX(Parameter.sqlxmlOf(name, value), name, JDBCType.SQLXML, value);
        
        assertX(Parameter.timeWithTimezoneOf(name, value), name, JDBCType.TIME_WITH_TIMEZONE, value);
        
        assertX(Parameter.timestampWithTimezoneOf(name, value), name, JDBCType.TIMESTAMP_WITH_TIMEZONE, value);
        
        // For IN(?) definition with common types

        assertX(Parameter.integerOf(name, values), name, JDBCType.INTEGER, values);
        
        assertX(Parameter.bigintOf(name, values), name, JDBCType.BIGINT, values);
        
        assertX(Parameter.floatOf(name, values), name, JDBCType.FLOAT, values);
        
        assertX(Parameter.realOf(name, values), name, JDBCType.REAL, values);
        
        assertX(Parameter.doubleOf(name, values), name, JDBCType.DOUBLE, values);
        
        assertX(Parameter.numericOf(name, values), name, JDBCType.NUMERIC, values);
        
        assertX(Parameter.decimalOf(name, values), name, JDBCType.DECIMAL, values);
        
        assertX(Parameter.charOf(name, values), name, JDBCType.CHAR, values);
        
        assertX(Parameter.varcharOf(name, values), name, JDBCType.VARCHAR, values);
        
        assertX(Parameter.ncharOf(name, values), name, JDBCType.NCHAR, values);
        
        assertX(Parameter.nvarcharOf(name, values), name, JDBCType.NVARCHAR, values);
    }

    private void assertX(Parameter param, String name, JDBCType type, Object value) {
        assertX(param, ParameterDirection.Input, name, type, value, false);
    }
    
    private void assertX(Parameter param, String name, JDBCType type, List<?> values) {
        assertX(param, ParameterDirection.Input, name, type, values, true);
    }
    
    private void assertX(Parameter param, ParameterDirection dir, String name, JDBCType type, Object value, boolean inValues) {
        assertEquals(dir, param.getDirection());
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        if(inValues)
            assertEquals(value, param.getValues());
        else
            assertEquals(value, param.getValue());

        assertEquals(inValues, param.isInValues());
    }
}
