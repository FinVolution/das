package com.ppdai.das.client.sqlbuilder;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.TableDefinition;
import org.junit.Before;
import org.junit.Test;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import static com.ppdai.das.client.SegmentConstants.SELECT;
import static com.ppdai.das.strategy.OperatorEnum.BEWTEEN;
import static com.ppdai.das.strategy.OperatorEnum.EQUAL;
import static com.ppdai.das.strategy.OperatorEnum.IN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


public class SqlBuilderSerializeTest {

    Gson outGson;

    @Before
    public void before(){
        outGson = new SqlBuilderSerializer().outGson;
    }

    @Test
    public void testText() {
        String gs1 = outGson.toJson(SegmentConstants.SELECT);
        Text keyword = outGson.fromJson(gs1, Text.class);
        assertEquals(keyword.getClass(), Keyword.class);
        assertEquals(SELECT.getText(), keyword.getText());

        String gs2 = outGson.toJson(SegmentConstants.leftBracket);
        Text lb = outGson.fromJson(gs2, Text.class);
        assertEquals(lb.getClass(), Bracket.class);
        assertEquals("(", lb.getText());

        String gs3 = outGson.toJson(SegmentConstants.AND);
        Text ad = outGson.fromJson(gs3, Text.class);
        assertEquals(ad.getClass(), Operator.class);
        assertEquals("AND", ad.getText());
    }

    @Test
    public void testTableDefinition() {
        String gs = outGson.toJson(Person.PERSON.as("p").inShard("s"));
        TableDefinition td = outGson.fromJson(gs, TableDefinition.class);

        assertEquals(Person.PERSON.getName(), td.getName());
        assertEquals("p", td.getAlias());
        assertEquals("s", td.getShardId());
    }

    @Test
    public void testColumnDefinition() {
        String gs = outGson.toJson(Person.PERSON.PeopleID.as("p"));
        ColumnDefinition cd = outGson.fromJson(gs, ColumnDefinition.class);

        assertEquals(Person.PERSON.PeopleID.getColumnName(), cd.getColumnName());
        assertEquals("p", cd.getAlias().get());
        assertEquals("person", cd.getTable().getName());
    }

    @Test
    public void testColumnValueExpression() {
        String gs = outGson.toJson(Person.PERSON.PeopleID.eq(5));
        ColumnValueExpression cd = outGson.fromJson(gs, ColumnValueExpression.class);

        assertEquals(EQUAL.getTemplate(), cd.getTemplate());
    }

    @Test
    public void testColumnValueExpressionNullable() {
        String gs = outGson.toJson(Person.PERSON.Name.eq(null).nullable());
        ColumnValueExpression cd = outGson.fromJson(gs, ColumnValueExpression.class);

        assertEquals(EQUAL.getTemplate(), cd.getTemplate());
    }

    @Test
    public void testColumnExpression() {
        String gs = outGson.toJson(Person.PERSON.PeopleID.in(Lists.newArrayList(1, 2)));
        InExpression cd = outGson.fromJson(gs, InExpression.class);

        assertEquals(IN.getTemplate(), cd.getTemplate());
    }

    @Test
    public void testNullExpression() {
        String gs = outGson.toJson(Person.PERSON.PeopleID.isNull());
        NullExpression ne = outGson.fromJson(gs, NullExpression.class);

        assertEquals("%s IS NULL", ne.getTemplate());
    }

    @Test
    public void testBetweenExpression() {
        String gs = outGson.toJson(Person.PERSON.PeopleID.between(1, 10));
        BetweenExpression ne = outGson.fromJson(gs, BetweenExpression.class);

        assertEquals(BEWTEEN.getTemplate(), ne.getTemplate());
    }

    @Test
    public void testInterColumnExpression() {
        String gs = outGson.toJson(new InterColumnExpression(EQUAL, Person.PERSON.PeopleID, Person.PERSON.Name));
        InterColumnExpression ne = outGson.fromJson(gs, InterColumnExpression.class);

        assertEquals("%s = %s", ne.getTemplate());
    }

    @Test
    public void testBooleanExpression() {
        String gs = outGson.toJson(BooleanExpression.TRUE);
        BooleanExpression ne = outGson.fromJson(gs, BooleanExpression.class);
        assertSame(BooleanExpression.TRUE, ne);

        String gs2 = outGson.toJson(BooleanExpression.FALSE);
        BooleanExpression ne2 = outGson.fromJson(gs2, BooleanExpression.class);
        assertSame(BooleanExpression.FALSE, ne2);
    }

    @Test
    public void testColumnReference() {
        SqlBuilder q = SqlBuilder.selectAllFrom(Person.PERSON).where().allOf(Person.PERSON.PeopleID.eq("s")).orderBy(Person.PERSON.CityID);
        String gs = outGson.toJson(new ColumnReference(Person.PERSON.CityID));
        ColumnReference cr = outGson.fromJson(gs, ColumnReference.class);

        assertEquals(Person.PERSON.CityID.getColumnName(), cr.getColumn().getColumnName());
    }

    @Test
    public void testColumnOrder() {
        String gs = outGson.toJson(new ColumnOrder(Person.PERSON.CityID, true));
        ColumnOrder cr = outGson.fromJson(gs, ColumnOrder.class);

        assertEquals(Person.PERSON.CityID.getColumnName(), cr.getColumn().getColumn().getColumnName());
        assertTrue(cr.isAsc());
    }

    @Test
    public void testTableDeclaration() {
        TableDeclaration td = (TableDeclaration)TableDeclaration.filter(Person.PERSON);
        String gs = outGson.toJson(td);
        TableDeclaration tableDeclaration = outGson.fromJson(gs, TableDeclaration.class);

        assertEquals("person", tableDeclaration.getName());
    }

    @Test
    public void testSQLBuilder() {
        SqlBuilder sqlBuilder = SqlBuilder.select(Person.PERSON.CityID.as("cid")).from(Person.PERSON.as("p"))
                .leftJoin(Person.PERSON.as("l"))
                .on(Person.PERSON.CityID.like("x")).where()
                .allOf(Person.PERSON.PeopleID.eq(2), Person.PERSON.Name.like("H"), Person.PERSON.PeopleID.isNull(), Person.PERSON.Name.between(1, 5))
                .orderBy(Person.PERSON.CountryID);
        String gs = SqlBuilderSerializer.serializeSegment(sqlBuilder);
        SqlBuilder sqlBuilder2 = SqlBuilderSerializer.deserializeSegment(gs);

        assertEquals(sqlBuilder.getSegments().size(), sqlBuilder2.getSegments().size());
        assertEquals(sqlBuilder.build(new DefaultBuilderContext()), sqlBuilder2.build(new DefaultBuilderContext()));
    }

    @Test
    public void testNestedSQLBuilder() {
        SqlBuilder sqlBuilder = SqlBuilder.selectCount()
                .from(SqlBuilder.selectAllFrom(Person.PERSON)).where(Person.PERSON.CityID.eq(1));

        String gs = SqlBuilderSerializer.serializeSegment(sqlBuilder);
        SqlBuilder sqlBuilder2 = SqlBuilderSerializer.deserializeSegment(gs);

        assertEquals(sqlBuilder.getSegments().size(), sqlBuilder2.getSegments().size());
        assertEquals(sqlBuilder.build(new DefaultBuilderContext()), sqlBuilder2.build(new DefaultBuilderContext()));
    }


    @Test
    public void testTemplate() {
        Template template = new Template("t? b?", Parameter.integerOf("n", 3), Parameter.varcharOf("k", "X"));
        String gs = outGson.toJson(template);
        Template template1 = outGson.fromJson(gs, Template.class);

        assertEquals(template.build(new DefaultBuilderContext()), template1.build(new DefaultBuilderContext()));
    }

    @Test
    public void testPage() {
        Page page = new Page(1, 2);
        String gs = outGson.toJson(page);
        Page page1 = outGson.fromJson(gs, Page.class);

        assertEquals(page.build(new DefaultBuilderContext()), page1.build(new DefaultBuilderContext()));
    }

    @Test
    public void testParameter() {
        Parameter p = Person.PERSON.Name.of("Jerry");
        String gs = outGson.toJson(p);
        Parameter p1 = outGson.fromJson(gs, Parameter.class);

        assertEquals(p.build(new DefaultBuilderContext()), p1.build(new DefaultBuilderContext()));
    }

    @Test
    public void testString() {
        String gs = SqlBuilderSerializer.serializePrimitive("A");
        String s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertEquals("A", s1);
    }

    @Test
    public void testLong() {
        String gs = SqlBuilderSerializer.serializePrimitive(12L);
        long s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertEquals(12L, s1);
    }

    @Test
    public void testFloat() {
        String gs = SqlBuilderSerializer.serializePrimitive(1.2f);
        float s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertEquals(1.2f, s1, 0.1);
    }

    @Test
    public void testBool() {
        String gs = SqlBuilderSerializer.serializePrimitive(true);
        boolean s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertTrue(s1);
    }

    @Test
    public void testDate() {
        Date date = new Date(100);
        String gs = SqlBuilderSerializer.serializePrimitive(date);
        Date s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertEquals(date, s1);

        Timestamp timestamp = new Timestamp(200);
        String gs2 = SqlBuilderSerializer.serializePrimitive(timestamp);
        Date s2 = SqlBuilderSerializer.deserializePrimitive(gs2);
        assertEquals(timestamp, s2);
    }

    @Test
    public void testArray() {
        String gs = SqlBuilderSerializer.serializePrimitive(Arrays.asList("A", "B"));
        Object s1 = SqlBuilderSerializer.deserializePrimitive(gs);
        assertEquals(Arrays.asList("A", "B"), s1);
    }
}
