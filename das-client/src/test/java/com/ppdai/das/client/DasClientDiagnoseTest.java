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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;

@RunWith(Parameterized.class)
public class DasClientDiagnoseTest extends DataPreparer {
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
    
    public DasClientDiagnoseTest(DatabaseCategory dbCategory) throws SQLException {
        super(dbCategory);
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    @BeforeClass
    public static void before() throws Exception {
//        System.setProperty("das.client.debug", "true");
    }
    
    @AfterClass
    public static void after() throws Exception {
//        System.setProperty("das.client.debug", "false");
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

    private void test(Diagnose d) {
        try {
            d.run(null);
            fail();
        } catch (Exception e) {
        }

        Hints hints = new Hints();
        try {
            d.run(hints);
            fail();
        } catch (Exception e) {
        }
    }
    
    private interface Diagnose {
        void run(Hints hints) throws SQLException;
    }
    
////    @Test
////    public void testQueryById() throws Exception {
////        test((hints)->{
////            Person pk = new Person();
////            pk.setPeopleID(null);
////            pk = dao.queryByPk(pk, hints);
////        });
////    }
////
////    @Test
////    public void testQueryx() throws Exception {
////        test((hints)->{
////            SqlBuilder builder = selectDistinct(p.Name).from(p).append("xxx").orderBy(p.Name.asc()).intoObject();
////            builder.setHints(hints);
////            dao.query(builder);
////        });
////    }
////
////    @Test
////    public void testQueryBySample() throws Exception {
////        test((hints)->{
////            Person pk = new Person();
////            List<Person> plist = dao.queryBySample(pk);
////        });
////    }
////
////    @Test
////    public void testQueryBySamplePage() throws Exception {
////        for(int j = 0; j < TABLE_MODE;j++) {
////            List<Person> plist;
////            Person pk = new Person();
////            pk.setName("test");
////
////            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID));
////            assertList(4, plist);
////            
////            plist = dao.queryBySample(pk, PageRange.atPage(2, 2, p.CityID, p.CountryID));
////            assertList(2, plist);
////            
////            if(dbCategory == DatabaseCategory.MySql) {
////                plist = dao.queryBySample(pk, PageRange.atPage(3, 2));
////                assertList(0, plist);
////            }
////        
////            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc()));
////            assertList(4, plist);
////            assertOrder(plist, true);
////            
////            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.desc()));
////            assertList(4, plist);
////            assertOrder(plist, false);
////            
////            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc(), p.CityID.desc()));
////            assertList(4, plist);
////            assertOrder(plist, true);
////        }
////    }
////    
////    private void assertList(int size, List<Person> plist) {
////        assertNotNull(plist);
////        assertEquals(size, plist.size());
////        
////        int id = -1;
////        for(Person p: plist) {
////            assertEquals("test", p.getName());
////        }
////    }
////    
////    private void assertOrder(List<Person> plist, boolean asc) {
////        int id = asc ? -1 : 10000;
////        for(Person p: plist) {
////            if(asc)
////                assertTrue(p.getPeopleID() > id);
////            else
////                assertTrue(p.getPeopleID() < id);
////        }
////    }
////
////    @Test
////    public void testCountBySample() throws Exception {
////        Person pk = new Person();
////        pk.setName("test");
////        assertEquals(4, dao.countBySample(pk));
////        
////        for(int j = 0; j < TABLE_MODE;j++) {
////            pk = new Person();
////            pk.setPeopleID(j+1);
////            assertEquals(1, dao.countBySample(pk));
////            
////            pk = new Person();
////            pk.setCountryID(j);
////            pk.setCityID(j);
////            assertEquals(1, dao.countBySample(pk));
////        }
////    }
////
//    @Test
//    public void testInsertOne() throws Exception {
//        test((hints)->{
//            Person p = new Person();
//            dao.insert(p, hints);
//        });
//
//        test((hints)->{
//            Person p = new Person();
//            dao.insert(p);
//        });
//    }
//
//    @Test
//    public void testInsertList() throws Exception {
//        test((hints)->{
//            List<Person> pl = new ArrayList<>();
//            for(int k = 0; k < TABLE_MODE;k++) {
//                Person p = new Person();
//                pl.add(p);
//            }
//            assertEquals(TABLE_MODE, dao.insert(pl, hints));
//        });
//
//        test((hints)->{
//            List<Person> pl = new ArrayList<>();
//            for(int k = 0; k < TABLE_MODE;k++) {
//                Person p = new Person();
//                pl.add(p);
//            }
//            assertEquals(TABLE_MODE, dao.insert(pl));
//        });
//    }
//
//    @Test
//    public void testBatchInsert() throws Exception {
//        List<Person> pl = new ArrayList<>();
//        for(int k = 0; k < TABLE_MODE;k++) {
//            Person p = new Person();
//            pl.add(p);
//        }
//        
//        test((hints)->{
//            dao.batchInsert(pl, hints);
//        });
//    }
//
////    @Test
////    public void testDeleteOne() throws Exception {
////        test((hints)->{
////            Person pk = new Person();
////            dao.deleteByPk(pk);
////        });
////    }
////
////    @Test
////    public void testDeleteBySample() throws Exception {
////        Person sample = new Person();
////        sample.setName("test");
////        assertEquals(4, dao.deleteBySample(sample));
////        assertEquals(0, dao.queryBySample(sample).size());
////    }
////
////    @Test
////    public void testBatchDelete() throws Exception {
////        List<Person> pl = new ArrayList<>();
////        for (int k = 0; k < TABLE_MODE; k++) {
////            Person pk = new Person();
////            pk.setPeopleID(k + 1);
////            pk.setCountryID(k);
////            pk.setCityID(k);
////            pl.add(pk);
////        }
////
////        test((hints)->{
////            dao.batchDelete(pl);
////        });
////    }
////
////    @Test
////    public void testUpdate() throws Exception {
////        test((hints)->{
////            Person pk = new Person();
////            pk.setName("Tom");
////            pk.setCountryID(100);
////            pk.setCityID(200);
////            assertEquals(1, dao.update(pk));
////        });
////    }
////
//    @Test
//    public void testInsertBuilder() throws Exception {
//        PersonDefinition p = Person.PERSON;
//        test((hints)->{
//            int k = 0;
//            SqlBuilder builder = insertInto(p, p.Name, p.CountryID, p.CityID).values(p.Name.of("Jerry" + k), p.CountryID.of(k+100), p.CityID.of(k+200), "8888");
//            dao.update(builder.setHints(hints));
//        });
//    }
//
//    @Test
//    public void testUpdateBuilder() throws Exception {
//        PersonDefinition p = Person.PERSON;
//        test((hints)->{
//            int k = 1;
//            SqlBuilder builder = update(Person.PERSON).set(p.Name.eq("Tom"), p.CountryID.eq(100), p.CityID.eq(200)).where(p.PeopleID.eq(k+1), 1234);
//            dao.update(builder.setHints(hints));
//        });
//    }
//
//    @Test
//    public void testDeleteBuilder() throws Exception {
//        PersonDefinition p = Person.PERSON;
//        test((hints)->{
//            int k = 1;
//            SqlBuilder builder = deleteFrom(p).where(p.PeopleID.eq(k+1), "1111");
//            dao.update(builder.setHints(hints));
//        });
//    }
//
//    @Test
//    public void testBatchUpdate() throws Exception {
//        List<Person> pl = new ArrayList<>();
//        for (int k = 0; k < TABLE_MODE; k++) {
//            Person pk = new Person();
//            pl.add(pk);
//        }
//
//        test((hints)->{
//            dao.batchUpdate(pl, hints);
//        });
//    }

//    @Test
//    public void testBatchUpdateBuillder() throws Exception {
//        String[] statements = new String[]{
//                "INdSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
//                        + " VALUES( 'test', 10, 1, 1)",
//                "INSdERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
//                        + " VALUES( 'test', 10, 1, 1)",
//                "INSEdRT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID)"
//                        + " VALUES( 'test', 10, 1, 1)",};
//
//        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
//        test((hints)->{
//            dao.batchUpdate(builder);
//        });
//    }
//    
    @Test
    public void testBatchUpdateBuillderValues() throws Exception {
        Person.PersonDefinition p = Person.PERSON;
        
        SqlBuilder sqlbuilder = new SqlBuilder().appendBatchTemplate(
                "INSERT INTOe " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                p.Name.var(), p.CityID.var(), p.ProvinceID.var(), p.CountryID.var());

        BatchUpdateBuilder builder = new BatchUpdateBuilder(sqlbuilder);
        
        builder.addBatch("test1", 10, 100, 200);
        builder.addBatch("test2", 20, 200, 100);
        builder.addBatch("test3", 30, 300, 0);
        
        test((hints)->{
            dao.batchUpdate(builder);
        });
    }
    
    @Test
    public void testQueryObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        test((hints)->{
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(1), 1212).into(Person.class);
            Person pk = dao.queryObject(builder);
        });
        
        //The following works for mysql!!!
//        test((hints)->{
//            SqlBuilder builder = selectCount().from(p).where("111").intoObject();
//            Number n = dao.queryObject(builder);
//        });

        test((hints)->{
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(1), 123).into(Person.class).withLock();
            Person pk = dao.queryObject(builder);
        });
        
        //The following works for mysql!!!
//        test((hints)->{
//            SqlBuilder builder = selectCount().from(p).where(111).intoObject().withLock();
//            dao.queryObject(builder);
//        });
    }

    @Test
    public void testQueryAll() throws Exception {
        test((hints)->{
            SqlBuilder builder = selectAll().from(p).where(p.PeopleID.eq(1), 123).into(Person.class);
            Person p = dao.queryObject(builder);
        });
    }

    @Test
    public void testQueryIntoObjectID() throws Exception {
        test((hints)->{
            PersonDefinition p = Person.PERSON;
            SqlBuilder builder = selectDistinct(p.PeopleID).from(p).orderBy(111, p.PeopleID.asc()).intoObject();
            dao.query(builder);
        });
    }

    @Test
    public void testBatchQueryBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        BatchQueryBuilder batchBuilder = new BatchQueryBuilder();

        for (int k = 0; k < TABLE_MODE; k++) {
            batchBuilder.addBatch(selectAllFrom(p).where().allOf("111", p.PeopleID.eq(k+1)).orderBy(p.PeopleID.asc()).into(Person.class));
        }
        
        test((hints)->{
            dao.batchQuery(batchBuilder);
        });
    }

    @Test
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        test((hints)->{
            dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where(111, p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                List<Person> plist = dao.query(builder);
            });
        });
    }
    
    @Test
    public void testTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        test((hints)->{
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
        });
    }
    
    @Test
    public void testCallableTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        test((hints)->{
            List<Person> plistx = dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where(111, p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                List<Person> plist = dao.query(builder);
                return plist;
            });            
        });        
    }

    @Test
    public void testCallableTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        test((hints)->{
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
        });
    }
    
    private void testTransactionNestBatchDelete(List<Person> plist) throws SQLException {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            
            SqlBuilder builder = selectAllFrom(p).where(111, p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
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