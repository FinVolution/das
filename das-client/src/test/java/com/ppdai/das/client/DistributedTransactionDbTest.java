package com.ppdai.das.client;

import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.DbSetupUtil.DbSetuper;
import com.ppdai.das.client.Person.PersonDefinition;

@RunWith(Parameterized.class)
public class DistributedTransactionDbTest {

    private static final int DB_MODE = 2;
    private static final int TABLE_MODE = 4;
    private final static String TABLE_NAME = "person";

    private DataPreparer preparer;

    private DasClient dao;
    private DbSetuper setuper;
    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() throws SQLException {
        return Arrays.asList(new Object[][]{
            {DasClientDBTest.of(MySql)},
            {DasClientDBTest.of(SqlServer)},
            
            {DasClientDbTableTest.of(MySql)},
            {DasClientDbTableTest.of(SqlServer)},
        });
    }

    public DistributedTransactionDbTest(DataPreparer preparer) throws SQLException {
        this.preparer = preparer;
        dao = DasClientFactory.getClient(preparer.getDbName());
    }
    
    public Hints hints() {
        return new Hints();
    }
    
    @Before
    public void setup() throws Exception {
        preparer.setup();
    }
    
    @After
    public void tearDown() throws Exception {
        preparer.tearDown();
    }
    
    @Test
    public void testBatchWrongLogicDb() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        try {
            DasClientFactory.getClient(preparer.getDbName(MySql)).execute(() -> {
                DasClient c2 = DasClientFactory.getClient(preparer.getDbName(SqlServer));
                c2.batchDelete(plist);
            }, new Hints().inShard(0));
            fail();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatchNoId() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        for(Person pp: plist) {
            pp.setCountryID(null);
        }
        
        try {
            dao.execute(() -> {
                dao.batchDelete(plist);
            }, new Hints().inShard(0));
            fail();
        } catch (SQLException e) {
        }
    }

    @Test
    public void testBatchWrongId() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        try {
            dao.execute(() -> {
                dao.batchDelete(plist);
            }, new Hints().inShard(1));
            fail();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBatchSameIdShard() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        try {
            dao.execute(() -> {
                //With shard id
                dao.batchDelete(plist, new Hints().inShard(1));
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();
        }        
    }
    
    @Test
    public void testBatchSameIdShardValue() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        try {
            dao.execute(() -> {
                //With shard value
                dao.batchDelete(plist, hints().shardValue(1));
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();
        }        
    }
    
    @Test
    public void testBuilderNoId() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                dao.query(builder);
            }, new Hints().inShard(1));
            fail();
        } catch (SQLException e) {
        }
    }
    
    @Test
    public void testBuilderNoIdShardId() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                //With shard id
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().inShard(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
    
    @Test
    public void testBuilderNoIdShardValue() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                //With shard value
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().shardValue(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
    
    @Test
    public void testBuilderWrongId() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                dao.query(builder);
            }, new Hints().inShard(1));
            fail();
        } catch (SQLException e) {
        }
    }
    
    @Test
    public void testBuilderWrongIdShardId() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        try {
            dao.execute(() -> {
                //With shard id
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().inShard(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
    
    @Test
    public void testBuilderWrongIdShardValue() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        try {
            dao.execute(() -> {
                //With shard value
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().shardValue(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
    
    @Test
    public void testBuilderSameId() throws Exception {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(1), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }

    @Test
    public void testBuilderSameIdShardId() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        try {
            dao.execute(() -> {
                //With shard id
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(1), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().inShard(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
    
    @Test
    public void testBuilderSameIdShardValue() throws Exception {
        PersonDefinition p = Person.PERSON;
        
        try {
            dao.execute(() -> {
                //With shard value
                SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(1), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                builder.hints().shardValue(1);
                dao.query(builder);
            }, new Hints().inShard(1));
        } catch (SQLException e) {
            fail();        
        }
    }
}
