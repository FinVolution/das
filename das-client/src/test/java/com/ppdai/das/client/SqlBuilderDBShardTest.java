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
public class SqlBuilderDBShardTest extends DataPreparer {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlConditionDbShard";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrConditionDbShard";

    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {SqlServer},
            {MySql},
            });
        }

    public SqlBuilderDBShardTest(DatabaseCategory dbCategory) throws SQLException {
        super(dbCategory);
    }

    @Override
    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    public Hints hints(int i) {
        return new Hints().inShard(i);
    }
    
    public Hints hints() {
        return new Hints();
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
            String[] statements = new String[TABLE_MODE];
            for (int j = 0; j < TABLE_MODE; j++) {
                //Give it a special unique name
                String name = name(i, j); 
                statements[j] = String.format("INSERT INTO person(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, '%s', %d, %d, 1)", j + 1, name, i, j);
            }

            if(!allowInsertWithId())
                statements = DbSetupUtil.handle("Person", statements);

            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            dao.batchUpdate(builder);
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
            dao.batchUpdate(builder);
        }
        
        assertEquals(0, count(0));
        assertEquals(0, count(1));
    }
    
    private String name(int i, int j) {
        return String.format("test_%d_%d", i, j);
    }

    /**
     * It is tested because internally , this method uses SqlBuilder
     * @throws Exception
     */
    @Test
    public void testQueryByPk() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                int id = j+1;
                Person pk = new Person();
                pk.setPeopleID(id);
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
        for (int i = 0; i < DB_MODE; i++) {
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
            sample.setCountryID(i);
            assertSample(4, sample);
        }
        
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            assertSample(2, sample);
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertSample(0, sample);
        
        sample = new Person();
        sample.setProvinceID(1);
        assertSample(8, sample);
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
            sample = new Person();
            sample.setCountryID(i);
            
            List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(2, sample, results);
            results = dao.queryBySample(sample, PageRange.atPage(2, 2, p.CityID.desc()));
            assertSample(2, sample, results);
        }
        
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            List<Person> results = dao.queryBySample(sample, PageRange.atPage(1, 2, p.CityID.desc()));
            assertSample(2, sample, results);
            
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
        assertSample(8, sample, results);
        
        results = dao.queryBySample(sample, PageRange.atPage(2, 4, p.CityID.desc()));
        //Each shard return 0 recorders
        assertSample(0, sample, results);
    }
    
    @Test
    public void testCountBySample() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
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
        }
        
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            assertEquals(2, dao.countBySample(sample));
        }
        
        sample = new Person();
        sample.setProvinceID(0);
        assertEquals(0, dao.countBySample(sample));
        
        sample = new Person();
        sample.setProvinceID(1);
        assertEquals(8, dao.countBySample(sample));
    }
    
    @Test
    public void testQueryObject() throws Exception {
        SqlBuilder builder;
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
                builder = selectAllFrom(p).where(p.Name.eq(name)).into(Person.class);
                Person pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);
                
                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.lt(10)).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.gt(-1)).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(p.CountryID.in(Arrays.asList(0, 1, 2, 3, 4))).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);

                builder = selectAllFrom(p).where(p.Name.eq(name)).and(bracket(p.CountryID.eq(0), OR, p.CountryID.eq(1))).into(Person.class);
                pk = dao.queryObject(builder);
                assertPerson(i, j, name, pk);
            }
        }
    }

    private void assertPerson(int i, int j, String name, Person pk) {
        assertNotNull(pk);
        assertEquals(j+1,  pk.getPeopleID().intValue());
        assertEquals(i,  pk.getCountryID().intValue());
        assertEquals(name, pk.getName());
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
        for (int i = 0; i < DB_MODE; i++) {
            for (int j = 0; j < TABLE_MODE; j++) {
                String name = name(i, j);
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
    
    @Test
    public void testDeleteBySample() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
            sample = new Person();
            sample.setCountryID(i);
            
            int results = dao.deleteBySample(sample);
            assertEquals(4, results);
        }
    }

    @Test
    public void testDeleteBySample2() throws Exception {
        Person sample;
        for (int j = 0; j < TABLE_MODE; j++) {
            sample = new Person();
            sample.setCityID(j);
            int results = dao.deleteBySample(sample);
            assertEquals(2, results);
        }
    }        

    @Test
    public void testDeleteBySample3() throws Exception {
        Person sample;
        for (int i = 0; i < DB_MODE; i++) {
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
        assertEquals(8, results);
    }

    @Test
    public void testQuery() throws Exception {
        List<Integer> pks = new ArrayList<>();
        for (int j = 0; j < TABLE_MODE; j++) 
            pks.add(j+1);

        SqlBuilder builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.ProvinceID.eq(1)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        assertEquals(8, plist.size());
        
        builder = selectAllFrom(p).where().anyOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(8, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.Name.like("test_1%")).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.in(Arrays.asList(0,1,2,3))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(8, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.in(Arrays.asList(1,2,3))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());
        
        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.gteq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(8, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CountryID.gteq(0), p.CityID.neq(-1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(8, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), allOf(p.CountryID.gteq(0), p.CityID.eq(1))).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(2, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CountryID.between(0, 1)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(8, plist.size());

        builder = selectAllFrom(p).where().allOf(p.PeopleID.in(pks), p.CityID.between(2, 3)).orderBy(p.PeopleID.asc()).into(Person.class);
        plist = dao.query(builder);
        assertEquals(4, plist.size());
    }

    @Test
    public void testInsertBuilder() throws Exception {
        for(int i = 0; i < DB_MODE;i++) {
            SqlBuilder builder = insertInto(p, p.Name, p.CountryID).values(p.Name.of("Jerry"), p.CountryID.of(i));
            assertEquals(1, dao.update(builder));
            Person sample = new Person();
            sample.setName("Jerry");
            sample.setCountryID(i);
            assertSample(1, sample);
        }
        //two inserted

        SqlBuilder builder = insertInto(p, p.Name).values(p.Name.of("Jerry"));
        //another two inserted
        assertEquals(2, dao.update(builder));
        Person sample = new Person();
        sample.setName("Jerry");
        assertSample(4, sample);
    }

    @Test
    public void testUpdateBuilder() throws Exception {
        SqlBuilder builder = update(p).set(p.Name.eq("1"), p.CityID.eq(5)).where(p.Name.like("test_1%"));
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
    public void testDeleteBuilder() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.in(1, 2, 3)).and(anyOf(p.CountryID.eq(1), p.CountryID.eq(0)));
        assertEquals(6, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder1() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(allOf(p.PeopleID.in(1, 2, 3, 4), p.CountryID.between(0, 1)));
        assertEquals(8, dao.update(builder));
    }

    @Test
    public void testDeleteBuilder2() throws Exception {
        SqlBuilder builder = deleteFrom(p).where(p.PeopleID.gt(1));
        assertEquals(6, dao.update(builder));
    }
}
