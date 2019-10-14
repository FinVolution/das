package com.ppdai.das.client;

import static com.ppdai.das.client.SqlBuilder.insertInto;
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
public class TableDaoShardByDbTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionDbShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionDbShard";

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

    public TableDaoShardByDbTest(DatabaseCategory dbCategory, ShardInfoProvider provider, boolean allowLogicDeletion) throws Exception {
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
    
    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    public static interface ShardInfoProvider {
        void process(Person p, Hints hints, int dbShard);
        void where(SqlBuilder sb, int dbShard);
        SqlBuilder insert(int dbShard);
        SqlBuilder update(int dbShard);
        void inShard(Hints hints, int dbShard);
    }
    
    private static class DefaultProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard) {
            p.setCountryID(dbShard);
        }

        public void where(SqlBuilder sb, int dbShard) {
            sb.and(p.CountryID.eq(dbShard));
        }
        
        public SqlBuilder insert(int dbShard) {
            SqlBuilder builder = insertInto(p, p.Name, p.CountryID).values(p.Name.of("Jerry"), p.CountryID.of(dbShard));
            return builder;
        }
        
        public SqlBuilder update(int dbShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"), p.CountryID.eq(dbShard));
            return builder;
        }
        
        public void inShard(Hints hints, int dbShard) {}
    }
    
    private static class ShardIdProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard) {
            hints.inShard(dbShard);
        }

        public void where(SqlBuilder sb, int dbShard) {
            sb.hints().inShard(dbShard);
        }
        
        public SqlBuilder insert(int dbShard) {
            SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
            builder.hints().inShard(dbShard);
            return builder;
        }
        
        public SqlBuilder update(int dbShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"));
            builder.hints().inShard(dbShard);
            return builder;
        }
        
        public void inShard(Hints hints, int dbShard) {
            hints.inShard(dbShard);
        }
    }
    
    private static class ShardValueProvider implements ShardInfoProvider {
        public void process(Person p, Hints hints, int dbShard) {
            hints.shardValue(dbShard);
        }

        public void where(SqlBuilder sb, int dbShard) {
            sb.hints().shardValue(dbShard);
        }
        
        public SqlBuilder insert(int dbShard) {
            SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
            builder.hints().shardValue(dbShard);
            return builder;
        }
        
        public SqlBuilder update(int dbShard) {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"));
            builder.hints().shardValue(dbShard);
            return builder;
        }

        public void inShard(Hints hints, int dbShard) {
            hints.shardValue(dbShard);
        }
    }
    
    public void process(Person p, Hints hints, int i) {
        provider.process(p, hints, i);
    }
    
    public void process(Person p, Hints hints, int i, int j) {
        process(p, hints, i);
        p.setCityID(j);
    }
    
    public void where(SqlBuilder sb, int i) {
        provider.where(sb, i);
    }
    
    public Hints hints(int i) {
        return new Hints().inShard(i);
    }
    
    public Hints hints() {
        return new Hints();
    }
    
    private long count(int i) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.select("count(1)").from(p).intoObject();
        
        if(allowLogicDeletion)
            sb.where(logicDelDao.getActiveCondition(p));

        sb.hints().inShard(i);
        return ((Number)super.dao.queryObject(sb)).longValue();
    }
    
    @Before
    public void setup() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            String[] statements = new String[TABLE_MODE];
            for (int j = 0; j < TABLE_MODE; j++) {
                statements[j] = String.format("INSERT INTO person(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, 'test', %d, %d, 1)", j + 1, i, j);
            }

            if(!allowInsertWithId())
                statements = DbSetupUtil.handle("Person", statements);

            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            super.dao.batchUpdate(builder);
        }
    }
    
    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    @After
    public void tearDown() throws SQLException {
        for (int i = 0; i < DB_MODE; i++) {
            String[] statements = new String[] {"DELETE FROM " + TABLE_NAME};
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            super.dao.batchUpdate(builder);
        }
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
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                Hints hints = new Hints();
                process(pk, hints, i);
                pk = dao.queryByPk(pk, hints);
                assertNotNull(pk);
                assertEquals("test", pk.getName());
            }
        }
    }

    @Test
    public void testQueryBySample() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            Person pk = new Person();
            pk.setName("test");
            Hints hints = new Hints();
            process(pk, hints, i);
            List<Person> plist = dao.queryBySample(pk, hints);
            assertNotNull(plist);
            assertEquals(4, plist.size());
            assertEquals("test", pk.getName());
        }
    }
    
    @Test
    public void testQueryBySamplePage() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            List<Person> plist;
            Hints hints = new Hints();
            Person pk = new Person();
            pk.setName("test");

            
            process(pk, hints, i);
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
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, i, j);
                assertEquals(1, dao.insert(p, hints));
                p.setName(null);
                List<Person> plist = dao.queryBySample(p, hints(i));
                assertNotNull(plist);
                assertEquals(2, plist.size());
            }
        }
    }

    @Test
    public void testInsertWithId() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + j + 1);
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, i, j);
                assertEquals(1, dao.insert(p, hints.insertWithId()));
                p = dao.queryByPk(p, hints(i));
                assertNotNull(p);
            }
        }
    }

    @Test
    public void testInsertSetIdBack() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry" + j);
                Hints hints = new Hints();
                process(p, hints, i, j);
                assertEquals(1, dao.insert(p, hints.setIdBack()));
                assertNotNull(p.getPeopleID());
                p = dao.queryByPk(p, hints(i));
                assertNotNull(p);
                assertEquals("jerry" + j, p.getName());
            }
        }
    }

    @Test
    public void testInsertList() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            List<Person> pl = new ArrayList<>();
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, i, j);
                pl.add(p);
            }
            Person p = new Person();
            Hints hints = new Hints();
            process(p, hints, i);
            assertEquals(TABLE_MODE, dao.insert(pl, hints));
                
            assertEquals(TABLE_MODE * 2, count(i));
        }
    }

    private List<Person> getAll(int i) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = new SqlBuilder().select("*").from(p).into(Person.class);
        sb.hints().inShard(i);
        return super.dao.query(sb);
    }

    @Test
    public void testInsertListWithId() throws Exception {
        if(!allowInsertWithId())
            return;
        
        for(int i = 0; i < DB_MODE;i++) {
            List<Person> pl = new ArrayList<>();
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + j + 1);
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, i, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, i);
            
            assertEquals(TABLE_MODE, dao.insert(pl, hints.insertWithId()));

            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = new Person();
                p.setPeopleID(TABLE_MODE + k + 1);
                int id=p.getPeopleID();
                p = new Person();
                p.setPeopleID(id);
                process(p, hints, i, k);
                p = dao.queryByPk(p, hints);
//                p.setCountryID(i);
//                p = dao.queryByPk(p);
                assertNotNull(p);
            }
        }
    }

    @Test
    public void testInsertListSetIdBack() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int i = 0; i < DB_MODE;i++) {
            List<Person> pl = new ArrayList<>();
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry" + j);
                Hints hints = new Hints();
                process(p, hints, i, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, i);

            assertEquals(TABLE_MODE, dao.insert(pl, hints.setIdBack()));

            for(int k = 0; k < TABLE_MODE;k++) {
                Person p = pl.get(k);
                p.setName(null);
                assertNotNull(p.getPeopleID());

                int id=p.getPeopleID();
                p = new Person();
                p.setPeopleID(id);
                process(p, hints, i, k);
                p = dao.queryByPk(p, hints);
//                p.setCountryID(i);
//                p = dao.queryByPk(p);
                assertNotNull(p);
                assertEquals("jerry" + k, p.getName());
            }
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            List<Person> pl = new ArrayList<>();
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry");
                Hints hints = new Hints();
                process(p, hints, i, j);
                pl.add(p);
            }
            Hints hints = new Hints();
            process(new Person(), hints, i);

            int[] ret = dao.batchInsert(pl, hints);
            for(int x: ret)
                assertEquals(batchRet, x);
            
            assertEquals(TABLE_MODE, ret.length);
            
            Person p = new Person();
            p.setCountryID(i);

            List<Person> plist = getAll(i);
            assertNotNull(plist);
            assertEquals(TABLE_MODE * 2, plist.size());
            for(Person p1: plist)
                assertNotNull(p1.getPeopleID());    
        }
    }

    @Test
    public void testBatchInsert2() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person p = new Person();
                p.setName("jerry");
                p.setCountryID(i);
                p.setCityID(j);
                pl.add(p);
            }
        }
        
        int[] ret = dao.batchInsert(pl);
        for(int x: ret)
            assertEquals(batchRet, x);
        
        assertEquals(TABLE_MODE * 2, ret.length);

        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {

                Person p = new Person();
                p.setCountryID(i);
                p.setCityID(j);

                List<Person> plist = dao.queryBySample(p);
                assertNotNull(plist);
                assertEquals(2, plist.size());
                for(Person p1: plist)
                    assertNotNull(p1.getPeopleID());   
            }
        }
    }

    @Test
    public void testDeleteOne() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                Hints hints = new Hints();
                process(pk, hints, i, j);
                assertEquals(1, dao.deleteByPk(pk, hints));
                clearDeletionFlag(pk);
                assertNull(dao.queryByPk(pk, hints));
            }
        }
    }

    @Test
    public void testDeleteBySample() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            Person sample = new Person();
            Hints hints = new Hints();
            process(sample, hints, i);
            sample.setName("test");
            assertEquals(4, dao.deleteBySample(sample, hints));
            assertEquals(0, count(i));
        }
    }

    @Test
    public void testBatchDelete() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                pk.setCountryID(i);
                pk.setCityID(j);
                pl.add(pk);
            }
        }
        int[] ret = dao.batchDelete(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(TABLE_MODE * DB_MODE, ret.length);
        
        for(Person pk: pl) {
            clearDeletionFlag(pk);
            assertNull(dao.queryByPk(pk));
        }
    }

    @Test
    public void testBatchDeleteByShard() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            List<Person> pl = new ArrayList<>();
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                Hints hints = new Hints();
                process(pk, hints, i, j);
                pl.add(pk);
            }
            
            Hints hints = new Hints();
            process(new Person(), hints, i);
            int[] ret = dao.batchDelete(pl, hints);
            for(int k: ret)
                assertEquals(1, k);
            
            assertEquals(TABLE_MODE, ret.length);
            for(Person pk: pl) {
                clearDeletionFlag(pk);
                assertNull(dao.queryByPk(pk, hints(i)));
            }
        }
    }

    @Test
    public void testUpdate() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                pk.setName("Tom");
                Hints hints = new Hints();
                process(pk, hints, i, j);
                assertEquals(1, dao.update(pk, hints));
                assertEquals("Tom", dao.queryByPk(pk, hints).getName());
            }
        }
    }

    @Test
    public void testBatchUpdate() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                pk.setName("Tom");
                pk.setCountryID(i);
                pk.setCityID(j);
                pl.add(pk);
            }
        }
        int[] ret = dao.batchUpdate(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(TABLE_MODE * DB_MODE, ret.length);
        
        for(Person pk: pl)
            assertEquals("Tom", dao.queryByPk(pk).getName());
    }

    @Test
    public void testBatchUpdateByShard() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            List<Person> pl = new ArrayList<>();
            for (int j = 0; j < TABLE_MODE; j++) {
                Person pk = new Person();
                pk.setPeopleID(j + 1);
                pk.setName("Tom");
                Hints hints = new Hints();
                process(pk, hints, i, j);
                pl.add(pk);
            }
            
            Hints hints = new Hints();
            process(new Person(), hints, i);
            int[] ret = dao.batchUpdate(pl, hints);
            for(int k: ret)
                assertEquals(1, k);
            
            assertEquals(4, ret.length);
            for(Person pk: pl)
                assertEquals("Tom", dao.queryByPk(pk, hints).getName());
        }
    }

    @Test
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            int ifinal = i;
            Hints hints = new Hints();
            provider.inShard(hints, i);
            
            if(hints.getShard() == null && hints.getShardValue() == null)
                continue;

            super.dao.execute(() -> {
                doInTransaction(p, ifinal);
            }, hints);
            
            assertEquals(0, count(i));
        }        
    }

    @Test
    public void testTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            int ifinal = i;
            Hints hints = new Hints();
            provider.inShard(hints, i);
            
            if(hints.getShard() == null && hints.getShardValue() == null)
                continue;

            try {
                super.dao.execute(() -> {
                    doInTransaction(p, ifinal);
                    throw new RuntimeException("1");
                }, hints);
                fail();
            } catch (Exception e) {
            }
            
            assertEquals(4, count(i));
        }        
    }
    
    @Test
    public void testCallableTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            int ifinal = i;

            Hints hints = new Hints();
            provider.inShard(hints, i);
            
            if(hints.getShard() == null && hints.getShardValue() == null)
                continue;

            assertEquals(10,  (int)super.dao.execute(() -> {
                doInTransaction(p, ifinal);
                return 10;
            }, hints));
            
            assertEquals(0, count(i));
        }        
    }

    @Test
    public void testCallableTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            int ifinal = i;

            Hints hints = new Hints();
            provider.inShard(hints, i);
            
            if(hints.getShard() == null && hints.getShardValue() == null)
                continue;

            try {
                assertEquals(10,  (int)super.dao.execute(() -> {
                    doInTransaction(p, ifinal);
                    throw new RuntimeException("1");
                }, hints));
                fail();
            } catch (Exception e) {
            }
            
            assertEquals(4, count(i));
        }        
    }
    
    private SqlBuilder selectAll(PersonDefinition p, int i) {
        Object[] active = allowLogicDeletion ? logicDelDao.getActiveCondition(p) : null;
        return selectAllFrom(p).where(p.CountryID.eq(i)).appendWhen(allowLogicDeletion, SegmentConstants.AND, active).orderBy(p.PeopleID.asc()).into(Person.class);
    }

    private void doInTransaction(PersonDefinition p, int ifinal) throws SQLException {
        List<Person> plist = super.dao.query(selectAll(p, ifinal));

        assertEquals(4, plist.size());
        
        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
        clearDeletionFlag(plist);
        
        assertEquals(0, super.dao.query(selectAll(p, ifinal)).size());
        
        assertEquals(4, dao.insert(plist));
        plist = super.dao.query(selectAll(p, ifinal));
        assertEquals(4, plist.size());
        
        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
    }    
}
