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
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;

import javax.persistence.Column;


@RunWith(Parameterized.class)
public class DasClientDbTableTest extends DataPreparer {
    public final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionDbTableShard";
    public final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionDbTableShard";

    private ShardInfoProvider provider;
    private static PersonDefinition p = Person.PERSON;

    private static String testDate = "2019-11-11 11:11:11";
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public DasClientDbTableTest(DatabaseCategory dbCategory, ShardInfoProvider provider) throws SQLException {
        super(dbCategory);
        this.provider = provider;
    }
    
    public static DasClientDbTableTest of(DatabaseCategory dbCategory) throws SQLException {
        return new DasClientDbTableTest(dbCategory, new DefaultProvider());
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    public static interface ShardInfoProvider {
        void process(Person p, Hints hints, int dbShard, int tableShard);
        void where(SqlBuilder sb, int dbShard, int tableShard);
        SqlBuilder insert(int dbShard, int tableShard) throws ParseException;
        SqlBuilder update(int dbShard, int tableShard) throws ParseException;
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
        
        public SqlBuilder insert(int dbShard, int tableShard) throws ParseException {
            return insertInto(p, p.Name, p.CountryID, p.CityID, p.DataChange_LastTime)
                    .values(p.Name.of("Jerry"), p.CountryID.of(dbShard), p.CityID.of(tableShard), p.DataChange_LastTime.of(SDF.parse(testDate)));
        }
        
        public SqlBuilder update(int dbShard, int tableShard) throws ParseException {
            return SqlBuilder.update(Person.PERSON)
                    .set(p.Name.eq("Tom"), p.CountryID.eq(dbShard), p.CityID.eq(tableShard), p.DataChange_LastTime.eq(SDF.parse(testDate)))
                    .where(p.PeopleID.eq(tableShard+1));
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
        
        public SqlBuilder update(int dbShard, int tableShard) throws ParseException {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON)
                    .set(p.Name.eq("Tom"), p.DataChange_LastTime.eq(SDF.parse(testDate)))
                    .where(p.PeopleID.eq(tableShard+1));
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
        
        public SqlBuilder insert(int dbShard, int tableShard) throws ParseException {
            SqlBuilder builder = insertInto(p, p.Name, p.DataChange_LastTime).values(p.Name.of("Jerry"), p.DataChange_LastTime.of(SDF.parse(testDate)));
            builder.hints().shardValue(dbShard).tableShardValue(tableShard);
            return builder;
        }
        
        public SqlBuilder update(int dbShard, int tableShard) throws ParseException {
            SqlBuilder builder = SqlBuilder.update(Person.PERSON).set(p.Name.eq("Tom"), p.DataChange_LastTime.eq(SDF.parse(testDate)))
                    .where(p.PeopleID.eq(tableShard+1));
            builder.hints().shardValue(dbShard).tableShardValue(tableShard);
            return builder;
        }

        public void inShard(Hints hints, int dbShard) {
            hints.shardValue(dbShard);
        }
    }
    
    public void process(Person p, Hints hints, int i, int j) {
        provider.process(p, hints, i, j);
    }

    public SqlBuilder where(SqlBuilder sb, int i, int j) {
        provider.where(sb, i, j);
        return sb;
    }
    
    public Hints hints(int i, int j) {
        return new Hints().inShard(i).inTableShard(j);
    }


    @BeforeClass
    public static void setupDataBase() throws SQLException {
    }

    @Before
    public void setup() throws Exception {
        tearDown();

        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String[] statements = new String[TABLE_MODE];
                for (int k = 0; k < TABLE_MODE; k++) {
                    statements[k] = String.format(
                            "INSERT INTO Person_%d(PeopleID, Name, CountryID, CityID, ProvinceID, DataChange_LastTime )" +
                                    " VALUES(%d, 'test', %d, %d, 1, '" + testDate + "')", j, k + 1, i, j);
                }
                
                if(!allowInsertWithId())
                    statements = DbSetupUtil.handle(String.format("Person_%d", j), statements);
                
                BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
                builder.hints().inShard(i);
                dao.batchUpdate(builder);
            }
        }
    }

    // For Sqlserver 2014 express, it seems you can not SET IDENTITY_INSERT between two SQL
    // Should try evaluation or formal version
    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    @After
    public void tearDown() throws SQLException {
        for (int i = 0; i < DB_MODE; i++) {
            String[] statements = new String[TABLE_MODE + 1];
            for (int j = 0; j < TABLE_MODE; j++) {
                statements[j] = "DELETE FROM " + TABLE_NAME + "_" + j;
            }
            statements[4] = "DELETE FROM " + TABLE_NAME;

            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            dao.batchUpdate(builder);
        }
    }

    @Test
    public void testQueryById() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    Hints hints = new Hints();
                    process(pk, hints, i, j);
                    pk = dao.queryByPk(pk, hints);
                    assertNotNull(pk);
                    assertEquals("test", pk.getName());
                    assertEquals(SDF.parse(testDate), pk.getDataChange_LastTime());
                }
            }
        }
    }


    @Test
    public void testQueryBySample() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person pk = new Person();
                pk.setName("test");
                Hints hints = new Hints();
                process(pk, hints, i, j);
                List<Person> plist = dao.queryBySample(pk, hints);
                assertNotNull(plist);
                assertEquals(4, plist.size());
                assertEquals("test", pk.getName());
            }
        }
    }

    @Test
    public void testQueryBySampleByDate() throws Exception {
        Date date = SDF.parse(testDate);
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                Person pk = new Person();
                pk.setDataChange_LastTime(date);
                Hints hints = new Hints();
                process(pk, hints, i, j);
                List<Person> plist = dao.queryBySample(pk, hints);
                assertNotNull(plist);
                assertEquals(4, plist.size());
                assertEquals(date, pk.getDataChange_LastTime());
            }
        }
    }


    @Test
    public void testQueryBySamplePage() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                List<Person> plist;
                Hints hints = new Hints();
                Person pk = new Person();
                pk.setName("test");
    
                
                process(pk, hints, i, j);
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
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry");
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    assertEquals(1, dao.insert(p, hints));
                    List<Person> plist = dao.queryBySample(p, hints(i, j));
                    assertNotNull(plist);
                    assertEquals(k + 1, plist.size());
                }
            }
        }
    }


    @Test
    public void testInsertWithId() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setPeopleID(TABLE_MODE + k + 1);
                    p.setName("jerry");
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    assertEquals(1, dao.insert(p, hints.insertWithId()));
                    p = dao.queryByPk(p, hints(i, j));
                    assertNotNull(p);
                }
            }
        }
    }

    @Test
    public void testInsertSetIdBack() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry" + k);
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    assertEquals(1, dao.insert(p, hints.setIdBack()));
                    assertNotNull(p.getPeopleID());
                    p = dao.queryByPk(p, hints(i, j));
                    assertNotNull(p);
                    assertEquals("jerry" + k, p.getName());
                }
            }
        }
    }

    @Test
    public void testInsertList() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                List<Person> pl = new ArrayList<>();
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry");
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    pl.add(p);
                }
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                assertEquals(TABLE_MODE, dao.insert(pl, hints));
                
                assertEquals(TABLE_MODE * 2, count(i, j));                
            }
        }
    }
    
    private long count(int i, int j) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("count(1)").from(p.inShard(String.valueOf(j))).intoObject();
        sb.hints().inShard(i);
        return ((Number)dao.queryObject(sb)).longValue();
    }

    private List<Person> getAll(int i, int j) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("*").from(p.inShard(String.valueOf(j))).into(Person.class);
        sb.hints().inShard(i);
        return dao.query(sb);
    }

    @Test
    public void testInsertListWithId() throws Exception {
        if(!allowInsertWithId())
            return;
        
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                List<Person> pl = new ArrayList<>();
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setPeopleID(TABLE_MODE + k + 1);
                    p.setName("jerry");
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    pl.add(p);
                }
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                
                assertEquals(TABLE_MODE, dao.insert(pl, hints.insertWithId()));

                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setPeopleID(TABLE_MODE + k + 1);
                    
                    int id=p.getPeopleID();
                    p = new Person();
                    p.setPeopleID(id);
                    process(p, hints, i, j);
                    p = dao.queryByPk(p, hints);
//                      
//                    this is the old version of queryByPk, allow wrong shard field and irrelevent fields
//                    p.setCountryID(i);
//                    p.setCityID(j);
//                    p = dao.queryByPk(p);
                    assertNotNull(p);
                }
                
            }
        }
    }

    @Test
    public void testInsertListSetIdBack() throws Exception {
        if(!allowInsertWithId())
            return;

        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                List<Person> pl = new ArrayList<>();
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry" + k);
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    pl.add(p);
                }
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                assertEquals(TABLE_MODE, dao.insert(pl, hints.setIdBack()));

                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = pl.get(k);
                    p.setName(null);
                    assertNotNull(p.getPeopleID());

                    int id=p.getPeopleID();
                    p = new Person();
                    p.setPeopleID(id);
                    process(p, hints, i, j);
                    p = dao.queryByPk(p, hints);
//                    p.setCountryID(i);
//                    p.setCityID(j);
//                    p = dao.queryByPk(p);
                    assertNotNull(p);
                    assertEquals("jerry" + k, p.getName());
                }
            }
        }
    }

    @Test
    public void testBatchInsert() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                List<Person> pl = new ArrayList<>();
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry");
                    Hints hints = new Hints();
                    process(p, hints, i, j);
                    pl.add(p);
                }
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                int[] ret = dao.batchInsert(pl, hints);
                for(int x: ret)
                    assertEquals(batchRet, x);
                
                assertEquals(TABLE_MODE, ret.length);

                List<Person> plist = getAll(i, j);
                assertNotNull(plist);
                assertEquals(TABLE_MODE * 2, plist.size());
                
                for(Person p1: plist)
                    assertNotNull(p1.getPeopleID());
            }
        }
    }

    @Test
    public void testBatchInsert2() throws Exception {
        List<Person> pl = new ArrayList<>();
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                for(int k = 0; k < TABLE_MODE;k++) {
                    Person p = new Person();
                    p.setName("jerry");
                    p.setCountryID(i);
                    p.setCityID(j);
                    pl.add(p);
                }
            }
        }
        
        int[] ret = dao.batchInsert(pl);
        for(int x: ret)
            assertEquals(batchRet, x);
        
        assertEquals(DB_MODE * TABLE_MODE * TABLE_MODE, ret.length);

        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {

                Person p = new Person();
                p.setCountryID(i);
                p.setCityID(j);

                List<Person> plist = dao.queryBySample(p);
                assertNotNull(plist);
                assertEquals(TABLE_MODE * 2, plist.size());                
            }
        }
    }

    @Test
    public void testDeleteOne() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    Hints hints = new Hints();
                    process(pk, hints, i, j);
                    assertEquals(1, dao.deleteByPk(pk, hints));
                    assertNull(dao.queryByPk(pk, hints));
                }
            }
        }
    }

    @Test
    public void testDeleteBySample() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                Person sample = new Person();
                Hints hints = new Hints();
                process(sample, hints, i, j);
                sample.setName("test");
                assertEquals(4, dao.deleteBySample(sample, hints));
                assertEquals(0, count(i, j));
            }
        }
    }

    @Test
    public void testBatchDelete() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    pk.setCountryID(i);
                    pk.setCityID(j);
                    pl.add(pk);
                }
            }
        }
        int[] ret = dao.batchDelete(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(32, ret.length);
        
        for(Person pk: pl)
            assertNull(dao.queryByPk(pk));
    }

    @Test
    public void testBatchDeleteByShard() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Person> pl = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    Hints hints = new Hints();
                    process(pk, hints, i, j);
                    pl.add(pk);
                }
                
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                int[] ret = dao.batchDelete(pl, hints);
                for(int k: ret)
                    assertEquals(1, k);
                
                assertEquals(4, ret.length);
                for(Person pk: pl)
                    assertNull(dao.queryByPk(pk, hints));
            }
        }
    }

    @Test
    public void testUpdate() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    pk.setName("Tom");
                    Hints hints = new Hints();
                    process(pk, hints, i, j);
                    assertEquals(1, dao.update(pk, hints));
                    assertEquals("Tom", dao.queryByPk(pk, hints).getName());
                }
            }
        }
    }

    @Test
    public void testBatchUpdateX() throws Exception {
        List<Person> pl = new ArrayList<>();
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    pk.setName("Tom");
                    pk.setCountryID(i);
                    pk.setCityID(j);
                    pl.add(pk);
                }
            }
        }
        int[] ret = dao.batchUpdate(pl);
        for(int i: ret)
            assertEquals(1, i);
        
        assertEquals(32, ret.length);
        
        for(Person pk: pl)
            assertEquals("Tom", dao.queryByPk(pk).getName());
    }

    @Test
    public void testBatchUpdate() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Person> pl = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    pk.setName("Tom");
                    Hints hints = new Hints();
                    process(pk, hints, i, j);
                    pl.add(pk);
                }
                
                Hints hints = new Hints();
                process(new Person(), hints, i, j);
                int[] ret = dao.batchUpdate(pl, hints);
                for(int k: ret)
                    assertEquals(1, k);
                
                assertEquals(4, ret.length);
                for(Person pk: pl)
                    assertEquals("Tom", dao.queryByPk(pk, hints).getName());
            }
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
        builder.hints().inShard(0);
        int[] ret = dao.batchUpdate(builder);
    }
    
    @Test
    public void testBatchUpdateBuillderValue() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                "INSERT INTO " + TABLE_NAME + "( Name, CityID, ProvinceID, CountryID) VALUES( ?, ?, ?, ?)",
                p.Name, p.CityID, p.ProvinceID, p.CountryID);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inShard(0);
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
        
        builder.hints().inShard(0);
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
        
        builder.hints().inShard(0).inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }

    @Test
    public void testBatchUpdateSet() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                SqlBuilder.update(p).set(
                        p.Name.eq(varcharVar("Name")),
                        p.CityID.eq(integerVar("CityID")),
                        p.ProvinceID.eq(integerVar("ProvinceID")),
                        p.CountryID.eq(integerVar("CountryID"))));
                ;
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.hints().inShard(0).inTableShard(0);
        int [] ret = dao.batchUpdate(builder);

        for(int r : ret){
            assertTrue(r > 0);
        }
    }

    @Test
    public void testBatchUpdateBuillderBuilderVar() throws Exception {
        BatchUpdateBuilder builder = new BatchUpdateBuilder(
                insertInto(p, p.Name, p.CityID, p.ProvinceID, p.CountryID).values(
                        p.Name.var(), p.CityID.var(), p.ProvinceID.var(), p.CountryID.var()));
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        builder.addBatch("test", 10, 1, 1);
        
        builder.hints().inShard(0).inTableShard(0);
        assertArrayEquals(new int[] {batchRet,  batchRet, batchRet}, dao.batchUpdate(builder));
    }
    
    // For builder tests
    @Test
    public void testInsertBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        for(int i = 0; i < DB_MODE;i++) {
            for(int j = 0; j < TABLE_MODE;j++) {
                for(int k = 0; k < TABLE_MODE;k++) {
                    SqlBuilder builder = provider.insert(i, j);
                    assertEquals(1, dao.update(builder));
                    
                    assertEquals(TABLE_MODE + k + 1, count(i, j));
                    Person pk = new Person();
                    pk.setName("Jerry");
                    List<Person> plist = dao.queryBySample(pk, hints(i, j));
                    assertNotNull(plist);
                    assertEquals(k + 1, plist.size());
                }
            }
        }
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    SqlBuilder builder = provider.update(i, j);

                    assertEquals(1, dao.update(builder));
                    Person pk = new Person();
                    pk.setPeopleID(j + 1);
                    pk = dao.queryByPk(pk, hints(i, j));
                    
                    assertEquals("Tom", pk.getName());
                    assertEquals(i, pk.getCountryID().intValue());
                    assertEquals(j, pk.getCityID().intValue());
                    assertNotNull(pk.getDataChange_LastTime());
                }
            }
        }
    }

    @Test
    public void testDeleteBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    SqlBuilder builder = deleteFrom(p).where(p.PeopleID.eq(k+1));
                    where(builder, i, j);
                    assertEquals(1, dao.update(builder));
                    Person pk = new Person();
                    pk.setPeopleID(k + 1);
                    assertNull(dao.queryByPk(pk, hints(i, j)));
                }
            }
        }
    }

    @Test
    public void testQueryObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.eq(k+1)).into(Person.class);
                    where(builder, i, j);

                    Person pk = dao.queryObject(builder);
                    assertNotNull(pk);
                    assertEquals(k+1,  pk.getPeopleID().intValue());
                    assertEquals(i,  pk.getCountryID().intValue());
                    assertEquals(j,  pk.getCityID().intValue());
                }
            }
        }
    }

    @Test
    public void testQueryObjectPPartial() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    SqlBuilder builder = select(p.PeopleID, p.CountryID, p.CityID).from(p).where(p.PeopleID.eq(k+1)).into(Person.class);
                    where(builder, i, j);
                    Person pk = dao.queryObject(builder);
                    assertNotNull(pk);
                    assertNull(pk.getName());
                    assertNull(pk.getDataChange_LastTime());
                    assertNull(pk.getProvinceID());
                    assertEquals(k+1,  pk.getPeopleID().intValue());
                    assertEquals(i,  pk.getCountryID().intValue());
                    assertEquals(j,  pk.getCityID().intValue());
                }
            }
        }
    }

    @Test
    public void testQuerySimpleObject() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    SqlBuilder builder = select(p.Name).from(p).where().allOf(p.PeopleID.eq(k+1), p.Name.eq("test")).into(String.class);
                    where(builder, i, j);
                    String name = dao.queryObject(builder);
                    assertEquals("test", name);
                }
            }
        }
    }

    @Test
    public void testQuery() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Integer> pks = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++)
                    pks.add(k+1);

                SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks)).into(Person.class);
                where(builder, i, j);
                
                List<Person> plist = dao.query(builder.orderBy(p.PeopleID.asc()));

                assertEquals(4, plist.size());
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = plist.get(k);
                    assertNotNull(p);
                    assertEquals(k+1,  pk.getPeopleID().intValue());
                    assertEquals(i,  pk.getCountryID().intValue());
                    assertEquals(j,  pk.getCityID().intValue());
                }
            }
        }
    }

    public static class AliasPerson {
        @Column(name = "x")
        private Integer foo;

        @Column(name = "y")
        private String bar;

        public Integer getFoo() {
            return foo;
        }

        public AliasPerson setFoo(Integer foo) {
            this.foo = foo;
            return this;
        }

        public String getBar() {
            return bar;
        }

        public AliasPerson setBar(String bar) {
            this.bar = bar;
            return this;
        }
    }

    @Test
    public void testAliasQuery() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Integer> pks = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++)
                    pks.add(k+1);

                SqlBuilder builder = select(p.PeopleID.as("x"), p.Name.as("y")).from(p).where().allOf(p.PeopleID.in(pks))
                        .into(AliasPerson.class);
                where(builder, i, j);
                List<AliasPerson> plist = dao.query(builder.orderBy(p.PeopleID.asc()));
                assertEquals(4, plist.size());
                for (int k = 0; k < TABLE_MODE; k++) {
                    AliasPerson pk = plist.get(k);
                    assertNotNull(pk);
                    assertEquals(k+1,  pk.getFoo().intValue());
                    assertNotNull(pk.getBar());
                }

                SqlBuilder builder2 = new SqlBuilder().appendTemplate("select PeopleID as x, Name as y from person_" + j)
                        .into(AliasPerson.class);
                builder2.hints().inShard(i);

                List<AliasPerson> list2 = dao.query(builder2);
                assertFalse(list2.isEmpty());
                for (int k = 0; k < TABLE_MODE; k++) {
                    AliasPerson pk = list2.get(k);
                    assertNotNull(pk);
                    assertEquals(k+1,  pk.getFoo().intValue());
                    assertNotNull(pk.getBar());
                }
            }
        }
    }

    @Test
    public void testQueryComplex() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            List<Integer> pks = new ArrayList<>();
            for (int k = 0; k < TABLE_MODE -1; k++)
                pks.add(k+1);

            SqlBuilder builder = new SqlBuilder();
        
            p = p.inShard("0");
            
            query(selectAllFrom(p).where(p.PeopleID.eq(1)), i, 1);
            query(selectAllFrom(p).where(p.PeopleID.equal(1)), i, 1);
            query(selectAllFrom(p).where(p.PeopleID.neq(1)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.notEqual(1)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.greaterThan(1)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.gteq(1)), i, 4);
            query(selectAllFrom(p).where(p.PeopleID.greaterThanOrEqual(1)), i, 4);
            query(selectAllFrom(p).where(p.PeopleID.lessThan(3)), i, 2);
            query(selectAllFrom(p).where(p.PeopleID.lt(3)), i, 2);
            query(selectAllFrom(p).where(p.PeopleID.lessThanOrEqual(3)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.lteq(3)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.between(1, 3)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.notBetween(2, 3)), i, 2);
            query(selectAllFrom(p).where(p.PeopleID.notBetween(2, 4)), i, 1);
            query(selectAllFrom(p).where(p.PeopleID.in(pks)), i, 3);
            query(selectAllFrom(p).where(p.PeopleID.notIn(pks)), i, 1);
            query(selectAllFrom(p).where(p.Name.like("Te%")), i, 4);
            query(selectAllFrom(p).where(p.Name.notLike("%s")), i, 4);
            query(selectAllFrom(p).where(p.Name.isNull()), i, 0);
            query(selectAllFrom(p).where(p.Name.isNotNull()), i, 4);
        }
    }
    
    private void query(SqlBuilder builder, int shard, int count) {
        List<Map<String, ?>> plist;
        builder.intoMap().hints().diagnose();
        builder.hints().inShard(shard);

        try {
            plist = dao.query(builder);
//            System.out.println(builder.hints().getDiagnose());
            assertEquals(count,  plist.size());
            for(Map<String, ?> pl: plist) {
//                System.out.println(pl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(builder.hints().getDiagnose());
            fail();
        }

    }
    
    @Test
    public void testQueryByPage() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Integer> pks = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++)
                    pks.add(k+1);

                SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.Name.like("te%")).into(Person.class);
                where(builder, i, j);
                builder.orderBy(p.PeopleID.asc());
                setuper.range(builder, 0, 3);
                List<Person> plist = dao.query(builder);
                
                assertEquals(3, plist.size());
                for (int k = 0; k < 3; k++) {
                    Person pk = plist.get(k);
                    assertNotNull(p);
                    assertEquals(k+1,  pk.getPeopleID().intValue());
                    assertEquals(i,  pk.getCountryID().intValue());
                    assertEquals(j,  pk.getCityID().intValue());
                }
                
                builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.Name.like("te%")).into(Person.class);
                where(builder, i, j);
                builder.orderBy(p.PeopleID.asc());
                setuper.range(builder, 2, 4);
                plist = dao.query(builder);

                assertEquals(2, plist.size());
            }
        }
    }
    
    @Test
    public void testBatchQueryBuilder() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            BatchQueryBuilder batchBuilder = new BatchQueryBuilder();
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Integer> pks = new ArrayList<>();
                for (int k = 0; k < TABLE_MODE; k++)
                    pks.add(k+1);

                SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.eq(i), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                batchBuilder.addBatch(builder);
            }
            provider.inShard(batchBuilder.hints(), i);
            List<List<Person>> plist = (List<List<Person>>)dao.batchQuery(batchBuilder);

            assertEquals(TABLE_MODE, plist.size());
            for (int j = 0; j < TABLE_MODE; j++) {
                List<Person> list = plist.get(j);
                for (int k = 0; k < TABLE_MODE; k++) {
                    Person pk = list.get(k);
                    assertNotNull(p);
                    assertEquals(k+1,  pk.getPeopleID().intValue());
                    assertEquals(i,  pk.getCountryID().intValue());
                    assertEquals(j,  pk.getCityID().intValue());
                }
            }
        }
    }

    @Test
    public void testQueryTopObj() throws Exception {
        if(dbCategory == MySql)
            return;
        
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                PersonDefinition p = Person.PERSON;
                SqlBuilder builder = selectTop(3, p.PeopleID).from(p).where(p.PeopleID.gt(-1));
                where(builder, i, j).orderBy(p.PeopleID.asc()).intoObject();
                List plist = dao.query(builder);
        
                assertEquals(3, plist.size());
                for(int k = 0; k < plist.size(); k++){
                    assertEquals(k + 1, ((Number)plist.get(k)).intValue());
                }
            }
        }
    }

    @Test
    public void testQueryTopEntity() throws Exception {
        if(dbCategory == MySql)
            return;

        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                PersonDefinition p = Person.PERSON;
                SqlBuilder builder = selectTop(3, p.PeopleID, p.Name).from(p).where(p.PeopleID.gt(-1));
                where(builder, i, j).orderBy(p.PeopleID.asc()).into(Person.class);
                
                List<Person> plist = null;
                plist = dao.query(builder);
        
                assertEquals(3, plist.size());
                for(int k = 0; k < plist.size(); k++){
                    assertEquals("test", plist.get(k).getName());
                }
            }
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
            
            dao.execute(() -> {
                for (int j = 0; j < TABLE_MODE; j++) {
                    SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    List<Person> plist = dao.query(builder);

                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    
                    builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    assertEquals(0, dao.query(builder).size());
                    
                    assertEquals(4, dao.insert(plist));
                    builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    plist = dao.query(builder);
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                }
            }, hints);
            
            for (int j = 0; j < TABLE_MODE; j++)
                assertEquals(0, count(i, j));
        }
    }
    
    @Test
    public void testTransactionRollack() throws Exception {
        PersonDefinition p = Person.PERSON;
        for (int i = 0; i < DB_MODE; i++) {
            int ifinal = i;
            Hints hints = new Hints();
            provider.inShard(hints, i);
            
            if(hints.getShard() == null && hints.getShardValue() == null)
                continue;
            
            try {
                dao.execute(() -> {
                    for (int j = 0; j < TABLE_MODE; j++) {
                        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        List<Person> plist = dao.query(builder);

                        assertEquals(4, plist.size());
                        
                        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                        
                        builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        assertEquals(0, dao.query(builder).size());
                        
                        assertEquals(4, dao.insert(plist, new Hints().setIdBack()));
                        builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        assertEquals(4, dao.query(builder).size());
                        
                        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                        throw new RuntimeException("1");
                    }
                }, hints);
                fail();
            } catch (Exception e) {
            }
            
            for (int j = 0; j < TABLE_MODE; j++)
                assertEquals(4, count(i, j));
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

            assertEquals(10,  (int)dao.execute(() -> {
                for (int j = 0; j < TABLE_MODE; j++) {
                    SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    List<Person> plist = dao.query(builder);

                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                    
                    builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    assertEquals(0, dao.query(builder).size());
                    
                    assertEquals(4, dao.insert(plist));
                    builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                    plist = dao.query(builder);
                    assertEquals(4, plist.size());
                    
                    assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                }
                return 10;
            }, hints));            

            for (int j = 0; j < TABLE_MODE; j++)
                assertEquals(0, count(i, j));
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
                assertEquals(10,  (int)dao.execute(() -> {
                    for (int j = 0; j < TABLE_MODE; j++) {
                        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        List<Person> plist = dao.query(builder);

                        assertEquals(4, plist.size());
                        
                        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                        
                        builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        assertEquals(0, dao.query(builder).size());
                        
                        assertEquals(4, dao.insert(plist, new Hints().setIdBack()));
                        builder = selectAllFrom(p).where().allOf(p.CountryID.eq(ifinal), p.CityID.eq(j)).orderBy(p.PeopleID.asc()).into(Person.class);
                        assertEquals(4, dao.query(builder).size());
                        
                        assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist));
                        
                        throw new RuntimeException("1");
                    }
                    return 10;
                }, hints));
                fail();
            } catch (Exception e) {
            }            
            for (int j = 0; j < TABLE_MODE; j++)
                assertEquals(4, count(i, j));
        }        
    }
}
