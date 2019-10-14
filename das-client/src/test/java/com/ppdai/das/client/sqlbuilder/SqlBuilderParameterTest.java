package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.*;
import java.sql.JDBCType;
import java.util.List;
import static com.ppdai.das.client.SegmentConstants.*;
import org.junit.Test;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.sqlbuilder.Person.PersonDefinition;

public class SqlBuilderParameterTest {

    @Test
    public void testColumnParameter( ) {
        SqlBuilder builder = new SqlBuilder();
        PersonDefinition p = Person.PERSON;
        builder.where(p.CityID.equal(1), AND, p.CountryID.greaterThan(2), OR, p.Name.like(null).nullable());
        List<Parameter> params =  builder.buildParameters();
        assertEquals(2, params.size());
        
        assertX(params.get(0), "CityID", JDBCType.INTEGER, 1);
        assertX(params.get(1), "CountryID", JDBCType.INTEGER, 2);
    }
    
    @Test
    public void testTemplateParameter( ) {
        SqlBuilder builder = new SqlBuilder();
        try {
            builder.append(template("ABC", set(JDBCType.INTEGER, 1)));
            fail();
        } catch (Exception e) {
        }
        
        builder = new SqlBuilder();
        builder.append(template("ABC? ?", set(JDBCType.INTEGER, 1), set("b", JDBCType.BIT, 2)));
        List<Parameter> params = builder.buildParameters();
        assertEquals(2, params.size());
        
        assertX(params.get(0), "", JDBCType.INTEGER, 1);
        assertX(params.get(1), "b", JDBCType.BIT, 2);
    }
    
    private void assertX(Parameter param, String name, JDBCType type, Object value) {
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        assertEquals(value, param.getValue());

    }
}
