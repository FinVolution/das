package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.SegmentConstants.inVar;
import static com.ppdai.das.client.SegmentConstants.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ppdai.das.core.enums.ParameterDirection;
import org.junit.Test;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;

import com.ppdai.das.client.Segment;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.client.SqlBuilder;

public abstract class AbstractColumnTest implements SegmentConstants {
    abstract protected AbstractColumn column();
    abstract protected String getReferName();
    abstract protected String getColumnName();
    abstract protected JDBCType getColumnType();
    
    public JDBCType type = getColumnType();
    public static final JDBCType type2 = JDBCType.BIGINT;
    
    private static final Object value = new Integer(10);
    private static final Object nullValue = null;
    private static final List<Object> nullValues = new ArrayList<>();
    private static final List<Object> values = Arrays.asList(1, 2, 3);
    private static final BuilderContext bc = new DefaultBuilderContext();

    @Test
    public void testBuild( ) {
        AbstractColumn c = column();
        assertEquals(getReferName() + " AS c", c.build(new DefaultBuilderContext()));
    }
    
    @Test
    public void testASC( ) {
        ColumnOrder o = column().asc();
        assertEquals(getReferName() + " ASC", o.build(new DefaultBuilderContext()));
    }
    
    @Test
    public void testDESC( ) {
        ColumnOrder o = column().desc();
        assertEquals(getReferName() + " DESC", o.build(new DefaultBuilderContext()));
    }
    
    private void assertFail(Expression exp) {
        try {
            exp.build(bc);
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testExpression() {
        assertExp(getReferName() + " = ?", value -> column().equal(value));
        assertExp(getReferName() + " <> ?", value -> column().notEqual(value));
        assertExp(getReferName() + " > ?", value -> column().greaterThan(value));
        assertExp(getReferName() + " >= ?", value -> column().greaterThanOrEqual(value));
        assertExp(getReferName() + " < ?", value -> column().lessThan(value));
        assertExp(getReferName() + " <= ?", value -> column().lessThanOrEqual(value));

        assertExp(getReferName() + " = ?", value -> column().eq(value));
        assertExp(getReferName() + " <> ?", value -> column().neq(value));
        assertExp(getReferName() + " > ?", value -> column().gt(value));
        assertExp(getReferName() + " >= ?", value -> column().gteq(value));
        assertExp(getReferName() + " < ?", value -> column().lt(value));
        assertExp(getReferName() + " <= ?", value -> column().lteq(value));

        assertExp(getReferName() + " LIKE ?", value -> column().like(value));
        assertExp(getReferName() + " NOT LIKE ?", value -> column().notLike(value));
        assertEquals(getReferName() + " IS NULL", column().isNull().build(bc));
        assertEquals(getReferName() + " IS NOT NULL", column().isNotNull().build(bc));
//      assertExp(getColumnName() + " BETWEEN ? AND ?", value -> column().between(value, value));
//      assertExp(getColumnName() + " NOT BETWEEN ? AND ?", value -> column().notBetween(value, value));
//      assertExp(getColumnName() + " IN ( ? )", value -> column().in(values));
//      assertExp(getColumnName() + " NOT IN ( ? )", value -> column().notIn(values));
    }

    @Test
    public void testBetween() {
        AbstractColumn c = column();
        assertEquals(getReferName() + " BETWEEN ? AND ?", c.between(value, value).build(bc));
        assertBetweenParam(c.between(value, value));
        assertFail(c.between(nullValue, value));
        assertFail(c.between(nullValue, nullValue));
        assertFail(c.between(value, nullValue));
        assertBetweenDef(c.between(var("var1"), var("var2")), "var1", "var2", type);
        
        try {
            c.between(var("var1", type2), var("var2"));
            fail();
        }catch (Exception e) {}

        try {
            c.between(var("var1"), var("var2", type2));
            fail();
        }catch (Exception e) {}

        assertBetweenDef(c.between(VAR, var("var2")), "VAR", "var2", type);
        assertBetweenDef(c.between(var("var1"), VAR), "var1", "VAR", type);
        assertBetweenDef(c.between(VAR, VAR), "VAR", "VAR", type);
    }

    @Test
    public void testNotBetween() {
        AbstractColumn c = column();
        assertEquals(getReferName() + " NOT BETWEEN ? AND ?", column().notBetween(value, value).build(bc));
        assertBetweenParam(c.notBetween(value, value));
        assertFail(c.notBetween(nullValue, value));
        assertFail(c.notBetween(nullValue, nullValue));
        assertFail(c.notBetween(value, nullValue));
        assertBetweenDef(c.notBetween(var("var1"), var("var2")), "var1", "var2", type);

        try {
            c.notBetween(var("var1", type2), var("var2"));
            fail();
        }catch (Exception e) {}

        try {
            c.notBetween(var("var1"), var("var2", type2));
            fail();
        }catch (Exception e) {}

        assertBetweenDef(c.notBetween(VAR, var("var2")), "VAR", "var2", type);
        assertBetweenDef(c.notBetween(var("var1"), VAR), "var1", "VAR", type);
        assertBetweenDef(c.notBetween(VAR, VAR), "VAR", "VAR", type);
    }

    @Test
    public void testIn() {
        AbstractColumn c = column();
        assertEquals(getReferName() + " IN ( ? )", c.in(values).build(bc));
        assertParam(c.in(values));
        assertParam(c.in(Parameter.inParameter(getColumnName(), type, values)));
        assertFail(c.in(nullValues));
        assertExp(c.in(var("var")), "var", type);
        assertExp(c.in(inVar("var", type)), "var", type);
        
        try {
            c.in(inVar("var", type2));
            fail();
        }catch (Exception e) {}

        assertExp(c.in(VAR), "VAR", type);
    }

    @Test
    public void testNotIn() {
        AbstractColumn c = column();
        assertEquals(getReferName() + " NOT IN ( ? )", c.notIn(values).build(bc));
        assertParam(c.notIn(values));
        assertParam(c.notIn(Parameter.inParameter(getColumnName(), type, values)));
        assertFail(c.notIn(nullValues));
        assertExp(c.notIn(var("var")), "var", type);
        assertExp(c.notIn(inVar("var", type)), "var", type);
        
        try {
            c.notIn(inVar("var", type2));
            fail();
        }catch (Exception e) {}

        assertExp(c.notIn(VAR), "VAR", type);
    }

    public void assertParam(Expression exp) {
        if(!(exp instanceof ParameterProvider))
            fail();
        ParameterProvider p = (ParameterProvider)exp;        
        List<Parameter> params = p.buildParameters();
        assertEquals(1, params.size());
        Parameter param = params.get(0);

        assertEquals(getColumnName(), param.getName());
        assertEquals(ParameterDirection.Input, param.getDirection());
        assertEquals(type, param.getType());
        
        if(param.isInValues())
            assertNotNull(param.getValues());
        else
            assertNotNull(param.getValue());
    }

    public void assertBetweenParam(Expression exp) {
        if(!(exp instanceof ParameterProvider))
            fail();
        ParameterProvider p = (ParameterProvider)exp;        
        List<Parameter> params = p.buildParameters();
        assertEquals(2, params.size());
        
        for(Parameter param: params) {
            assertEquals(getColumnName(), param.getName());
            assertEquals(ParameterDirection.Input, param.getDirection());
            assertEquals(type, param.getType());
            assertNotNull(param.getValue());
        }
    }

    private interface ExpProvider {
        Expression create(Object value);
    }
    
    public void assertExp(String tpl, ExpProvider provider) {
        assertEquals(tpl, provider.create(value).build(bc));
        assertParam(provider.create(value));
        assertFail(provider.create(nullValue));

        assertExp(provider.create(var("var")), "var", type);
        
        try {
            provider.create(var(type2));
            fail();
        }catch (Exception e) {}
        
        assertExp(provider.create(var("var", type)), "var", type);
        assertExp(provider.create(VAR), "VAR", type);
    }
        
    public void assertExp(Expression exp, String name, JDBCType type) {
        if(!(exp instanceof ParameterDefinitionProvider))
            fail();
        ParameterDefinitionProvider p = (ParameterDefinitionProvider)exp;        
        List<ParameterDefinition> defs = p.buildDefinitions();
        assertEquals(1, defs.size());
        ParameterDefinition def = defs.get(0);
        
        assertEquals(name, def.getName());
        assertEquals(ParameterDirection.Input, def.getDirection());
        assertEquals(type, def.getType());        
        
        if(exp instanceof InExpression) {
            if(!def.isInValues())
                fail();
        }
        else {
            if(def.isInValues())
                fail();
        }
    }
    
    public void assertBetweenDef(Expression exp, String name1, String name2, JDBCType type) {
        if(!(exp instanceof ParameterDefinitionProvider))
            fail();
        ParameterDefinitionProvider p = (ParameterDefinitionProvider)exp;        
        List<ParameterDefinition> defs = p.buildDefinitions();
        assertEquals(2, defs.size());
        
        assertEquals(name1, defs.get(0).getName());
        assertEquals(ParameterDirection.Input, defs.get(0).getDirection());
        assertEquals(type, defs.get(0).getType());        

        assertEquals(name2, defs.get(1).getName());
        assertEquals(ParameterDirection.Input, defs.get(1).getDirection());
        assertEquals(type, defs.get(1).getType());        
    }

    @Test
    public void testAllNullable() {
        AbstractColumn c = column();
        SqlBuilder sb = new SqlBuilder();
        sb.append(
        c.equal(nullValue).nullable(),
        c.notEqual(nullValue).nullable(),
        c.greaterThan(nullValue).nullable(),
        c.greaterThanOrEqual(nullValue).nullable(),
        c.lessThan(nullValue).nullable(),
        c.lessThanOrEqual(nullValue).nullable(),

        c.eq(nullValue).nullable(),
        c.neq(nullValue).nullable(),
        c.gt(nullValue).nullable(),
        c.gteq(nullValue).nullable(),
        c.lt(nullValue).nullable(),
        c.lteq(nullValue).nullable(),

        c.between(nullValue, nullValue).nullable(),
        c.notBetween(nullValue, nullValue).nullable(),
        c.in(nullValues).nullable(),
        c.notIn(nullValues).nullable(),
        c.like(nullValue).nullable(),
        c.notLike(nullValue).nullable());
        
        assertEquals("", build(sb));
    }

    @Test
    public void testAllWhen() {
        AbstractColumn c = column();
        SqlBuilder sb = new SqlBuilder();
        sb.append(
        c.equal(nullValue).when(false),
        c.notEqual(nullValue).when(false),
        c.greaterThan(nullValue).when(false),
        c.greaterThanOrEqual(nullValue).when(false),
        c.lessThan(nullValue).when(false),
        c.lessThanOrEqual(nullValue).when(false),

        c.eq(nullValue).when(false),
        c.neq(nullValue).when(false),
        c.gt(nullValue).when(false),
        c.gteq(nullValue).when(false),
        c.lt(nullValue).when(false),
        c.lteq(nullValue).when(false),

        c.between(nullValue, nullValue).when(false),
        c.notBetween(nullValue, nullValue).when(false),
        c.in(nullValues).when(false),
        c.notIn(nullValues).when(false),
        c.like(nullValue).when(false),
        c.notLike(nullValue).when(false));
        
        assertEquals("", build(sb));
    }

    @Test
    public void testAllNullableOp() {
        Segment and = SegmentConstants.AND;
        AbstractColumn c = column();
        SqlBuilder sb = new SqlBuilder();
        sb.append(
        c.equal(nullValue).nullable(), and,
        c.notEqual(nullValue).nullable(), and,
        c.greaterThan(nullValue).nullable(), and,
        c.greaterThanOrEqual(nullValue).nullable(), and,
        c.lessThan(nullValue).nullable(), and,
        c.lessThanOrEqual(nullValue).nullable(), and,

        c.eq(nullValue).nullable(), and,
        c.neq(nullValue).nullable(), and,
        c.gt(nullValue).nullable(), and,
        c.gteq(nullValue).nullable(), and,
        c.lt(nullValue).nullable(), and,
        c.lteq(nullValue).nullable(), and,

        c.between(nullValue, nullValue).nullable(), and,
        c.notBetween(nullValue, nullValue).nullable(), and,
        c.in(nullValues).nullable(), and,
        c.notIn(nullValues).nullable(), and,
        c.like(nullValue).nullable(), and,
        c.notLike(nullValue).nullable());
        
        assertEquals("", build(sb));
    }

    @Test
    public void testAllWhenOp() {
        Segment and = SegmentConstants.AND;

        AbstractColumn c = column();
        SqlBuilder sb = new SqlBuilder();
        sb.append(
        c.equal(nullValue).when(false), and,
        c.notEqual(nullValue).when(false), and,
        c.greaterThan(nullValue).when(false), and,
        c.greaterThanOrEqual(nullValue).when(false), and,
        c.lessThan(nullValue).when(false), and,
        c.lessThanOrEqual(nullValue).when(false), and,

        c.eq(nullValue).when(false), and,
        c.neq(nullValue).when(false), and,
        c.gt(nullValue).when(false), and,
        c.gteq(nullValue).when(false), and,
        c.lt(nullValue).when(false), and,
        c.lteq(nullValue).when(false), and,

        c.between(nullValue, nullValue).when(false), and,
        c.notBetween(nullValue, nullValue).when(false), and,
        c.in(nullValues).when(false), and,
        c.notIn(nullValues).when(false), and,
        c.like(nullValue).when(false), and,
        c.notLike(nullValue).when(false));
        
        assertEquals("", build(sb));
    }
    
    private String build(SqlBuilder builder) {
        return builder.build(new DefaultBuilderContext());
    }
}
