package com.ppdai.das.client;

import static com.ppdai.das.client.ParameterDefinition.integerVar;
import static com.ppdai.das.client.ParameterDefinition.varcharVar;
import static com.ppdai.das.client.SqlBuilder.deleteFrom;
import static com.ppdai.das.client.SqlBuilder.insertInto;
import static com.ppdai.das.client.SqlBuilder.select;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.client.SqlBuilder.selectTop;
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
public class DasClientTableTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionTableShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionTableShard";

    private ShardInfoProvider provider;
    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {SqlServer, new DefaultProvider()},
            {SqlServer, new ShardIdProvider()},
            {SqlServer, new ShardValueProvider()},
            {MySql, new DefaultProvider()},
            {MySql, new ShardIdProvider()},
            {MySql, new ShardValueProvider()},
            });
        }

    public DasClientTableTest(DatabaseCategory dbCategory, ShardInfoProvider provider) throws SQLException {
        super(dbCategory);
        this.provider = provider;
    }
    
    public static DasClientTableTest of(DatabaseCategory dbCategory) throws SQLException {
        return new DasClientTableTest(dbCategory, new DefaultProvider());
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

    public SqlBuilder  where(SqlBuilder sb, int j) {
        provider.where(sb, 0, j);
        return sb;
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
        sb.hints().inTableShard(j);
        return ((Number)dao.queryObject(sb)).longValue();
    }

    private List<Person> getAll(int j) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("*").from(p).into(Person.class);
        sb.hints().inTableShard(j);
        return dao.query(sb);
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
            dao.batchUpdate(builder);
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
        dao.batchUpdate(builder);
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
            
            plist = dao.queryBySample(pk, PageRange.atPage(1, 2, p.CityID, p.CountryID), hints);
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

    @Test
    public void testBatchUpdateBuillder() throws Exception {
        for (int j = 0; j < TABLE_MODE; j++) {
            String[] statements = new String[]{
                    "INSERT INTO " + TABLE_NAME + "_" + j + "( Name, CityID, ProvinceID, CountryID)"
                            + " VALUES( 'test', 10, 1, 1)",
                    "INSERT INTO " + TABLE_NAME + "_" + j + "( Name, CityID, ProvinceID, CountryID)"
                            + " VALUES( 'test', 10, 1, 1)",
                    "INSERT INTO " + TABLE_NAME + "_" + j + "( Name, CityID, ProvinceID, CountryID)"
                            + " VALUES( 'test', 10, 1, 1)",};
    
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(0);
            int[] ret = dao.batchUpdate(builder);
            assertEquals(3, ret.length);
            for(int k: ret)
                assertEquals(1, k);
        }
    }
    
    @Test
    public void testBatchUpdateBuillderValue() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                p.Name, p.CityID, p.ProvinceID, p.CountryID);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }

    @Test
    public void testBatchUpdateBuillderDef() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                varcharVar("Name"), integerVar("CityID"), integerVar("ProvinceID"), integerVar("CountryID"));
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }

    @Test
    public void testBatchUpdateBuillderBuilder() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                insertInto(p, p.Name, p.CityID, p.ProvinceID, p.CountryID).values(
                        varcharVar("Name"), integerVar("CityID"), integerVar("ProvinceID"), integerVar("CountryID")));
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }

    @Test
    public void testBatchUpdateBuillderBuilderVar() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                insertInto(p, p.Name, p.CityID, p.ProvinceID, p.CountryID).values(
                        p.Name.var(), p.CityID.var(), p.ProvinceID.var(), p.CountryID.var()));
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }

    // For builder tests
    @Test
    public void testInsertBuilder() throws Exception {
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                SqlBuilder builder = provider.insert(0, j);
                assertEquals(1, dao.update(builder));
                
                assertEquals(TABLE_MODE + k + 1, count(j));
                Person pk = new Person();
                pk.setName("Jerry");
                List<Person> plist = dao.queryBySample(pk, hints(j));
                assertNotNull(plist);
                assertEquals(k + 1, plist.size());
            }
        }
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                SqlBuilder builder = provider.update(0, j).where(p.PeopleID.eq(k+1));
//                builder.update(Person.PERSON).set(p.Name.eq("Tom"), p.CountryID.eq(j), p.CityID.eq(j)).where(p.PeopleID.eq(j+1));
                
                assertEquals(1, dao.update(builder));
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                pk = dao.queryByPk(pk, hints(j));
                
                assertEquals("Tom", pk.getName());
            }
        }
    }

    @Test
    public void testDeleteBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for(int j = 0; j < TABLE_MODE;j++) {
            for(int k = 0; k < TABLE_MODE;k++) {
                SqlBuilder builder = deleteFrom(p).where(p.PeopleID.eq(k+1));
                where(builder, j);
                assertEquals(1, dao.update(builder));
                Person pk = new Person();
                pk.setPeopleID(k + 1);
                assertNull(dao.queryByPk(pk, hints(j)));
            }
        }
    }

    @Test
    public void testQueryObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(k+1)).into(Person.class);
                where(builder, j);
                Person pk = dao.queryObject(builder);
                assertNotNull(pk);
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals(j,  pk.getCityID().intValue());
            }
        }
    }

    @Test
    public void testQueryObjectPartial() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                SqlBuilder builder = select(p.PeopleID, p.CountryID, p.CityID).from(p).where(p.PeopleID.eq(k+1)).into(Person.class);
                where(builder, j);
                Person pk = dao.queryObject(builder);
                assertNotNull(pk);
                assertNull(pk.getName());
                assertNull(pk.getDataChange_LastTime());
                assertNull(pk.getProvinceID());
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals(j,  pk.getCityID().intValue());
            }
        }
    }

    @Test
    public void testQuerySimpleObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            for (int k = 0; k < TABLE_MODE; k++) {
                SqlBuilder builder = select(p.Name).from(p).where(p.PeopleID.eq(k+1)).into(String.class);
                where(builder, j);
                String name = dao.queryObject(builder);
                assertEquals("test", name);
            }
        }
    }

    @Test
    public void testQuery() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Integer> pks = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE; k++)
                pks.add(k+1);

            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.in(pks)).into(Person.class);
            where(builder, j);
            builder.orderBy(p.PeopleID.asc());
            
            List<Person> plist = dao.query(builder);

            assertEquals(4, plist.size());
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = plist.get(k);
                assertNotNull(p);
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals(j,  pk.getCityID().intValue());
            }
        }
    }

    @Test
    public void testQueryByPage() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Integer> pks = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE; k++)
                pks.add(k+1);

            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.in(pks)).into(Person.class);
            where(builder, j);
            builder.orderBy(p.PeopleID.asc());
            setuper.range(builder, 0, 3);
            List<Person> plist = dao.query(builder);
            
            assertEquals(3, plist.size());
            
            for (int k = 0; k < 3; k++) {
                Person pk = plist.get(k);
                assertNotNull(p);
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals(j,  pk.getCityID().intValue());
            }
            
            builder = selectAllFrom(p).where(p.PeopleID.in(pks)).into(Person.class);
            where(builder, j);
            builder.orderBy(p.PeopleID.asc());
            setuper.range(builder, 2, 4);
            plist = dao.query(builder);
            
            assertEquals(2, plist.size());
        }
    }

    @Test
    public void testQueryTopObj() throws Exception {
        if(dbCategory == MySql)
            return;
        
        for (int j = 0; j < TABLE_MODE; j++) {
            PersonDefinition p = Person.PERSON;
            SqlBuilder builder = selectTop(3, p.PeopleID).from(p).where(p.PeopleID.gt(-1));
            where(builder, j).orderBy(p.PeopleID.asc()).intoObject();
            List plist = dao.query(builder);
    
            assertEquals(3, plist.size());
            for(int i = 0; i < plist.size(); i++){
                assertEquals(i + 1, ((Number)plist.get(i)).intValue());
            }
        }
    }

    @Test
    public void testQueryTopEntity() throws Exception {
        if(dbCategory == MySql)
            return;

        for (int j = 0; j < TABLE_MODE; j++) {
            PersonDefinition p = Person.PERSON;
            SqlBuilder builder = selectTop(3, p.PeopleID, p.Name).from(p).where(p.PeopleID.gt(-1));
            where(builder, j).orderBy(p.PeopleID.asc()).into(Person.class);
            
            List<Person> plist = null;
            plist = dao.query(builder);
    
            assertEquals(3, plist.size());
            for(int i = 0; i < plist.size(); i++){
                assertEquals("test", plist.get(i).getName());
            }
        }
    }

    @Test
    public void testBatchQueryBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        BatchQueryBuilder batchBuilder = new BatchQueryBuilder();
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Integer> pks = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE; k++)
                pks.add(k+1);

            batchBuilder.addBatch(selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class));
        }
        
        List<List<Person>> plist = (List<List<Person>>)dao.batchQuery(batchBuilder);

        assertEquals(TABLE_MODE, plist.size());
        for (int j = 0; j < TABLE_MODE; j++) {
            List<Person> list = plist.get(j);
            for (int k = 0; k < TABLE_MODE; k++) {
                Person pk = list.get(k);
                assertNotNull(p);
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals("test",  pk.getName());
                assertEquals(j,  pk.getCityID().intValue());
            }
        }
    }

    @Test
    public void testBatchQueryBuilderByShard() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int j = 0; j < TABLE_MODE; j++) {
            BatchQueryBuilder batchBuilder = new BatchQueryBuilder();

            for (int k = 0; k < TABLE_MODE; k++) {
                batchBuilder.addBatch(selectAllFrom(p).where().allOf(p.PeopleID.eq(k+1)).orderBy(p.PeopleID.asc()).into(Person.class));
            }
            batchBuilder.hints().inTableShard(j);
            List<List<Person>> plist = (List<List<Person>>)dao.batchQuery(batchBuilder);

            assertEquals(TABLE_MODE, plist.size());
            for (int k = 0; k < TABLE_MODE; k++) {
                List<Person> list = plist.get(k);
                assertEquals(1, list.size());
                Person pk = list.get(0);
                assertNotNull(p);
                assertEquals(k+1,  pk.getPeopleID().intValue());
                assertEquals("test",  pk.getName());
                assertEquals(j,  pk.getCityID().intValue());
            }
        }
    }

    @Test
    public void testTransaction() throws Exception {
        PersonDefinition p = Person.PERSON;

        dao.execute(() -> {
            for (int j = 0; j < TABLE_MODE; j++) {
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                List<Person> plist = dao.query(builder);

                assertEquals(4, plist.size());
                
                assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                
                builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                assertEquals(0, dao.query(builder).size());
                
                assertEquals(4, dao.insert(plist));
                builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                plist = dao.query(builder);
                assertEquals(4, plist.size());
                
                assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            }
        });
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(0, count(j));
    }

    @Test
    public void testTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;

        try {
            dao.execute(() -> {
                for (int j = 0; j < TABLE_MODE; j++) {
                    SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    List<Person> plist = dao.query(builder);
    
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    
                    builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    assertEquals(0, dao.query(builder).size());
                    
                    assertEquals(4, dao.insert(plist));
                    builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    plist = dao.query(builder);
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    throw new RuntimeException("1");
                }
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
        assertEquals(10,  (int)dao.execute(() -> {
            List<Person> plist = null;
            for (int j = 0; j < TABLE_MODE; j++) {
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                plist = dao.query(builder);

                assertEquals(4, plist.size());
                
                assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                
                builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                assertEquals(0, dao.query(builder).size());
                
                assertEquals(4, dao.insert(plist));
                builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                plist = dao.query(builder);
                assertEquals(4, plist.size());
                
                assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
            }
            return 10;
        }));
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(0, count(j));
    }
    
    @Test
    public void testCallableTransactionRollback() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            assertEquals(10,  (int)dao.execute(() -> {
                List<Person> plist = null;
                for (int j = 0; j < TABLE_MODE; j++) {
                    SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    plist = dao.query(builder);
    
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    
                    builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    assertEquals(0, dao.query(builder).size());
                    
                    assertEquals(4, dao.insert(plist));
                    builder = selectAllFrom(p).where().allOf(p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    plist = dao.query(builder);
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    throw new RuntimeException("1");
                }
                return 10;
            }));
            fail();
        } catch (Exception e) {
        }
        
        for (int j = 0; j < TABLE_MODE; j++)
            assertEquals(4, count(j));
    }
}
