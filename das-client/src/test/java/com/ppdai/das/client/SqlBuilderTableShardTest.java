package com.ppdai.das.client;

import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.allOf;
import static com.ppdai.das.client.SegmentConstants.anyOf;
import static com.ppdai.das.client.SegmentConstants.bracket;
import static com.ppdai.das.client.SqlBuilder.deleteFrom;
import static com.ppdai.das.client.SqlBuilder.insertInto;
import static com.ppdai.das.client.SqlBuilder.select;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.client.SqlBuilder.selectCount;
import static com.ppdai.das.client.SqlBuilder.update;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import com.ppdai.das.core.ErrorCode;
import com.ppdai.das.core.enums.DatabaseCategory;

@RunWith(Parameterized.class)
public class SqlBuilderTableShardTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionTableShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionTableShard";

    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {SqlServer},
            {MySql},
            });
        }

    public SqlBuilderTableShardTest(DatabaseCategory dbCategory) throws SQLException {
        super(dbCategory);
    }
    
    public static SqlBuilderTableShardTest of(DatabaseCategory dbCategory) throws SQLException {
        return new SqlBuilderTableShardTest(dbCategory);
    }
    
    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }

    private boolean allowInsertWithId() {
        return setuper.turnOnIdentityInsert("person") == null;
    }

    @Before
    public void setup() throws Exception {
        for (int i = 0; i < TABLE_MODE; i++) {
            String[] statements = new String[TABLE_MODE];
            for (int j = 0; j < TABLE_MODE; j++) {
                statements[j] = String.format("INSERT INTO person_%d(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, '%s', %d, %d, 1)", i, j + 1, name(i, j), j, i);
            }

            if(!allowInsertWithId())
                statements = DbSetupUtil.handle(String.format("Person_%d", i), statements);
            
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inTableShard(i);
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

    
    private String name(int i, int j) {
        return String.format("test_%d_%d", i, j);
    }

    private void assertPerson(int i, int j, String name, Person pk) {
        assertNotNull(pk);
        assertEquals(j+1,  pk.getPeopleID().intValue());
        assertEquals(i,  pk.getCityID().intValue());
        assertEquals(name, pk.getName());
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

    /**
     * It is tested because internally , this method uses SqlBuilder
     * @throws Exception
     */
    @Test
    public void testQueryByPk() throws Exception {
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                int id = j+1;
                Person pk = new Person();
                pk.setPeopleID(id);
                failPk(pk);
                
                pk = new Person();
                pk.setPeopleID(id);
                pk.setCountryID(j);
                failPk(pk);
                
                pk = new Person();
                pk.setPeopleID(id);
                pk.setProvinceID(1);
                failPk(pk);
                
                pk = new Person();
                pk.setPeopleID(id);
                pk.setCityID(i);
                pk = dao.queryByPk(pk);
                assertPerson(i, j, name, pk);

                pk = new Person();
                pk.setPeopleID(id);
                pk.setProvinceID(0);
                pk = dao.queryByPk(pk);
                assertNull(pk);
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
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                sample = new Person();
                sample.setName(name);
                assertSample(1, sample);

                sample = new Person();
                sample.setCountryID(i);
                sample.setCityID(j);
                assertSample(1, sample);
            }

            sample = new Person();
            sample.setCityID(i);
            assertSample(4, sample);
            
            sample = new Person();
            sample.setCountryID(i);
            assertSample(4, sample);
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertSample(0, sample);
        
        sample = new Person();
        sample.setProvinceID(1);
        assertSample(16, sample);
    }
    
    @Test
    public void testQueryBySamplePage() throws Exception {
        Person sample;
        for (int i = 0; i < TABLE_MODE; i++) {
            sample = new Person();
            sample.setCityID(i);
            
            List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(2, sample, results);
            results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
            assertSample(2, sample, results);

            sample = new Person();
            sample.setCountryID(i);
            //Every shard return 1 recorder
            results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(4, sample, results);
            
            results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
            assertSample(0, sample, results);
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
        assertSample(0, sample, results);
        
        sample = new Person();
        sample.setProvinceID(1);
        results = dao.queryBySample(sample, PageRange.atPage(1, 4, p.CityID.desc()));
        //Each shard return 4 recorders
        assertSample(16, sample, results);
        
        results = dao.queryBySample(sample, PageRange.atPage(2, 4, p.CityID.desc()));
        //Each shard return 0 recorders
        assertSample(0, sample, results);
    }
    
    @Test
    public void testCountBySample() throws Exception {
        Person sample;
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                sample = new Person();
                sample.setName(name);
                assertEquals(1, dao.countBySample(sample));
                
                sample = new Person();
                sample.setCountryID(i);
                sample.setCityID(j);
                assertEquals(1, dao.countBySample(sample));

            }
            sample = new Person();
            sample.setCountryID(i);
            assertEquals(4, dao.countBySample(sample));

            sample = new Person();
            sample.setCityID(i);
            assertEquals(4, dao.countBySample(sample));
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertEquals(0, dao.countBySample(sample));
        
        sample = new Person();
        sample.setProvinceID(1);
        assertEquals(16, dao.countBySample(sample));
    }
    
    @Test
    public void testQueryObject() throws Exception {
        SqlBuilder builder;
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                builder = selectAllFrom(p).where(p.Name.eq(name)).into(Person.class);
                Person pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);
                
                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.lt(10)).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.gt(-1)).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.in(Arrays.asList(0, 1, 2, 3, 4))).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CityID.in(0, 1, 2, 3, 4)).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(anyOf(p.CityID.eq(0), p.CityID.eq(1), p.CityID.eq(2), p.CityID.eq(3))).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);
            }
        }
    }

    @Test
    public void testQueryObjectPPartial() throws Exception {
        SqlBuilder builder;
        String name = name(1, 1);
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
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                builder = select(p.Name).from(p).where(p.Name.eq(name)).into(String.class);
                String result = dao.queryObject(builder);
                assertEquals(name,  result);
                
                builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CityID.lt(10)).into(String.class);
                result = dao.queryObject(builder);
                assertEquals(name,  result);

                builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CityID.gt(-1)).into(String.class);
                result = dao.queryObject(builder);
                assertEquals(name,  result);

                builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CityID.in(Arrays.asList(0, 1, 2, 3, 4))).into(String.class);
                result = dao.queryObject(builder);
                assertEquals(name,  result);

                builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CityID.in(0, 1, 2, 3, 4)).into(String.class);
                result = dao.queryObject(builder);
                assertEquals(name,  result);

                builder = select(p.Name).from(p).where(p.Name.eq(name)).and(p.CityID.between(0, TABLE_MODE)).into(String.class);
                result = dao.queryObject(builder);
                assertEquals(name,  result);
            }
        }
    }
    
    @Test
    public void testDeleteBySample() throws Exception {
        Person sample;
        for (int i = 0; i < TABLE_MODE; i++) {
            sample = new Person();
            sample.setCityID(i);
            
            int results = dao.deleteBySample(sample);
            assertEquals(4, results);
        }
    }

    @Test
    public void testDeleteBySample2() throws Exception {
        Person sample;
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCountryID(j);
            int results = dao.deleteBySample(sample);
            assertEquals(4, results);
        }
    }

    @Test
    public void testDeleteBySample3() throws Exception {
        Person sample;
        for (int i = 0; i < TABLE_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                sample = new Person();
                sample.setName(name(i, j));
                
                int results = dao.deleteBySample(sample);
                assertEquals(1, results);
            }
        }
    }
        
    @Test
    public void testDeleteBySample4() throws Exception {
        Person sample;
        sample = new Person();
        sample.setProvinceID(0);
        int results = dao.deleteBySample(sample);
        assertEquals(0, results);
        
        sample = new Person();
        sample.setProvinceID(1);
        results = dao.deleteBySample(sample);
        assertEquals(16, results);
    }

    @Test
    public void testQuery() throws Exception {
        List<Integer> pks = new ArrayList<>();
        for (int j = 0; j < TABLE_MODE; j++) 
            pks.add(j+1);

        SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.ProvinceID.eq(1)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().anyOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.in(0,1,2,3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.in(Arrays.asList(1,2,3))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(12, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(12, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.gteq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CityID.gteq(0), p.CountryID.neq(-1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(16, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CityID.gteq(0), p.CountryID.eq(1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.between(1,  3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(12, plist.size());

    
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.between(1,  3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(12, plist.size());
    }

    @Test
    public void testInsertBuilder() throws Exception {
        for(int i = 0; i < TABLE_MODE;i++) {
            SqlBuilder builder = insertInto(p, p.Name, p.CityID).values(p.Name.of("Jerry"), p.CityID.of(i));
            assertEquals(1, dao.update(builder));
            Person sample = new Person();
            sample.setName("Jerry");
            sample.setCityID(i);
            assertSample(1, sample);
        }
        //four inserted

        SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
        //another two inserted
        assertEquals(4, dao.update(builder));
        Person sample = new Person();
        sample.setName("Jerry");
        assertSample(8, sample);
    }

    @Test
    public void testInsertBuilder1() throws Exception {
        for(int i = 0; i < TABLE_MODE;i++) {
            SqlBuilder builder = insertInto(p, p.Name).append(select(p.Name).from(p).where(p.CityID.eq(i)));
            assertEquals(4, dao.update(builder));
            builder = SqlBuilder.selectCount().from(p).where(p.Name.like("test%"));
            builder.hints().inTableShard(i);
            assertEquals(8L, (Number)dao.queryObject(builder));
        }
    }
    
    @Test
    public void testInsertBuilder2() throws Exception {
        SqlBuilder builder = insertInto(p, p.Name).append(select(p.Name).from(p));
        assertEquals(16, dao.update(builder));
        builder = selectCount().from(p).where(p.ProvinceID.isNull());
        assertEquals(16L, (Number)dao.queryObject(builder));
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CountryID.eq(5)).where(p.Name.like("test_1%"));
        assertEquals(4, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCountryID(5);
        assertSample(4, sample);
    }

    @Test
    public void testUpdateBuilder1() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CountryID.eq(5)).where(p.CityID.eq(0)).or(p.CityID.eq(1));
        assertEquals(8, dao.update(builder));
        Person sample = new Person();
        sample.setName("1");
        sample.setCountryID(5);
        assertSample(8, sample);
    }

    @Test
    public void testDeleteBuilder() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.in(1, 2, 3)).and(anyOf(p.CityID.eq(1), p.CityID.eq(0)));
        assertEquals(6, dao.update(builder));
    }
    
    @Test
    public void testDeleteBuilder0() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.in(1, 2, 3)).and(anyOf(p.CountryID.eq(1), p.CountryID.eq(0)));
        assertEquals(8, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder1() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(allOf(p.PeopleID.in(1, 2, 3, 4), p.CityID.between(0, 1)));
        assertEquals(8, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder2() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(allOf(p.PeopleID.in(1, 2, 3, 4), p.CityID.in(0, 1, 2)));
        assertEquals(12, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder3() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.gt(1));
        assertEquals(12, dao.update(builder));
    }
}
