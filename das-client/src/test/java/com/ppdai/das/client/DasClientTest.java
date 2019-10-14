package com.ppdai.das.client;

import static com.ppdai.das.client.Hints.hints;
import static com.ppdai.das.client.ParameterDefinition.integerVar;
import static com.ppdai.das.client.ParameterDefinition.varcharVar;
import static com.ppdai.das.client.SqlBuilder.*;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.ppdai.das.client.sqlbuilder.InExpression;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;

@RunWith(Parameterized.class)
public class DasClientTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlSimple";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrSimple";

    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {SqlServer},
                {MySql},
        });
    }
    
    public DasClientTest(DatabaseCategory dbCategory) throws SQLException {
        super(dbCategory);
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    @Before
    public void setup() throws Exception {
        String[] statements = new String[TABLE_MODE];
        for (int k = 0; k < TABLE_MODE; k++) {
            statements[k] = String.format("INSERT INTO person(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, 'test', %d, %d, 1)", k + 1, k, k);
        }
        
        if(!allowInsertWithId())
            statements = DbSetupUtil.handle("Person", statements);
        
        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
        dao.batchUpdate(builder);
    }

    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    @After
    public void tearDown() throws SQLException {
        String[] statements = new String[]{"DELETE FROM " + TABLE_NAME};

        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
        dao.batchUpdate(builder);
    }

    @Test
    public void testQueryById() throws Exception {
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk = dao.queryByPk(pk);
            assertNotNull(pk);
            assertEquals("test", pk.getName());
        }
    }

    @Test
    public void testQueryBySample() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            Person pk = new Person();
            pk.setName("test");
            List<Person> plist = dao.queryBySample(pk);
            assertNotNull(plist);
            assertEquals(4, plist.size());
            for(Person p: plist)
                assertEquals("test", p.getName());
        }
    }

    @Test
    public void testQueryBySamplePage() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> plist;
            Person pk = new Person();
            pk.setName("test");

            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID));
            assertList(4, plist);
            
            plist = dao.queryBySample(pk, PageRange.atPage(2, 2, p.CityID, p.CountryID));
            assertList(2, plist);
            
            if(dbCategory == DatabaseCategory.MySql) {
                plist = dao.queryBySample(pk, PageRange.atPage(3, 2));
                assertList(0, plist);
            }
        
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc()));
            assertList(4, plist);
            assertOrder(plist, true);
            
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.desc()));
            assertList(4, plist);
            assertOrder(plist, false);
            
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc(), p.CityID.desc()));
            assertList(4, plist);
            assertOrder(plist, true);
        }
    }
    
    private void assertList(int size, List<Person> plist) {
        assertNotNull(plist);
        assertEquals(size, plist.size());
        
        int id = -1;
        for(Person p: plist) {
            assertEquals("test", p.getName());
        }
    }
    
    private void assertOrder(List<Person> plist, boolean asc) {
        int id = asc ? -1 : 10000;
        for(Person p: plist) {
            if(asc)
                assertTrue(p.getPeopleID() > id);
            else
                assertTrue(p.getPeopleID() < id);
        }
    }

    @Test
    public void testCountBySample() throws Exception {
        Person pk = new Person();
        pk.setName("test");
        assertEquals(4, dao.countBySample(pk));
        
        for(int j = 0; j < TABLE_MODE;j++) {
            pk = new Person();
            pk.setPeopleID(j+1);
            assertEquals(1, dao.countBySample(pk));
            
            pk = new Person();
            pk.setCountryID(j);
            pk.setCityID(j);
            assertEquals(1, dao.countBySample(pk));
        }
    }

    @Test
    public void testInsertOne() throws Exception {
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            assertEquals(1, dao.insert(p));
            p.setCountryID(null);
            p.setCityID(null);
            List<Person> plist = dao.queryBySample(p);
            assertNotNull(plist);
            assertEquals(k + 1, plist.size());
        }
    }

    @Test
    public void testInsertWithId() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setPeopleID(TABLE_MODE + k + 1);
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            assertEquals(1, dao.insert(p, hints().insertWithId()));
            p = dao.queryByPk(p);
            assertNotNull(p);
        }
    }

    @Test
    public void testInsertSetIdBack() throws Exception {
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry" + k);
            p.setCountryID(k);
            p.setCityID(k);
            assertEquals(1, dao.insert(p, hints().setIdBack()));
            assertNotNull(p.getPeopleID());
            p = dao.queryByPk(p);
            assertNotNull(p);
            assertEquals("jerry" + k, p.getName());
        }
    }

    @Test
    public void testInsertList() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            pl.add(p);
        }
        assertEquals(TABLE_MODE, dao.insert(pl));
        
        Person p = new Person();
        p.setName("jerry");

        List<Person> plist = dao.queryBySample(p);
        assertNotNull(plist);
        assertEquals(TABLE_MODE, plist.size());                
    }

    @Test
    public void testInsertListWithId() throws Exception {
        if(!allowInsertWithId())
            return;

        List<Person> pl = new ArrayList<>();
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setPeopleID(TABLE_MODE + k + 1);
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            pl.add(p);
        }
        assertEquals(TABLE_MODE, dao.insert(pl, hints().insertWithId()));

        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setPeopleID(TABLE_MODE + k + 1);
            p = dao.queryByPk(p);
            assertNotNull(p);
        }
    }

    @Test
    public void testInsertListSetIdBack() throws Exception {
        if(!allowInsertWithId())
            return;

        List<Person> pl = new ArrayList<>();
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry" + k);
            p.setCountryID(k);
            p.setCityID(k);
            pl.add(p);
        }
        assertEquals(TABLE_MODE, dao.insert(pl, hints().setIdBack()));

        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = pl.get(k);
            p.setName(null);
            assertNotNull(p.getPeopleID());

            p = dao.queryByPk(p);
            assertNotNull(p);
            assertEquals("jerry" + k, p.getName());
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            pl.add(p);
        }
        int[] ret = dao.batchInsert(pl);
        for(int x: ret)
            assertEquals(batchRet, x);
        
        assertEquals(TABLE_MODE, ret.length);
        
        Person p = new Person();
        p.setName("jerry");

        List<Person> plist = dao.queryBySample(p);
        assertNotNull(plist);
        assertEquals(TABLE_MODE, plist.size());
        
        for(Person p1: plist)
            assertNotNull(p1.getPeopleID());
    }

    @Test
    public void testBatchInsert2() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int k = 0; k < TABLE_MODE;k++) {
            Person p = new Person();
            p.setName("jerry");
            p.setCountryID(k);
            p.setCityID(k);
            pl.add(p);
        }
        
        int[] ret = dao.batchInsert(pl);
        for(int x: ret)
            assertEquals(batchRet, x);
        
        assertEquals(TABLE_MODE, ret.length);

        for(int j = 0; j < TABLE_MODE;j++) {
    
            Person p = new Person();
            p.setName("jerry");
    
            List<Person> plist = dao.queryBySample(p);
            assertNotNull(plist);
            assertEquals(TABLE_MODE, plist.size());                
        }
    }

    @Test
    public void testDeleteOne() throws Exception {
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk.setCountryID(k);
            pk.setCityID(k);
            assertEquals(1, dao.deleteByPk(pk));
            assertNull(dao.queryByPk(pk));
        }
    }

    @Test
    public void testDeleteBySample() throws Exception {
        Person sample = new Person();
        sample.setName("test");
        assertEquals(4, dao.deleteBySample(sample));
        assertEquals(0, dao.queryBySample(sample).size());
    }

    @Test
    public void testBatchDelete() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk.setCountryID(k);
            pk.setCityID(k);
            pl.add(pk);
        }

        int[] ret = dao.batchDelete(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(TABLE_MODE, ret.length);
        
        for(Person pk: pl)
            assertNull(dao.queryByPk(pk));
    }

    @Test
    public void testUpdate() throws Exception {
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk.setName("Tom");
            pk.setCountryID(100);
            pk.setCityID(200);
            assertEquals(1, dao.update(pk));
            pk = dao.queryByPk(pk);
            
            assertEquals("Tom", pk.getName());
            assertEquals(100, pk.getCountryID().intValue());
            assertEquals(200, pk.getCityID().intValue());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateOverloading() throws Exception {
        dao.update(SqlBuilder.selectCount(), Hints.hints());
    }

    @Test
    public void testInsertBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int k = 0; k < TABLE_MODE; k++) {
            SqlBuilder builder = insertInto(p, p.Name, p.CountryID, p.CityID).values(p.Name.of("Jerry" + k), p.CountryID.of(k+100), p.CityID.of(k+200));
            assertEquals(1, dao.update(builder));
            Person pk = new Person();
            pk.setName("Jerry" + k);
            List<Person> pl = dao.queryBySample(pk);
            assertEquals(1,  pl.size());
            pk = pl.get(0);
            assertNotNull(pk.getPeopleID());
            assertEquals("Jerry" + k, pk.getName());
            assertEquals(k+100, pk.getCountryID().intValue());
            assertEquals(k+200, pk.getCityID().intValue());
        }
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int k = 0; k < TABLE_MODE; k++) {
            SqlBuilder builder = update(Person.PERSON).set(p.Name.eq("Tom"), p.CountryID.eq(100), p.CityID.eq(200)).where(p.PeopleID.eq(k+1));
            assertEquals(1, dao.update(builder));
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk = dao.queryByPk(pk);
            
            assertEquals("Tom", pk.getName());
            assertEquals(100, pk.getCountryID().intValue());
            assertEquals(200, pk.getCityID().intValue());
        }
    }

    @Test
    public void testUpdateNullableBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int k = 0; k < TABLE_MODE; k++) {
            SqlBuilder builder = update(Person.PERSON).set(p.Name.eq("Tom"), p.DataChange_LastTime.eq(null).nullable(), p.CountryID.eq(100), p.CityID.eq(200)).where(p.PeopleID.eq(k+1));
            assertEquals(1, dao.update(builder));
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk = dao.queryByPk(pk);

            assertEquals("Tom", pk.getName());
            assertEquals(100, pk.getCountryID().intValue());
            assertEquals(200, pk.getCityID().intValue());
        }
    }

    @Test
    public void testDeleteBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int k = 0; k < TABLE_MODE; k++) {
            SqlBuilder builder = deleteFrom(p).where(p.PeopleID.eq(k+1));
            assertEquals(1, dao.update(builder));
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            assertNull(dao.queryByPk(pk));
        }
    }

    @Test
    public void testBatchUpdate() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = new Person();
            pk.setPeopleID(k + 1);
            pk.setName("Tom");
            pk.setCountryID(100);
            pk.setCityID(200);
            pl.add(pk);
        }

        int[] ret = dao.batchUpdate(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(4, ret.length);
        
        for(Person pk: pl) {
            pk = dao.queryByPk(pk);
            
            assertEquals("Tom", pk.getName());
            assertEquals(100, pk.getCountryID().intValue());
            assertEquals(200, pk.getCityID().intValue());
        }
    }

    @Test
    public void testBatchUpdateBuillder() throws Exception {
        String[] statements = new String[]{
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
                        + " VALUES( 'test', 10, 1, 1)",
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
                        + " VALUES( 'test', 10, 1, 1)",
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
                        + " VALUES( 'test', 10, 1, 1)",};

        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
        int[] ret = dao.batchUpdate(builder);
        assertEquals(3,  ret.length);
        for(int i: ret)
            assertEquals(1, i);
    }
    
    @Test
    public void testBatchUpdateBuillderValues() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                p.Name, p.CityID, p.ProvinceID, p.CountryID);
        
        builder.addBatch("test1", 10, 100, 200);
        builder.addBatch("test2", 20, 200, 100);
        builder.addBatch("test3", 30, 300, 0);
        
        int[] ret = dao.batchUpdate(builder);
        assertEquals(3,  ret.length);
        for(int i: ret)
            assertEquals(batchRet, i);//because mysql connection property set to rewriteStatement
        
        List<String> names = new ArrayList<>();

        names.add("test1");
        names.add("test2");
        names.add("test3");
        
        List<Person> list = dao.query(selectAllFrom(p).where(p.Name.in(names)).orderBy(p.Name.asc()).into(Person.class));
        int i = 0;
        for(Person px: list) {
            assertEquals("test" + (i + 1), px.getName());
            assertEquals(10 + 10 * i, px.getCityID().intValue());
            assertEquals(100 + 100 * i, px.getProvinceID().intValue());
            assertEquals(200 - 100 * i++, px.getCountryID().intValue());
        }
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInValuesThreshold() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        try{
            InExpression.setInThreshold(2);
            selectAllFrom(p).where(p.Name.in(Lists.newArrayList("a", "b")));
            Assert.fail();
        } finally {
            InExpression.setInThreshold(-1);
        }
    }

    @Test
    public void testBatchUpdateBuillderValues2() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                varcharVar("Name"), integerVar("CityID"), integerVar("ProvinceID"), integerVar("CountryID"));
        
        builder.addBatch("test1", 10, 100, 200);
        builder.addBatch("test2", 20, 200, 100);
        builder.addBatch("test3", 30, 300, 0);
        
        int[] ret = dao.batchUpdate(builder);
        assertEquals(3,  ret.length);
        for(int i: ret)
            assertEquals(batchRet, i);//because mysql connection property set to rewriteStatement
        
        List<String> names = new ArrayList<>();

        names.add("test1");
        names.add("test2");
        names.add("test3");
        
        List<Person> list = dao.query(selectAllFrom(p).where(p.Name.in(names)).orderBy(p.Name.asc()).into(Person.class));
        int i = 0;
        for(Person px: list) {
            assertEquals("test" + (i + 1), px.getName());
            assertEquals(10 + 10 * i, px.getCityID().intValue());
            assertEquals(100 + 100 * i, px.getProvinceID().intValue());
            assertEquals(200 - 100 * i++, px.getCountryID().intValue());
        }
    }
    
    @Test
    public void testBatchUpdateBuillderBuilder() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                insertInto(p, p.Name, p.CityID, p.ProvinceID, p.CountryID).values(
                varcharVar("Name"), integerVar("CityID"), integerVar("ProvinceID"), integerVar("CountryID")));
        
        builder.addBatch("test1", 10, 100, 200);
        builder.addBatch("test2", 20, 200, 100);
        builder.addBatch("test3", 30, 300, 0);
        
        int[] ret = dao.batchUpdate(builder);
        assertEquals(3,  ret.length);
        for(int i: ret)
            assertEquals(batchRet, i);//because mysql connection property set to rewriteStatement
        
        List<String> names = new ArrayList<>();

        names.add("test1");
        names.add("test2");
        names.add("test3");
        
        List<Person> list = dao.query(selectAllFrom(p).where(p.Name.in(names)).orderBy(p.Name.asc()).into(Person.class));
        int i = 0;
        for(Person px: list) {
            assertEquals("test" + (i + 1), px.getName());
            assertEquals(10 + 10 * i, px.getCityID().intValue());
            assertEquals(100 + 100 * i, px.getProvinceID().intValue());
            assertEquals(200 - 100 * i++, px.getCountryID().intValue());
        }
    }
    
    @Test
    public void testBatchUpdateBuillderBuilderTemplate() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        
        SqlBuilder sqlbuilder = new SqlBuilder().appendBatchTemplate(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                p.Name.var(), p.CityID.var(), p.ProvinceID.var(), p.CountryID.var());

        BatchUpdateBuilder builder = new BatchUpdateBuilder(sqlbuilder);
        
        builder.addBatch("test1", 10, 100, 200);
        builder.addBatch("test2", 20, 200, 100);
        builder.addBatch("test3", 30, 300, 0);
        
        int[] ret = dao.batchUpdate(builder);
        assertEquals(3,  ret.length);
        for(int i: ret)
            assertEquals(batchRet, i);//because mysql connection property set to rewriteStatement
        
        List<String> names = new ArrayList<>();

        names.add("test1");
        names.add("test2");
        names.add("test3");
        
        List<Person> list = dao.query(selectAllFrom(p).where(p.Name.in(names)).orderBy(p.Name.asc()).into(Person.class));
        int i = 0;
        for(Person px: list) {
            assertEquals("test" + (i + 1), px.getName());
            assertEquals(10 + 10 * i, px.getCityID().intValue());
            assertEquals(100 + 100 * i, px.getProvinceID().intValue());
            assertEquals(200 - 100 * i++, px.getCountryID().intValue());
        }
    }
    
    @Test
    public void testQueryObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(j+1)).into(Person.class);
            Person pk = dao.queryObject(builder);
            assertNotNull(pk);
            assertEquals(j+1,  pk.getPeopleID().intValue());
            assertEquals("test",  pk.getName());
        }
        
        SqlBuilder builder = selectCount().from(p).intoObject();
        Number n = dao.queryObject(builder);
        assertEquals(4, n.longValue());
        
        for (int j = 0; j < TABLE_MODE; j++) {
            builder = selectAllFrom(p).where(p.PeopleID.eq(j+1)).into(Person.class).withLock();
            Person pk = dao.queryObject(builder);
            assertNotNull(pk);
            assertEquals(j+1,  pk.getPeopleID().intValue());
            assertEquals("test",  pk.getName());
        }
        
        builder = selectCount().from(p).intoObject().withLock();
        n = dao.queryObject(builder);
        assertEquals(4, n.longValue());

    }

    @Test
    public void testQueryObjectAllowNullResult() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(-1)).into(Person.class);
        
        try {
            dao.queryObject(builder);
            fail();
        }catch(Throwable e) {
            
        }
        
        Person pk = dao.queryObjectNullable(builder);
        assertNull(pk);
    }

    @Test
    public void testQueryCount() throws Exception {
        SqlBuilder builder = selectCount().from(p).intoObject();
        Number n = dao.queryObject(builder);
        assertEquals(4, n.longValue());
    }

    @Test
    public void testQueryAll() throws Exception {
        SqlBuilder builder = selectAll().from(p).where(p.PeopleID.eq(1)).into(Person.class);
        Person p = dao.queryObject(builder);
        assertNotNull(p);
        assertEquals("test", p.getName());
    }

    @Test
    public void testQueryBetween() throws Exception {
        SqlBuilder builder = selectAll().from(p).where(p.PeopleID.between(1, 3)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> pl = dao.query(builder);
        
        assertEquals(3, pl.size());
        assertEquals(1, pl.get(0).getPeopleID().intValue());
        assertEquals(2, pl.get(1).getPeopleID().intValue());
        assertEquals(3, pl.get(2).getPeopleID().intValue());
    }

    @Test
    public void testQuery() throws Exception {
        PersonDefinition p = Person.PERSON;
        List<Integer> pks = new ArrayList<>();
        for (int k = 0; k < TABLE_MODE; k++)
            pks.add(k+1);

        SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);

        assertEquals(4, plist.size());
        for (int k = 0; k < TABLE_MODE; k++) {
            Person pk = plist.get(k);
            assertNotNull(p);
            assertEquals(k+1,  pk.getPeopleID().intValue());
            assertEquals("test",  pk.getName());
        }
    }

    @Test
    public void testQueryIntoObjectID() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectDistinct(p.PeopleID).from(p).orderBy(p.PeopleID.asc()).intoObject();
        List plist = dao.query(builder);

        assertEquals(4, plist.size());
        for(int i = 0; i < plist.size(); i++){
            assertEquals(i + 1, ((Number)plist.get(i)).intValue());
        }
    }

    @Test
    public void testQueryTopObj() throws Exception {
        if(dbCategory == MySql)
            return;
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectTop(3, p.PeopleID).from(p).orderBy(p.PeopleID.asc()).intoObject();
        List plist = dao.query(builder);

        assertEquals(3, plist.size());
        for(int i = 0; i < plist.size(); i++){
            assertEquals(i + 1, ((Number)plist.get(i)).intValue());
        }
    }

    @Test
    public void testQueryTopEntity() throws Exception {
        if(dbCategory == MySql)
            return;

        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectTop(3, p.PeopleID, p.Name).from(p).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = null;
        plist = dao.query(builder);

        assertEquals(3, plist.size());
        for(int i = 0; i < plist.size(); i++){
            assertEquals("test", plist.get(i).getName());
        }
    }

    @Test
    public void testQueryIntoObjectString() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectDistinct(p.Name).from(p).orderBy(p.Name.asc()).intoObject();
        List plist = dao.query(builder);

        assertEquals(1, plist.size());
        assertEquals("test", (String)plist.get(0));
    }

    @Test
    public void testBatchQueryBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        BatchQueryBuilder batchBuilder = new BatchQueryBuilder();

        for (int k = 0; k < TABLE_MODE; k++) {
            batchBuilder.addBatch(selectAllFrom(p).where().allOf(p.PeopleID.eq(k+1)).orderBy(p.PeopleID.asc()).into(Person.class));
        }
        List<List<Person>> plist = (List<List<Person>>)dao.batchQuery(batchBuilder);
        assertEquals(TABLE_MODE, plist.size());
        for (int k = 0; k < TABLE_MODE; k++) {
            List<Person> list = plist.get(k);
            assertEquals(1, list.size());
            Person pk = list.get(0);
            assertNotNull(p);
            assertEquals(k+1,  pk.getPeopleID().intValue());
            assertEquals("test",  pk.getName());
        }
    }

    @Test
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            List<Person> plist = dao.query(builder);

            assertEquals(4, plist.size());
            
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(0, dao.query(builder).size());
            
            assertEquals(4, dao.insert(plist));
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(4, dao.query(builder).size());
        });            
    }
    
    @Test
    public void testTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            List<Person> plist = dao.query(builder);

            assertEquals(4, plist.size());
            
            testTransactionNestBatchDelete(plist);
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(0, dao.query(builder).size());
            
            testTransactionNestInsert(plist);
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(4, dao.query(builder).size());
        });            
    }
    
    @Test
    public void testCallableTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        List<Person> plistx = dao.execute(() -> {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            List<Person> plist = dao.query(builder);

            assertEquals(4, plist.size());
            
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(0, dao.query(builder).size());
            
            assertEquals(4, dao.insert(plist));
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(4, dao.query(builder).size());
            return plist;
        });            
        
        assertEquals(4, plistx.size());
    }

    @Test
    public void testCallableTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        List<Person> plistx = dao.execute(() -> {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            List<Person> plist = dao.query(builder);

            assertEquals(4, plist.size());
            
            testTransactionNestBatchDelete(plist);
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(0, dao.query(builder).size());
            
            testTransactionNestInsert(plist);
            
            builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(4, dao.query(builder).size());
            return plist;
        });            
        
        assertEquals(4, plistx.size());
    }
    
    private void testTransactionNestBatchDelete(List<Person> plist) throws SQLException {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(0, dao.query(builder).size());
        });            
    }
    
    private void testTransactionNestInsert(List<Person> plist) throws SQLException {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            assertEquals(4, dao.insert(plist));
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            assertEquals(4, dao.query(builder).size());
        });            
    }    
}