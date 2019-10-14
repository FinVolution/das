package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.ParameterDefinition.*;
import static com.ppdai.das.client.SegmentConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.JDBCType;
import java.util.List;

import com.ppdai.das.client.*;


import org.junit.Test;

import com.ppdai.das.client.sqlbuilder.Person.PersonDefinition;

public class SqlBuilderParameterDefinitionTest {

    @Test
    public void testColumnParameter( ) {
        SqlBuilder builder = new SqlBuilder();
        PersonDefinition p = Person.PERSON;
        builder.where(p.CityID.equal(var("a")), AND, p.CountryID.greaterThan(var("b")), OR, p.Name.like(var("c")).when(false));
        List<ParameterDefinition> params = builder.buildDefinitions();
        
        assertEquals(2, params.size());
        
        assertX(params.get(0), "a", JDBCType.INTEGER);
        assertX(params.get(1), "b", JDBCType.INTEGER);
    }
    
    @Test
    public void testTemplateParameter( ) {
        SqlBuilder builder = new SqlBuilder();
        try {
            builder.append(template("ABC", integerVar("a")));
            fail();
        } catch (Exception e) {
        }
        
        builder = new SqlBuilder();
        builder.append(template("ABC? ?", integerVar("a"), bitVar("b")));
        List<ParameterDefinition> params = builder.buildDefinitions();
        assertEquals(2, params.size());
        
        assertX(params.get(0), "a", JDBCType.INTEGER);
        assertX(params.get(1), "b", JDBCType.BIT);

        //IN test
        builder = new SqlBuilder();
        builder.append(template("ABC? ?", integerInVar("a"), ncharInVar("b")));
        params = builder.buildDefinitions();
        assertEquals(2, params.size());
        
        assertX(params.get(0), "a", JDBCType.INTEGER);
        assertX(params.get(1), "b", JDBCType.NCHAR);
        assertTrue(params.get(0).isInValues());
        assertTrue(params.get(1).isInValues());
    }
    
    private void assertX(ParameterDefinition param, String name, JDBCType type) {
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());

    }
}
