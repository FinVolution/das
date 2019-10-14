package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.SegmentConstants.*;
import static com.ppdai.das.client.sqlbuilder.Person.PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ppdai.das.core.enums.ParameterDirection;
import org.junit.Test;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;

import com.ppdai.das.client.SqlBuilder;

public class SegmentConstantsTest {
    private BuilderContext ctx = new DefaultBuilderContext();
    private String var = "var1";
    @Test
    public void testCustomizedColumns( ) {
        SqlBuilder sb = new SqlBuilder();
        CustomizedColumn c = column("count()");
        sb.append(c).orderBy(c.asc(), c.desc());
        assertEquals("count() ORDER BY count() ASC, count() DESC", build(sb));
    }

    @Test
    public void testCustomizedExpression( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(expression("exp"), expression("exp1").when(false));
        assertEquals("exp", build(sb));
    }
    
    @Test
    public void testVar( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(var(var).type(JDBCType.INTEGER).build());
        assertEquals("?", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        ParameterDefinition p = pl.get(0);
        assertX(pl.get(0), var, JDBCType.INTEGER, false);
    }

    @Test
    public void testVar1( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(var(JDBCType.INTEGER));
        assertEquals("?", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        ParameterDefinition p = pl.get(0);
        assertX(pl.get(0), VAR.getName(), JDBCType.INTEGER, false);
        
    }
    
    @Test
    public void testVar2( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(var(var, JDBCType.INTEGER));
        assertEquals("?", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        ParameterDefinition p = pl.get(0);
        assertX(pl.get(0), var, JDBCType.INTEGER, false);
        
    }
    
    @Test
    public void testInVar( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(inVar(var, JDBCType.INTEGER));
        assertEquals("?", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        ParameterDefinition p = pl.get(0);
        assertX(pl.get(0), var, JDBCType.INTEGER, true);
        
    }
    
    @Test
    public void testInVar1( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(inVar(JDBCType.INTEGER));
        assertEquals("?", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        ParameterDefinition p = pl.get(0);
        assertX(pl.get(0), VAR.getName(), JDBCType.INTEGER, true);
        
    }
    
    @Test
    public void testConcat( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(concatWith(text("|"), "A", "B", "C"));
        assertEquals("A | B | C", build(sb));
    }

    @Test
    public void testComma( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(comma("A", "B", "C"));
        assertEquals("A, B, C", build(sb));
    }

    @Test
    public void testSet( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(template("ABC ? ?", set("a", JDBCType.INTEGER, 1), set("b", JDBCType.BIGINT, 2)));
        assertEquals("ABC ? ?", build(sb));
        List<Parameter> pl = sb.buildParameters();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "a", JDBCType.INTEGER, 1);        
        assertX(pl.get(1), "b", JDBCType.BIGINT, 2);
        
        //Test no name
        sb = new SqlBuilder();
        sb.append(template("ABC ? ?", set(JDBCType.INTEGER, 1), set(JDBCType.BIGINT, 2)));
        assertEquals("ABC ? ?", build(sb));
        pl = sb.buildParameters();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "", JDBCType.INTEGER, 1);        
        assertX(pl.get(1), "", JDBCType.BIGINT, 2);
        
        //Test IN name
        List<?> values1 = Arrays.asList(new Integer[] {1, 2, 3});
        List<?> values2 = Arrays.asList(new Integer[] {4, 5, 6});
        
        sb = new SqlBuilder();
        sb.append(template("ABC ? ?", set("A", JDBCType.INTEGER, values1), set("B", JDBCType.BIGINT, values2)));
        assertEquals("ABC ? ?", build(sb));
        pl = sb.buildParameters();
        assertEquals(2, pl.size());
        assertInX(pl.get(0), "A", JDBCType.INTEGER, values1);        
        assertInX(pl.get(1), "B", JDBCType.BIGINT, values2);
        
        //Test IN no name
        sb = new SqlBuilder();
        sb.append(template("ABC ? ?", set(JDBCType.INTEGER, values1), set(JDBCType.BIGINT, values2)));
        assertEquals("ABC ? ?", build(sb));
        pl = sb.buildParameters();
        assertEquals(2, pl.size());
        assertInX(pl.get(0), "", JDBCType.INTEGER, values1);        
        assertInX(pl.get(1), "", JDBCType.BIGINT, values2);
    }

    @Test
    public void testTemplate( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(template("ABC ? ?", set("a", JDBCType.INTEGER, 1), set("b", JDBCType.BIGINT, 2)));
        assertEquals("ABC ? ?", build(sb));
        List<Parameter> pl = sb.buildParameters();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "a", JDBCType.INTEGER, 1);        
        assertX(pl.get(1), "b", JDBCType.BIGINT, 2);
    }

    @Test
    public void testTemplateDef( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.append(template("ABC ? IN ( ? )", var("a", JDBCType.INTEGER), inVar("b", JDBCType.BIGINT)));
        assertEquals("ABC ? IN ( ? )", build(sb));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "a", JDBCType.INTEGER, false);        
        assertX(pl.get(1), "b", JDBCType.BIGINT, true);
    }
    
    @Test
    public void testBracketsFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(bracket(bracket(), bracket())).build(ctx));
        
        
        assertEquals("", new SqlBuilder().append(bracket(bracket(bracket(), bracket()), bracket())).build(ctx));
        
        assertEquals("(AAA)", new SqlBuilder().append(bracket("AAA")).build(ctx));
        assertEquals("(AAA BBB)", new SqlBuilder().append(bracket("AAA", "BBB")).build(ctx));
        assertEquals("(AAA person.CityID)", new SqlBuilder().append(bracket("AAA", PERSON.CityID)).build(ctx));
        assertEquals("(AAA p.CityID)", new SqlBuilder().append(bracket("AAA", PERSON.as("p").CityID)).build(ctx));
    }

    @Test
    public void testAllOfFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(allOf()).build(ctx));
        assertEquals("(aaa)", new SqlBuilder().append(allOf(expression("aaa"))).build(ctx));
        assertEquals("(aaa AND bbb)", new SqlBuilder().append(allOf(expression("aaa"), expression("bbb"))).build(ctx));
        assertEquals("(aaa AND bbb AND ccc)", new SqlBuilder().append(allOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
        assertEquals("(aaa AND bbb AND ccc) (aaa AND bbb AND ccc)", new SqlBuilder().append(allOf(expression("aaa"), expression("bbb"), expression("ccc")), allOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
    }

    @Test
    public void testAnyOfFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(anyOf()).build(ctx));
        assertEquals("(aaa)", new SqlBuilder().append(anyOf(expression("aaa"))).build(ctx));
        assertEquals("(aaa OR bbb)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"))).build(ctx));
        assertEquals("(aaa OR bbb OR ccc)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
        assertEquals("(aaa OR bbb OR ccc) (aaa OR bbb OR ccc)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"), expression("ccc")), anyOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
    }

    private void assertX(ParameterDefinition param, String name, JDBCType type, boolean inValues) {
        assertEquals(ParameterDirection.Input, param.getDirection());
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        assertEquals(inValues, param.isInValues());
    }
    
    private void assertX(Parameter param, String name, JDBCType type, Object value) {
        assertX(param, name, type, false); 
        assertEquals(param.getValue(), value);
    }

    private void assertInX(Parameter param, String name, JDBCType type, Object value) {
        assertX(param, name, type, true); 
        assertEquals(param.getValues(), value);
    }

    private String build(SqlBuilder builder) {
        return builder.build(new DefaultBuilderContext());
    }
}
