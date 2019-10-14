package com.ppdai.das.client;

import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.allOf;
import static com.ppdai.das.client.SegmentConstants.anyOf;
import static com.ppdai.das.client.SegmentConstants.bracket;
import static com.ppdai.das.client.SqlBuilder.deleteFrom;
import static com.ppdai.das.client.SqlBuilder.insertInto;
import static com.ppdai.das.client.SqlBuilder.select;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.client.SqlBuilder.update;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import com.ppdai.das.core.exceptions.ErrorCode;

@RunWith(Parameterized.class)
public class SqlBuilderDbTableShardTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionDbTableShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionDbTableShard";

    private static PersonDefinition p = Person.PERSON;
    private static String testDate = "2019-11-11 11:11:11";
    
    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {SqlServer},
            {MySql},
            });
        }

    public SqlBuilderDbTableShardTest(DatabaseCategory dbCategory) throws SQLException {
        super(dbCategory);
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    private long count(int i) throws SQLException {
        PersonDefinition p = Person.PERSON;
        SqlBuilder sb = select("count(1)").from(p).intoObject();
        sb.hints().inShard(i);
        return ((Number)dao.queryObject(sb)).longValue();
    }
    
    @Before
    public void setup() throws Exception {
        tearDown();

        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String[] statements = new String[TABLE_MODE];
                for (int k = 0; k < TABLE_MODE; k++) {
                    String name = name(i,j,k);
                    statements[k] = String.format(
                            "INSERT INTO Person_%d(PeopleID, Name, CountryID, CityID, ProvinceID, DataChange_LastTime )" +
                                    " VALUES(%d, '%s', %d, %d, 1, '" + testDate + "')", j, k + 1, name, i, j);
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
    private String name(int i, int j, int k) {
        return String.format("test_%d_%d_%d", i, j, k);
    }

    /**
     * It is tested because internally , this method uses SqlBuilder
     * @throws Exception
     */
    @Test
    public void testQueryByPk() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    String name = name(i, j, k);
                    int id = k+1;
                    Person pk = new Person();
                    pk.setPeopleID(id);
                    failPk(pk);
                    
                    pk = new Person();
                    pk.setPeopleID(id);
                    pk.setCountryID(i);
                    failPk(pk);
                    
                    pk = new Person();
                    pk.setPeopleID(id);
                    pk.setCityID(j);
                    failPk(pk);
                    
                    pk = new Person();
                    pk.setPeopleID(id);
                    pk.setProvinceID(1);
                    failPk(pk);
                    
                    pk = new Person();
                    pk.setPeopleID(id);
                    pk.setCountryID(i);
                    pk.setCityID(j);
                    pk = dao.queryByPk(pk);
                    assertPerson(i, j, id, name, pk);
    
                    pk = new Person();
                    pk.setPeopleID(id);
                    pk.setProvinceID(0);
                    pk = dao.queryByPk(pk);
                    assertNull(pk);
                }
            }
        }
    }

    private void failPk(Person pk) {
        try {
            //More than 1 returned
            pk = dao.queryByPk(pk);
            fail();
        } catch (Exception e) {
            assertEquals(ErrorCode.AssertSingle.getMessage(), e.getMessage());
        }
    }

    @Test
    public void testQueryBySample() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    String name = name(i, j, k);
                    sample = new Person();
                    sample.setName(name);
                    assertSample(1, sample);
                    
                    sample = new Person();
                    sample.setCountryID(i);
                    sample.setCityID(j);
                    assertSample(4, sample);
    
                }
                sample = new Person();
                sample.setCityID(j);
                assertSample(4*2, sample);
            }
            sample = new Person();
            sample.setCountryID(i);
            assertSample(4*4, sample);
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertSample(0, sample);
        
        sample = new Person();
        sample.setProvinceID(1);
        assertSample(32, sample);
    }
    
    private void assertSample(int count, Person sample) throws SQLException {
        List<Person> results = dao.queryBySample(sample);
        assertSample(count, sample, results);
    }
    
    private void assertSample(int count, Person sample, List<Person> results) throws SQLException {
        assertEquals(count, results.size());
        for(Person p: results) {
            assertSample(sample, p);
        }
    }

    private void assertSample(Person sample, Person p) {
        if(sample.getName() != null)
            assertEquals(sample.getName(), p.getName());

        if(sample.getCountryID() != null)
            assertEquals(sample.getCountryID(), p.getCountryID());
        
        if(sample.getCityID() != null)
            assertEquals(sample.getCityID(), p.getCityID());

        if(sample.getDataChange_LastTime() != null)
            assertEquals(sample.getDataChange_LastTime(), p.getDataChange_LastTime());
        
        if(sample.getPeopleID() != null)
            assertEquals(sample.getPeopleID(), p.getPeopleID());

        if(sample.getProvinceID() != null)
            assertEquals(sample.getProvinceID(), p.getProvinceID());
    }

    @Test
    public void testQueryBySamplePage() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                sample = new Person();
                sample.setCountryID(i);
                sample.setCityID(j);

                List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
                assertSample(2, sample, results);
                results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
                assertSample(2, sample, results);
            }
        }
        
        for (int i = 0; i < DB_MODE; i++) {
            sample = new Person();
            sample.setCountryID(i);
            
            List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(2*4, sample, results);
            results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
            assertSample(2*4, sample, results);
        }
        
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(2*2, sample, results);
            
            results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
            assertSample(2*2, sample, results);
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
        assertSample(0, sample, results);
        
        sample = new Person();
        sample.setProvinceID(1);
        results = dao.queryBySample(sample, PageRange.atPage(1, 4, p.CityID.desc()));
        //Each shard return 4 recorders
        assertSample(4*2*4, sample, results);
        
        results = dao.queryBySample(sample, PageRange.atPage(2, 4, p.CityID.desc()));
        //Each shard return 0 recorders
        assertSample(0, sample, results);
    }
    
    @Test
    public void testCountBySample() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    String name = name(i, j, k);
                    sample = new Person();
                    sample.setName(name);
                    assertEquals(1, dao.countBySample(sample));
                    
                    sample = new Person();
                    sample.setCountryID(i);
                    sample.setCityID(j);
                    assertEquals(4, dao.countBySample(sample));
                }
                sample = new Person();
                sample.setCityID(j);
                assertEquals(4*2, dao.countBySample(sample));
            }
            sample = new Person();
            sample.setCountryID(i);
            assertEquals(4*4, dao.countBySample(sample));
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertEquals(0, dao.countBySample(sample));
        
        sample = new Person();
        sample.setProvinceID(1);
        assertEquals(32, dao.countBySample(sample));
    }
    
    @Test
    public void testQueryObject() throws Exception {
        SqlBuilder builder;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    String name = name(i, j, k);
                    int id = k+1;
                    builder = selectAllFrom(p).where(p.Name.eq(name)).into(Person.class);
                    Person pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
                    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.lt(10)).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.gt(-1)).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.in(Arrays.asList(0, 1, 2, 3, 4))).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(bracket(p.CountryID.eq(0), OR, p.CountryID.eq(1))).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);

                    //By CityId
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.lt(10)).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.gt(-1)).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.in(Arrays.asList(0, 1, 2, 3, 4))).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
    
                    builder = selectAllFrom(p).where(p.Name.eq(name)).and(anyOf(p.CityID.eq(0), p.CityID.eq(1), p.CityID.eq(2), p.CityID.eq(3))).into(Person.class);
                    pk = dao.queryObject(builder);
                    assertPerson(i, j, id, name, pk);
                }
            }
        }
    }

    private void assertPerson(int i, int j, int id, String name, Person pk) {
        assertNotNull(pk);
        assertEquals(i,  pk.getCountryID().intValue());
        assertEquals(j,  pk.getCityID().intValue());
        assertEquals(id,  pk.getPeopleID().intValue());
        assertEquals(name, pk.getName());
    }

    @Test
    public void testQueryObjectPPartial() throws Exception {
        SqlBuilder builder;
        String name = name(1, 1, 1);
        builder = select(p.PeopleID, p.CountryID, p.CityID).from(p).where(p.Name.eq(name)).into(Person.class);
        Person pk = dao.queryObject(builder);
        assertNotNull(pk);
        assertNull(pk.getName());
        assertNull(pk.getDataChange_LastTime());
        assertNull(pk.getProvinceID());
        assertEquals(1+1,  pk.getPeopleID().intValue());
        assertEquals(1,  pk.getCountryID().intValue());
    }

    @Test
    public void testQuerySimpleObject() throws Exception {
        SqlBuilder builder;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    int id = k+1;
                    String name = name(i, j, k);
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).into(String.class);
                    String result = dao.queryObject(builder);
                    assertEquals(name,  result);
                    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CountryID.lt(10)).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CountryID.gt(-1)).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CountryID.in(Arrays.asList(0, 1, 2, 3, 4))).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CountryID.in(0, 1, 2, 3, 4)).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CountryID.between(0, 4)).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
    
                    builder = select(p.Name).from(p).where(p.Name.eq(name)).and(bracket(p.CountryID.eq(0), OR, p.CountryID.eq(1))).into(String.class);
                    result = dao.queryObject(builder);
                    assertEquals(name,  result);
                }
            }
        }
    }
    
    @Test
    public void testDeleteBySample() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            sample = new Person();
            sample.setCountryID(i);
            
            int results = dao.deleteBySample(sample);
            assertEquals(4*4, results);
        }
    }

    @Test
    public void testDeleteBySample2() throws Exception {
        Person sample;
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            int results = dao.deleteBySample(sample);
            assertEquals(2*4, results);
        }
    }        

    @Test
    public void testDeleteBySample3() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                sample = new Person();
                sample.setCountryID(i);
                sample.setCityID(j);
                
                int results = dao.deleteBySample(sample);
                assertEquals(4, results);
            }
        }
    }
    
    @Test
    public void testDeleteBySample4() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                for (int k = 0; k < TABLE_MODE; k++) {
                    sample = new Person();
                    sample.setName(name(i, j, k));
                    
                    int results = dao.deleteBySample(sample);
                    assertEquals(1, results);
                }
            }
        }
    }
    
    @Test
    public void testDeleteBySample5() throws Exception {
        Person sample;
        sample = new Person();
        sample.setProvinceID(0);
        int results = dao.deleteBySample(sample);
        assertEquals(0, results);
        
        sample = new Person();
        sample.setProvinceID(1);
        results = dao.deleteBySample(sample);
        assertEquals(32, results);
    }

    @Test
    public void testQuery() throws Exception {
        List<Integer> pks = new ArrayList<>();
        for (int j = 0; j < TABLE_MODE; j++) 
            pks.add(j+1);

        SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.ProvinceID.eq(1)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        assertEquals(2*4*4, plist.size());
        
        //Please note below is using anyOf
        builder = selectAllFrom(p).where().anyOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(32, plist.size());
        
        //Please note below is using allOf
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.in(0,1,2,3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(32, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.in(Arrays.asList(1,2,3))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.gteq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(32, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CountryID.gteq(0), p.CityID.neq(-1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(32, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CountryID.gteq(0), p.CityID.eq(1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4*2, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.between(0, 1)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(32, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.between(2, 3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
    }
    
    @Test
    public void testQueryX() throws Exception {
        SqlBuilder builder = selectAllFrom(p).where(p.CityID.eq(p.CountryID)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        assertEquals(2*4, plist.size());

        builder = selectAllFrom(p).where(p.CityID.eq(p.CountryID)).and(p.CountryID.between(-1, 100)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(2*4, plist.size());
        
        builder = selectAllFrom(p).where(p.CityID.eq(p.CountryID)).and(p.CountryID.eq(1)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());    
    }

    @Test
    public void testInsertBuilder() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            SqlBuilder builder = insertInto(p, p.Name, p.CountryID).values(p.Name.of("Jerry"), p.CountryID.of(i));
            assertEquals(4, dao.update(builder));
            
            Person sample = new Person();
            sample.setName("Jerry");
            sample.setCountryID(i);
            assertSample(4, sample);
        }
    }
     
    @Test
    public void testInsertBuilder1() throws Exception {
        SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
        //another two inserted
        assertEquals(2*4, dao.update(builder));
        Person sample = new Person();
        sample.setName("Jerry");
        assertSample(2*4, sample);
    }

    @Test
    public void testInsertBuilder2() throws Exception {
        //The following won't work for mysql
        //SqlBuilder builder = insertInto(p, p.CityID, p.CountryID).append(select(p.CityID, p.CountryID).where(p.CountryID.eq(1)));
        SqlBuilder builder = insertInto(p, p.CountryID).append(select(p.CountryID).from(p).where(p.CityID.eq(1)));
        //another two inserted
        assertEquals(4*2, dao.update(builder));
        builder = SqlBuilder.selectCount().from(p).where(p.CityID.isNull()).and(p.CountryID.between(0, 1));
        assertEquals(4*2, ((Number)dao.queryObject(builder)).longValue());

        builder = SqlBuilder.selectCount().from(p).where(p.CityID.isNotNull()).and(p.CountryID.between(0, 1));
        assertEquals(32, ((Number)dao.queryObject(builder)).longValue());
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CityID.eq(5)).where(p.Name.like("test_1%"));
        //In shard [0, 1] and table shard [1 (5%4=1)]
        assertEquals(4, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCityID(5);
        assertSample(4, sample);
    }

    @Test
    public void testUpdateBuilder1() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CityID.eq(5)).where(p.CountryID.eq(0)).or(p.CountryID.eq(1));
        assertEquals(8, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCityID(5);
        assertSample(8, sample);
    }

    @Test
    public void testUpdateBuilder2() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CityID.eq(5)).where(allOf(p.CityID.isNull(), anyOf(p.CountryID.eq(0), p.CountryID.eq(1))));
        assertEquals(0, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCityID(5);
        assertSample(0, sample);
    }

    @Test
    public void testUpdateBuilder3() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CityID.eq(5)).where(allOf(p.CityID.isNotNull(), anyOf(p.CountryID.eq(0), p.CountryID.eq(1))));
        assertEquals(2*4, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCityID(5);
        assertSample(2*4, sample);
    }

    @Test
    public void testDeleteBuilder() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.in(1, 2, 3)).and(anyOf(p.CountryID.eq(1), p.CountryID.eq(0)));
        assertEquals(6*4, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder1() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(allOf(p.PeopleID.in(1, 2, 3, 4), p.CountryID.between(0, 1)));
        assertEquals(8*4, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder2() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.gt(1));
        assertEquals(6*4, dao.update(builder));
    }
}