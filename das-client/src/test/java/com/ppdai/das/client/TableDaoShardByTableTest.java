package com.ppdai.das.client;

import static com.ppdai.das.client.SqlBuilder.insertInto;
import static com.ppdai.das.client.SqlBuilder.select;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;

@RunWith(Parameterized.class)
public class TableDaoShardByTableTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionTableShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionTableShard";

    private ShardInfoProvider provider;
    private static PersonDefinition p = Person.PERSON;

    private final static Integer DELETED = 0;
    private final static Integer ACTIVE = 1;

    private TableDao<Person> dao;
    private boolean allowLogicDeletion;
    private LogicDeletionDao<Person> logicDelDao;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {SqlServer, new DefaultProvider(), false},
            {SqlServer, new ShardIdProvider(), false},
            {SqlServer, new ShardValueProvider(), false},
            {MySql, new DefaultProvider(), false},
            {MySql, new ShardIdProvider(), false},
            {MySql, new ShardValueProvider(), false},
            
            {SqlServer, new DefaultProvider(), true},
            {SqlServer, new ShardIdProvider(), true},
            {SqlServer, new ShardValueProvider(), true},
            {MySql, new DefaultProvider(), true},
            {MySql, new ShardIdProvider(), true},
            {MySql, new ShardValueProvider(), true},
            });
        }

    public TableDaoShardByTableTest(DatabaseCategory dbCategory, ShardInfoProvider provider, boolean allowLogicDeletion) throws Exception {
        super(dbCategory);
        this.allowLogicDeletion = allowLogicDeletion;

        this.provider = provider;
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
    
    private void clearDeletionFlag(Person entity) {
        if(allowLogicDeletion)
            logicDelDao.clearDeletionFlag(entity);
    }

    private void clearDeletionFlag(List<Person > entities) {
        if(allowLogicDeletion)
            logicDelDao.clearDeletionFlag(entities);
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    public static interface ShardInfoProvider {
        void process(Person p, Hints hints, int dbShard, int tableShard);
        void where(SqlBuilder sb, int dbShard, int tableShard);
        SqlBuilder insert(int dbShard, int tableShard);
        SqlBuilder update(int dbShard, int tableShard);
        void inShard(Hints hints, int dbShard);
    }
    
    private static class DefaultProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard, int tableShard) {
            p.setCountryID(dbShard);
            p.setCityID(tableShard);
        }

        public void where(SqlBuilder sb, int dbShard, int tableShard) {
            sb.and(p.CountryID.eq(dbShard)).and(p.CityID.eq(tableShard));
        }
        
        public SqlBuilder insert(int dbShard, int tableShard) {
            SqlBuilder builder = insertInto(p, p.Name, p.CountryID, p.CityID).values(p.Name.of("Jerry"), p.CountryID.of(dbShard), p.CityID.of(tableShard));
            return builder;
        }
        
        public SqlBuilder update(int dbShard, int tableShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"), p.CountryID.eq(dbShard), p.CityID.eq(tableShard));
            return builder;
        }
        
        public void inShard(Hints hints, int dbShard) {}
    }
    
    private static class ShardIdProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard, int tableShard) {
            hints.inShard(dbShard).inTableShard(tableShard);
        }

        public void where(SqlBuilder sb, int dbShard, int tableShard) {
            sb.hints().inShard(dbShard).inTableShard(tableShard);
        }
        
        public SqlBuilder insert(int dbShard, int tableShard) {
            SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
            builder.hints().inShard(dbShard).inTableShard(tableShard);
            return builder;
        }
        
        public SqlBuilder update(int dbShard, int tableShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"));
            builder.hints().inShard(dbShard).inTableShard(tableShard);
            return builder;
        }
        
        public void inShard(Hints hints, int dbShard) {
            hints.inShard(dbShard);
        }
    }
    
    private static class ShardValueProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard, int tableShard) {
            hints.shardValue(dbShard).tableShardValue(tableShard);
        }

        public void where(SqlBuilder sb, int dbShard, int tableShard) {
            sb.hints().shardValue(dbShard).tableShardValue(tableShard);
        }
        
        public SqlBuilder insert(int dbShard, int tableShard) {
            SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
            builder.hints().shardValue(dbShard).tableShardValue(tableShard);
            return builder;
        }
        
        public SqlBuilder update(int dbShard, int tableShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"));
            builder.hints().shardValue(dbShard).tableShardValue(tableShard);
            return builder;
        }

        public void inShard(Hints hints, int dbShard) {
            hints.shardValue(dbShard);
        }
    }
    
    public void process(Person p, Hints hints, int j) {
        provider.process(p, hints, 0, j);
    }

    public void where(SqlBuilder sb, int j) {
        provider.where(sb, 0, j);
    }
    
    public Hints hints(int j) {
        return new Hints().inTableShard(j);
    }
    
    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    private long count(int j) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("count(1)").from(p).intoObject();
        
        if(allowLogicDeletion)
            sb.where(logicDelDao.getActiveCondition(p));
        
        sb.hints().inTableShard(j);
        return ((Number)super.dao.queryObject(sb)).longValue();
    }

    private List<Person> getAll(int j) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("*").from(p).into(Person.class);

        if(allowLogicDeletion)
            sb.where(logicDelDao.getActiveCondition(p));

        sb.hints().inTableShard(j);
        return super.dao.query(sb);
    }

    @Before
    public void setup() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            String[] statements = new String[TABLE_MODE];
            for (int k = 0; k < TABLE_MODE; k++) {
                statements[k] = String.format("INSERT INTO person_%d(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, 'test', %d, %d, 1)", j, k + 1, 0, j);
            }

            if(!allowInsertWithId())
                statements = DbSetupUtil.handle(String.format("Person_%d", j), statements);
            
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inTableShard(j);
            super.dao.batchUpdate(builder);
        }
    }

    @After
    public void tearDown() throws SQLException {
        String[] statements = new String[TABLE_MODE + 1];
        for (int j = 0; j < TABLE_MODE; j++) {
            statements[j] = "DELETE FROM " + TABLE_NAME + "_" + j;
        }
        statements[4] = "DELETE FROM " + TABLE_NAME;

        BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
        builder.hints();
        super.dao.batchUpdate(builder);
    }

    @Test
    public void testQueryById() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                Hints hints = new Hints();
                process(pk, hints, j);
                pk = dao.queryByPk(pk, hints);
                assertNotNull(pk);
                assertEquals("test", pk.getName());
            }
        }
    }

    @Test
    public void testQueryBySample() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            Person pk = new Person();
            pk.setName("test");
            Hints hints = new Hints();
            process(pk, hints, j);
            List<Person> plist = dao.queryBySample(pk, hints);
            assertNotNull(plist);
            assertEquals(4, plist.size());
            assertEquals("test", pk.getName());
        }
    }

    @Test
    public void testQueryBySamplePage() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> plist;
            Hints hints = new Hints();
            Person pk = new Person();
            pk.setName("test");

            
            process(pk, hints, j);
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID), hints);
            assertList(4, plist);
            
            plist = dao.queryBySample(pk, PageRange.atPage(2, 2, p.CityID, p.CountryID), hints);
            assertList(2, plist);
            
            if(dbCategory == DatabaseCategory.MySql) {
                plist = dao.queryBySample(pk, PageRange.atPage(3, 2), hints);
                assertList(0, plist);
            }
        
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc()), hints);
            assertList(4, plist);
            assertOrder(plist, true);
            
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.desc()), hints);
            assertList(4, plist);
            assertOrder(plist, false);
            
            plist = dao.queryBySample(pk, PageRange.atPage(1, 10, p.PeopleID.asc(), p.CityID.desc()), hints);
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
    public void testInsertOne() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, j);
                assertEquals(1, dao.insert(p, hints));
                List<Person> plist = dao.queryBySample(p, hints(j));
                assertNotNull(plist);
                assertEquals(k + 1, plist.size());
            }
        }
    }

    @Test
    public void testInsertWithId() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + k + 1);
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, j);
                assertEquals(1, dao.insert(p, hints.insertWithId()));
                p = dao.queryByPk(p, hints(j));
                assertNotNull(p);
            }
        }
    }

    @Test
    public void testInsertSetIdBack() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry" + k);
                Hints hints = new Hints();
                process(p, hints, j);
                assertEquals(1, dao.insert(p, hints.setIdBack()));
                assertNotNull(p.getPeopleID());
                p = dao.queryByPk(p, hints(j));
                assertNotNull(p);
                assertEquals("jerry" + k, p.getName());
            }
        }
    }

    @Test
    public void testInsertList() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> pl = new ArrayList<>();
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, j);
            assertEquals(TABLE_MODE, dao.insert(pl, hints));
            
            assertEquals(TABLE_MODE * 2, count(j));                
        }
    }

    @Test
    public void testInsertListWithId() throws Exception {
        if(!allowInsertWithId())
            return;
        
        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> pl = new ArrayList<>();
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + k + 1);
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, j);
            assertEquals(TABLE_MODE, dao.insert(pl, hints.insertWithId()));

            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + k + 1);
                int id=p.getPeopleID();
                p = new Person();
                p.setPeopleID(id);
                process(p, hints, j);
                p = dao.queryByPk(p, hints);
//                p.setCityID(j);
//                p = dao.queryByPk(p);
                assertNotNull(p);
            }
        }
    }

    @Test
    public void testInsertListSetIdBack() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> pl = new ArrayList<>();
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry" + k);
                Hints hints = new Hints();
                process(p, hints, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, j);
            assertEquals(TABLE_MODE, dao.insert(pl, hints.setIdBack()));

            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = pl.get(k);
                p.setName(null);
                assertNotNull(p.getPeopleID());

                int id=p.getPeopleID();
                p = new Person();
                p.setPeopleID(id);
                process(p, hints, j);
                p = dao.queryByPk(p, hints);
//                p.setCountryID(j);
//                p.setCityID(j);
//                p = dao.queryByPk(p);
                assertNotNull(p);
                assertEquals("jerry" + k, p.getName());
            }
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            List<Person> pl = new ArrayList<>();
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, j);
            int[] ret = dao.batchInsert(pl, hints);
            for(int x: ret)
                assertEquals(batchRet, x);
            
            assertEquals(TABLE_MODE, ret.length);
            
            Person p = new Person();
            p.setName("jerry");
            p.setCountryID(j);
            p.setCityID(j);

            List<Person> plist = getAll(j);
            assertNotNull(plist);
            assertEquals(TABLE_MODE * 2, plist.size());
            
            for(Person p1: plist)
                assertNotNull(p1.getPeopleID());
        }
    }

    @Test
    public void testBatchInsert2() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setName("jerry");
                p.setCountryID(j);
                p.setCityID(j);
                pl.add(p);
            }
        }
        
        int[] ret = dao.batchInsert(pl);
        for(int x: ret)
            assertEquals(batchRet, x);
        
        assertEquals(TABLE_MODE * TABLE_MODE, ret.length);

        for(int j = 0; j < TABLE_MODE;j++) {

            Person p = new Person();
            p.setName("jerry");
            p.setCityID(j);

            List<Person> plist = dao.queryBySample(p);
            assertNotNull(plist);
            assertEquals(TABLE_MODE, plist.size());                
        }
    }

    @Test
    public void testDeleteOne() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                Hints hints = new Hints();
                process(pk, hints, j);
                assertEquals(1, dao.deleteByPk(pk, hints));
                clearDeletionFlag(pk);
                assertNull(dao.queryByPk(pk, hints(j)));
            }
        }
    }

    @Test
    public void testDeleteBySample() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            Person sample = new Person();
            sample.setName("test");
            Hints hints = new Hints();
            process(sample, hints, j);
            clearDeletionFlag(sample);
            assertEquals(4, dao.deleteBySample(sample, hints));
            assertEquals(0, count(j));
        }
    }

    @Test
    public void testDeleteBySample2() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            Person sample = new Person();
            sample.setCountryID(0);
            Hints hints = new Hints();
            process(sample, hints, j);
            clearDeletionFlag(sample);
            assertEquals(4, dao.deleteBySample(sample, hints));
            assertEquals(0, count(j));
        }
    }

    @Test
    public void testBatchDelete() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                pk.setCountryID(j);
                pk.setCityID(j);
                pl.add(pk);
            }
        }

        int[] ret = dao.batchDelete(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(TABLE_MODE * TABLE_MODE, ret.length);
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(0, count(j));
    }

    @Test
    public void testBatchDeleteX() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Person> pl = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                Hints hints = new Hints();
                process(pk, hints, j);
                pl.add(pk);
            }
            
            Hints hints = new Hints();
            process(new Person(), hints, j);
            int[] ret = dao.batchDelete(pl, hints);
            for(int k: ret)
                assertEquals(1, k);
            
            assertEquals(4, ret.length);
            
            assertEquals(0, count(j));
        }
    }

    @Test
    public void testUpdate() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                pk.setName("Tom");
                Hints hints = new Hints();
                process(pk, hints, j);
                assertEquals(1, dao.update(pk, hints));
                assertEquals("Tom", dao.queryByPk(pk, hints(j)).getName());
            }
        }
    }

    @Test
    public void testBatchUpdateX() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                pk.setName("Tom");
                pk.setCountryID(j);
                pk.setCityID(j);
                pl.add(pk);
            }
        }

        int[] ret = dao.batchUpdate(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(TABLE_MODE * TABLE_MODE, ret.length);
        
        for(Person pk: pl)
            assertEquals("Tom", dao.queryByPk(pk).getName());
    }

    @Test
    public void testBatchUpdate() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Person> pl = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                pk.setName("Tom");
                Hints hints = new Hints();
                process(pk, hints, j);
                pl.add(pk);
            }
            
            Hints hints = new Hints();
            process(new Person(), hints, j);
            int[] ret = dao.batchUpdate(pl, hints);
            for(int k: ret)
                assertEquals(1, k);
            
            assertEquals(4, ret.length);
            for(Person pk: pl)
                assertEquals("Tom", dao.queryByPk(pk, hints).getName());
        }
    }

    private SqlBuilder selectAll(PersonDefinition p, int j) {
        Object[] active = allowLogicDeletion ? logicDelDao.getActiveCondition(p) : null;
        SqlBuilder builder = selectAllFrom(p).where(p.CityID.eq(j)).appendWhen(allowLogicDeletion, SegmentConstants.AND, active).orderBy(p.PeopleID.asc()).into(Person.class);
        return builder;
    }

    @Test
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;

        super.dao.execute(() -> {
            doInTransaction(p);
        });
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(0, count(j));
    }

    private int doInTransaction(PersonDefinition p) throws SQLException {
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Person> plist = super.dao.query(selectAll(p, j));

            assertEquals(4, plist.size());
            
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            clearDeletionFlag(plist);
            
            assertEquals(0, super.dao.query(selectAll(p, j)).size());
            
            assertEquals(4, dao.insert(plist));
            plist = super.dao.query(selectAll(p, j));
            assertEquals(4, plist.size());
            
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
        }
        
        return 10;
    }

    @Test
    public void testTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;

        try {
            super.dao.execute(() -> {
                doInTransaction(p);
                throw new RuntimeException("1");
            });
            fail();
        } catch (Exception e) {
        }

        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(4, count(j));
    }

    @Test
    public void testCallableTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        assertEquals(10,  (int)super.dao.execute(() -> {
            return doInTransaction(p);
        }));
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(0, count(j));
    }
    
    @Test
    public void testCallableTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            assertEquals(10,  (int)super.dao.execute(() -> {
                doInTransaction(p);
                throw new RuntimeException("1");
            }));
            fail();
        } catch (Exception e) {
        }
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(4, count(j));
    }
}
