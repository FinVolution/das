package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import com.ppdai.das.client.Person;
import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.strategy.ConditionList;

public class ConditionBuilderTest {
    private static PersonDefinition p = Person.PERSON;
    
//    @Test
//    public void testIllegalArgument() {
//        SqlBuilder builder = new SqlBuilder();
//        builder.values(p.Name.of("Jerry"), p.CountryID.of(1));
//        try {
//            builder.buildUpdateConditions();
//            fail();
//        } catch (IllegalArgumentException e) {
//        }
//    }
    
    @Test
    public void testInsertValues() throws SQLException {
        SqlBuilder builder = SqlBuilder.insertInto(p, p.Name, p.CountryID);
        builder.values(p.Name.of("Jerry"), p.CountryID.of(1));
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }

    @Test
    public void testInsertBySelect() throws SQLException {
        SqlBuilder builder = SqlBuilder.insertInto(p);
        builder.append(SqlBuilder.select(p.Name).from(p).where().allOf(p.Name.eq(1), p.CountryID.gt(2)));
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }
    
    @Test
    public void testInsertWithColumnListBySelect() throws SQLException {
        SqlBuilder builder = SqlBuilder.insertInto(p, p.Name, p.CountryID);
        builder.append(SqlBuilder.select(p.Name).from(p).where().allOf(p.Name.eq(1), p.CountryID.gt(2)));
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }
    
    @Test
    public void testInsertWithColumnListBySelectWhereBracket() throws SQLException {
        SqlBuilder builder = SqlBuilder.insertInto(p, p.Name, p.CountryID);
        builder.append(SqlBuilder.select(p.Name).from(p).where().leftBracket().allOf(p.Name.eq(1), p.CountryID.gt(2)).rightBracket());
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }

    @Test
    public void testUpdate() throws SQLException {
        SqlBuilder builder = SqlBuilder.update(p).set(p.Name.eq("Jerry"), p.CountryID.eq(1));
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }

    @Test
    public void testUpdateWhere() throws SQLException {
        SqlBuilder builder = SqlBuilder.update(p).set(p.Name.eq("Jerry"), p.CountryID.eq(1)).
                where().leftBracket().allOf(p.Name.eq(1), p.CountryID.gt(2)).rightBracket();;
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(4, cl.size());
    }

    @Test
    public void testDelete() throws SQLException {
        SqlBuilder builder = SqlBuilder.deleteFrom(p);
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(0, cl.size());
    }

    @Test
    public void testDeleteWhere() throws SQLException {
        SqlBuilder builder = SqlBuilder.deleteFrom(p).where().leftBracket().allOf(p.Name.eq(1), p.CountryID.gt(2)).rightBracket();;
        ConditionList cl = builder.buildUpdateConditions();
        assertTrue(cl.isIntersected());
        assertEquals(2, cl.size());
    }
}
