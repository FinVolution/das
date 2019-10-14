package com.ppdai.das.client;

import static com.ppdai.das.client.Hints.hints;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;

@RunWith(Parameterized.class)
public class TableDaoTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlSimple";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrSimple";

    private static PersonDefinition p = Person.PERSON;

    private final static Integer DELETED = 0;
    private final static Integer ACTIVE = 1;

    private TableDao<Person> dao;
    private boolean allowLogicDeletion;
    private LogicDeletionDao<Person> logicDelDao;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {SqlServer, false},
                {MySql, false},                
                {SqlServer, true},
                {MySql, true},
        });
    }
    
    public TableDaoTest(DatabaseCategory dbCategory, boolean allowLogicDeletion) throws Exception {
        super(dbCategory);
        this.allowLogicDeletion = allowLogicDeletion;
        
        if(allowLogicDeletion)
            dao = buildDao();
        else
            dao = new TableDao<>(getDbName(dbCategory), Person.class);
    }
    
    private TableDao<Person> buildDao() throws Exception {
        DeletionFieldSupport<Person> support = new DeletionFieldSupport<>(Person.class, Person.PERSON.ProvinceID, DELETED, ACTIVE);
        logicDelDao = new LogicDeletionDao<>(getDbName(dbCategory), Person.class, support);
        return logicDelDao;
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
        super.dao.batchUpdate(builder);
    }

    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    @After
    public void tearDown() throws SQLException {
        String[] statements = new String[]{"DELETE FROM " + TABLE_NAME};

        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
        super.dao.batchUpdate(builder);
    }

    private void clearDeletionFlag(Person entity) {
        if(allowLogicDeletion)
            logicDelDao.clearDeletionFlag(entity);
    }

    private void clearDeletionFlag(List<Person > entities) {
        if(allowLogicDeletion)
            logicDelDao.clearDeletionFlag(entities);
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
            assertEquals("test", pk.getName());
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
            clearDeletionFlag(pk);
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
        
        for(Person pk: pl) {
            clearDeletionFlag(pk);
            assertNull(dao.queryByPk(pk));
        }
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
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        super.dao.execute(() -> {
            doInTransaction(p, false);
        });            
    }
    
    @Test
    public void testTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        super.dao.execute(() -> {
            doInTransaction(p, true);
        });            
    }
    
    @Test
    public void testCallableTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        List<Person> plistx = super.dao.execute(() -> {
            return doInTransaction(p, false);
        });            
        
        assertEquals(4, plistx.size());
    }

    @Test
    public void testCallableTransactionNest() throws Exception {
        PersonDefinition p = Person.PERSON;
        List<Person> plistx = super.dao.execute(() -> {
            return doInTransaction(p, true);
        });            
        
        assertEquals(4, plistx.size());
    }

    private List<Person> doInTransaction(PersonDefinition p, boolean nestTrans) throws SQLException {
        List<Person> plist = super.dao.query(selectAll(p));

        assertEquals(4, plist.size());
        
        testTransactionNestBatchDelete(plist, nestTrans);
        
        assertEquals(0, super.dao.query(selectAll(p)).size());
        
        testTransactionNestInsert(plist, nestTrans);
        
        assertEquals(4, super.dao.query(selectAll(p)).size());
        return plist;
    }

    private SqlBuilder selectAll(PersonDefinition p) {
        if(allowLogicDeletion)
            return selectAllFrom(p).where().allOf(p.PeopleID.gt(0), logicDelDao.getActiveCondition(p)).orderBy(p.PeopleID.asc()).into(Person.class);
        else
            return selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
    }
    
    private void testTransactionNestBatchDelete(List<Person> plist, boolean nestTrans) throws SQLException {
        PersonDefinition p = Person.PERSON;
        if(nestTrans)
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
        else
            super.dao.execute(() -> {
                assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                
                assertEquals(0, super.dao.query(selectAll(p)).size());
            });         
        clearDeletionFlag(plist);
    }
    
    private void testTransactionNestInsert(List<Person> plist, boolean nestTrans) throws SQLException {
        PersonDefinition p = Person.PERSON;
        if(nestTrans)
            assertEquals(4, dao.insert(plist));
        else
            super.dao.execute(() -> {
                assertEquals(4, dao.insert(plist));
                assertEquals(4, super.dao.query(selectAll(p)).size());
            });            
    }
}