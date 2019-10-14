package com.ppdai.das.client.sqlbuilder;

import com.google.common.collect.Lists;
import com.ppdai.das.client.*;
import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.client.Hints;

import com.ppdai.das.client.delegate.remote.BuilderUtils;
import com.ppdai.das.service.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.ppdai.das.client.SegmentConstants.AND;
import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.var;
import static com.ppdai.das.client.SqlBuilder.select;
import static com.ppdai.das.client.sqlbuilder.Person.PERSON;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import com.ppdai.das.core.enums.ParameterDirection;
public class BuilderUtilsTest {

    @Test
    public void testBuildParameterDefinition(){
        SqlBuilder builder = new SqlBuilder();
        Person.PersonDefinition p = Person.PERSON;
        builder.where(p.CityID.equal(var("a")), AND, p.CountryID.greaterThan(var("b")), OR, p.Name.like(var("c")).when(false));
        List<ParameterDefinition> params = builder.buildDefinitions();
        ParameterDefinition d = new ParameterDefinition(ParameterDirection.InputOutput, "h", JDBCType.DATE, true);
        params.add(d);

        List<DasParameterDefinition> l = BuilderUtils.buildParameterDefinition(params);
        assertEquals(3, l.size());
        assertEquals("h", l.get(2).getName());//TODO

        List<ParameterDefinition> pds = BuilderUtils.fromDefinition(l);
        assertEquals(3, pds.size());
        assertEquals("h", pds.get(2).getName());//TODO
    }

    @Test
    public void testBuildParameters(){
        SqlBuilder builder = new SqlBuilder();
        Person.PersonDefinition p = Person.PERSON;
        builder.where(p.CityID.equal(1), AND, p.CountryID.greaterThan(2), OR, p.Name.like(null).nullable());
        List<Parameter> params =  builder.buildParameters();
        Parameter pr = new Parameter("h", JDBCType.VARCHAR, "A");
        pr.setValues(Arrays.asList("B", "C"));
        params.add(pr);

        Date date = new java.sql.Date(1L);
        Parameter pr2 = new Parameter("h", JDBCType.DATE, date);
        pr2.setValues(Arrays.asList(date,  date));
        params.add(pr2);

        List<DasParameter> ps = BuilderUtils.buildParameters(params);
        assertEquals(4, ps.size());
        assertEquals("h", ps.get(2).getName());//TODO

        List<Parameter> ps2 = BuilderUtils.fromParameters(ps);
        assertEquals(4, ps2.size());
        assertEquals("h", ps2.get(2).getName());
        assertEquals("A", ps2.get(2).getValue());
        assertEquals(Arrays.asList("B", "C"), ps2.get(2).getValues());

        assertEquals(date, ps2.get(3).getValue());
    }

    @Test
    public void testBuildCallBuilder(){
        CallBuilder cb = new CallBuilder("SP_WITH_OUT_PARAM");
        cb.registerInput("v_id", JDBCType.INTEGER, 4);
        cb.registerOutput("count", JDBCType.INTEGER);

        DasCallBuilder dasCallBuilder = BuilderUtils.buildCallBuilder(cb);
        CallBuilder cb2 = BuilderUtils.fromCallBuilder(dasCallBuilder);
        assertEquals(2, cb2.buildParameters().size());
        assertEquals(cb.buildParameters().get(0).getValue(),
                cb2.buildParameters().get(0).getValue());
    }

    @Test
    public void testBuildSqlBuilders(){
        Person.PersonDefinition p = PERSON.as("p");

        SqlBuilder sqlBuilder = select(p.PeopleID).from(p, PERSON).where(p.CityID.equal(PERSON.CityID), AND, p.PeopleID.greaterThan(PERSON.CountryID));
        List<DasSqlBuilder> dasSqlBuilders = BuilderUtils.buildSqlBuilders(Arrays.asList(sqlBuilder));

        SqlBuilder sqlBuilder2 = BuilderUtils.fromSqlBuilder(dasSqlBuilders.get(0));
        assertEquals2("SELECT p.PeopleID FROM person p, person WHERE p.CityID = person.CityID AND p.PeopleID > person.CountryID",
                sqlBuilder.build(testCtx),
                sqlBuilder2.build(testCtx));
    }

    @Test
    public void testBuildBatchCallBuilder() {
        BatchCallBuilder cb = new BatchCallBuilder("SP_WITH_OUT_PARAM");
        cb.registerInput("v_id", JDBCType.INTEGER);
        cb.registerOutput("count", JDBCType.INTEGER);
        cb.addBatch(1);
        cb.addBatch(2);
        cb.addBatch(3);

        DasBatchCallBuilder dasBatchCallBuilder = BuilderUtils.buildBatchCallBuilder(cb);
        BatchCallBuilder cb2 = BuilderUtils.fromBatchCallBuilder(dasBatchCallBuilder);
        assertArrayEquals(cb.getValuesList().get(0), cb2.getValuesList().get(0));
        assertEquals(cb.buildDefinitions().get(0).getType(), cb2.buildDefinitions().get(0).getType());
        assertEquals(cb.buildDefinitions().get(0).getName(), cb2.buildDefinitions().get(0).getName());
        assertEquals(cb.buildDefinitions().get(0).isInValues(), cb2.buildDefinitions().get(0).isInValues());
        assertEquals(cb.buildDefinitions().get(0).getDirection(), cb2.buildDefinitions().get(0).getDirection());
    }

    private BuilderContext ctx = new DefaultBuilderContext();
    private BuilderContext testCtx = new CtripBuilderContextTest("person");

    static public void assertEquals2(Object expected, Object actual1, Object actual2) {
        assertEquals(null, expected, actual1);
        assertEquals(null, expected, actual2);
    }

    BuilderContext shardedBC = new BuilderContext() {
        @Override
        public String wrapName(String name) {
            return name;
        }

        @Override
        public String locateTableName(Table table) {
            return table.getName() + "_" + table.getShardId();
        }

        @Override
        public String locateTableName(TableDefinition definition) {
            return definition.getShardId() == null ? definition.getName() : definition.getName() + "_" + definition.getShardId();
        }

        @Override
        public String declareTableName(String name) {
            return name;
        }

        @Override
        public String getPageTemplate() {
            return Page.EMPTY;
        }
    };

    static class CtripBuilderContextTest extends DasBuilderContext {

        public CtripBuilderContextTest(String logicDbName) {
            super("das-test", logicDbName);
        }

        public CtripBuilderContextTest(String logicDbName, Hints ctripHints, List<Parameter> parameters) {
            super("das-test", logicDbName, ctripHints, parameters);
        }

        public CtripBuilderContextTest(String logicDbName, Hints ctripHints, List<Parameter> parameters, SqlBuilder builder) {
            super("das-test", logicDbName, ctripHints, parameters, builder);
        }

        @Override
        public String locateTableName(TableDefinition definition) {
            return definition.getName();
        }

        @Override
        public String locateTableName(Table table) {
            return table.getName();
        }

        @Override
        public String wrapName(String name) {
            return name;
        }

        @Override
        public String declareTableName(String name) {
            return name;
        }

        @Override
        public String locate(String rawTableName, String tableShardId, Object tableShardValue) {
            if(tableShardId == null){
                return rawTableName;
            } else {
                return rawTableName + "_" + tableShardId;
            }
            // return rawTableName;// + tableShardId +tableShardValue;
        }
    }
/*
    public static void main(String[] v){
        Object[] ob = new Object[]{"A", 2, "", 4.6f, new Date(), "QQ".getBytes()};
        List<Object[]> values = Lists.<Object[]>newArrayList(ob);
        List<List<ByteBuffer>> b = BuilderUtils.buildValues(values);
        List<Object[]> p = BuilderUtils.fromValues(b);
        String sk = new String((byte[])p.get(0)[5]);
        sk.toUpperCase();

    }*/
}
