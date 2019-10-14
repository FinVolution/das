package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;
import com.ppdai.das.core.enums.ParameterDirection;

public class ParameterDefinitionTest {
    @Test
    public void testConstructor() {
        ParameterDefinition d;
        
        d = new ParameterDefinition(ParameterDirection.Input, "name", JDBCType.INTEGER, true);
        assertX(d, "name", JDBCType.INTEGER, true);
        
        d = new ParameterDefinition(ParameterDirection.Input, "name", JDBCType.INTEGER, false);
        assertX(d, "name", JDBCType.INTEGER, false);
    }
    
    @Test
    public void testCreateParameter() {
        ParameterDefinition d;
        Parameter p;
        
        d = new ParameterDefinition(ParameterDirection.Input, "name", JDBCType.INTEGER, true);
        try {
            p = d.createParameter(1);
            fail();
        }catch (Exception e) {}

        List<Integer> vl = new ArrayList<>();
        p = d.createParameter(vl);
        assertX(p, "name", JDBCType.INTEGER, vl, true);
        
        d = new ParameterDefinition(ParameterDirection.Input, "name", JDBCType.INTEGER, false);
        try {
            p = d.createParameter(vl);
            fail();
        }catch (Exception e) {}

        p = d.createParameter(1);
        assertX(p, "name", JDBCType.INTEGER, 1, false);
        
    }
    
    @Test
    public void testBuilder() {
        ParameterDefinition.Builder b = ParameterDefinition.builder();
        
        try {
            b.name(null);
            fail();
        }catch (Exception e) {}
        
        b.name("name");
        try {
            b.type(null);
            fail();
        }catch (Exception e) {}
        b.type(JDBCType.INTEGER);
        
        try {
            b.direction(null);
            fail();
        }catch (Exception e) {}
        b.direction(ParameterDirection.Input);
        
        assertX(b.build(), "name", JDBCType.INTEGER, false);
        b.inValues(true);
        assertX(b.build(), "name", JDBCType.INTEGER, true);
    }
    
    private void assertX(ParameterDefinition param, String name, JDBCType type, boolean inValues) {
        assertEquals(ParameterDirection.Input, param.getDirection());
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        assertEquals(inValues, param.isInValues());
    }
    
    private void assertX(Parameter param, String name, JDBCType type, Object value, boolean inValues) {
        assertEquals(ParameterDirection.Input, param.getDirection());
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        if(inValues)
            assertEquals(value, param.getValues());
        else
            assertEquals(value, param.getValue());

        assertEquals(inValues, param.isInValues());
    }
    
    @Test
    public void testVar() {
        ParameterDefinition d = ParameterDefinition.var("name", JDBCType.INTEGER);
        assertX(d, "name", JDBCType.INTEGER, false);        

        d = ParameterDefinition.inVar("name", JDBCType.INTEGER);
        assertX(d, "name", JDBCType.INTEGER, true);        
    }
    
    @Test
    public void testVarX() {
        String name = "name";
        assertX(ParameterDefinition.bitVar(name), name, JDBCType.BIT, false);
        
        assertX(ParameterDefinition.tinyintVar(name), name, JDBCType.TINYINT, false);
        
        assertX(ParameterDefinition.smallintVar(name), name, JDBCType.SMALLINT, false);
        
        assertX(ParameterDefinition.integerVar(name), name, JDBCType.INTEGER, false);
        
        assertX(ParameterDefinition.bigintVar(name), name, JDBCType.BIGINT, false);
        
        assertX(ParameterDefinition.floatVar(name), name, JDBCType.FLOAT, false);
        
        assertX(ParameterDefinition.realVar(name), name, JDBCType.REAL, false);
        
        assertX(ParameterDefinition.doubleVar(name), name, JDBCType.DOUBLE, false);
        
        assertX(ParameterDefinition.numericVar(name), name, JDBCType.NUMERIC, false);
        
        assertX(ParameterDefinition.decimalVar(name), name, JDBCType.DECIMAL, false);
        
        assertX(ParameterDefinition.charVar(name), name, JDBCType.CHAR, false);
        
        assertX(ParameterDefinition.varcharVar(name), name, JDBCType.VARCHAR, false);
        
        assertX(ParameterDefinition.longvarcharVar(name), name, JDBCType.LONGVARCHAR, false);
        
        assertX(ParameterDefinition.dateVar(name), name, JDBCType.DATE, false);
        
        assertX(ParameterDefinition.timeVar(name), name, JDBCType.TIME, false);
        
        assertX(ParameterDefinition.timestampVar(name), name, JDBCType.TIMESTAMP, false);
        
        assertX(ParameterDefinition.binaryVar(name), name, JDBCType.BINARY, false);
        
        assertX(ParameterDefinition.varbinaryVar(name), name, JDBCType.VARBINARY, false);
        
        assertX(ParameterDefinition.longvarbinaryVar(name), name, JDBCType.LONGVARBINARY, false);
        
        assertX(ParameterDefinition.nullVar(name), name, JDBCType.NULL, false);
        
        assertX(ParameterDefinition.blobVar(name), name, JDBCType.BLOB, false);
        
        assertX(ParameterDefinition.clobVar(name), name, JDBCType.CLOB, false);
        
        assertX(ParameterDefinition.booleanVar(name), name, JDBCType.BOOLEAN, false);
        
        assertX(ParameterDefinition.ncharVar(name), name, JDBCType.NCHAR, false);
        
        assertX(ParameterDefinition.nvarcharVar(name), name, JDBCType.NVARCHAR, false);
        
        assertX(ParameterDefinition.longnvarcharVar(name), name, JDBCType.LONGNVARCHAR, false);
        
        assertX(ParameterDefinition.nclobVar(name), name, JDBCType.NCLOB, false);
        
        assertX(ParameterDefinition.sqlxmlVar(name), name, JDBCType.SQLXML, false);
        
        assertX(ParameterDefinition.timeWithTimezoneVar(name), name, JDBCType.TIME_WITH_TIMEZONE, false);
        
        assertX(ParameterDefinition.timestampWithTimezoneVar(name), name, JDBCType.TIMESTAMP_WITH_TIMEZONE, false);
        
        // For IN(?) definition with common types

        assertX(ParameterDefinition.integerInVar(name), name, JDBCType.INTEGER, true);
        
        assertX(ParameterDefinition.bigintInVar(name), name, JDBCType.BIGINT, true);
        
        assertX(ParameterDefinition.floatInVar(name), name, JDBCType.FLOAT, true);
        
        assertX(ParameterDefinition.realInVar(name), name, JDBCType.REAL, true);
        
        assertX(ParameterDefinition.doubleInVar(name), name, JDBCType.DOUBLE, true);
        
        assertX(ParameterDefinition.numericInVar(name), name, JDBCType.NUMERIC, true);
        
        assertX(ParameterDefinition.decimalInVar(name), name, JDBCType.DECIMAL, true);
        
        assertX(ParameterDefinition.charInVar(name), name, JDBCType.CHAR, true);
        
        assertX(ParameterDefinition.varcharInVar(name), name, JDBCType.VARCHAR, true);
        
        assertX(ParameterDefinition.ncharInVar(name), name, JDBCType.NCHAR, true);
        
        assertX(ParameterDefinition.nvarcharInVar(name), name, JDBCType.NVARCHAR, true);
    }
}
