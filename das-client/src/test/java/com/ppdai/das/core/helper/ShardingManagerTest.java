package com.ppdai.das.core.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.Person;
import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.delegate.local.PPDaiDalParser;
import com.ppdai.das.core.ShardingManager;
import com.ppdai.das.strategy.ConditionList;

import oracle.sql.DATE;

@RunWith(Parameterized.class)
public class ShardingManagerTest {
    private String appId;
    private String logicTableName = "Person";
    private static final String COUNTRY_ID = "CountryID";
    private static final String CITY_ID = "CityID";
    private PersonDefinition p = Person.PERSON;
    
    private String noShardDb;
    private String dbShardLogicDb;
    private String tableShardLogicDb;
    private String dbTableShardLogicDb;
    
    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
            {"MySqlSimple", "MySqlConditionDbShard", "MySqlConditionTableShard", "MySqlConditionDbTableShard"},
            {"SqlSvrSimple", "SqlSvrConditionDbShard", "SqlSvrConditionTableShard", "SqlSvrConditionDbTableShard"},
        });
    }
    
    public ShardingManagerTest(String noShardDb, String dbShardLogicDb, String tableShardLogicDb, String dbTableShardLogicDb) {
        this.noShardDb = noShardDb;
        this.dbShardLogicDb = dbShardLogicDb;
        this.tableShardLogicDb = tableShardLogicDb;
        this.dbTableShardLogicDb = dbTableShardLogicDb;
    }
    
    @Before
    public void before() {
        DasClientFactory.initClientFactory();
        appId = DasClientFactory.getAppId();
    }
    
    @Test
    public void testIsShardingEnabled() {
        Arrays.asList(dbShardLogicDb, dbTableShardLogicDb).forEach(
                logicDbName->assertTrue(ShardingManager.isShardingEnabled(appId, logicDbName)));

        Arrays.asList(noShardDb, tableShardLogicDb).forEach(
                logicDbName->assertFalse(ShardingManager.isShardingEnabled(appId, logicDbName)));
    }

    @Test
    public void testIsTableShardingEnabledStringString() {
        Arrays.asList(tableShardLogicDb, dbTableShardLogicDb).forEach(
                logicDbName->assertTrue(ShardingManager.isTableShardingEnabled(appId, logicDbName)));
    
        Arrays.asList(noShardDb, dbShardLogicDb).forEach(
                logicDbName->assertFalse(ShardingManager.isTableShardingEnabled(appId, logicDbName)));
    }

    @Test
    public void testIsTableShardingEnabledByTable() {
        Arrays.asList(tableShardLogicDb, dbTableShardLogicDb).forEach(
                logicDbName->assertTrue(ShardingManager.isTableShardingEnabled(appId, logicDbName, logicTableName)));
        
        Arrays.asList(tableShardLogicDb, dbTableShardLogicDb).forEach(
                logicDbName->assertFalse(ShardingManager.isTableShardingEnabled(appId, logicDbName, logicTableName+"xx")));
    }

    @Test
    public void testBuildTableName() {
        Arrays.asList(tableShardLogicDb, dbTableShardLogicDb).forEach(logicDbName->{
            try {
                assertEquals(logicTableName + "_1", ShardingManager.buildTableName(appId, logicDbName, logicTableName, "1"));
            } catch (SQLException e) {
                fail();
            }
        });
    }

    @Test
    public void testGetDatabaseSet() {
        Arrays.asList(noShardDb, dbShardLogicDb, dbTableShardLogicDb, tableShardLogicDb).forEach(
                logicDbName->assertNotNull(ShardingManager.getDatabaseSet(appId, logicDbName)));
    }

    @Test
    public void testLocateShardIdByHints() throws SQLException {
        assertEquals("0", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().inShard(0)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().inShard(1)));
        assertEquals("0", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().inShard("0")));
        assertEquals("1", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().inShard("1")));

        assertEquals("0", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().shardValue(0)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().shardValue(1)));
        assertEquals("0", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().shardValue(2)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbTableShardLogicDb, new Hints().shardValue(3)));

        assertEquals("0", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().inShard(0)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().inShard(1)));
        assertEquals("0", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().inShard("0")));
        assertEquals("1", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().inShard("1")));

        assertEquals("0", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().shardValue(0)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().shardValue(1)));
        assertEquals("0", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().shardValue(2)));
        assertEquals("1", ShardingManager.locateShardId(appId, dbShardLogicDb, new Hints().shardValue(3)));
    }

    @Test
    public void testLocateShardIdByFields() throws SQLException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(COUNTRY_ID, "1");
        assertEquals("1", ShardingManager.locateShardId(appId, dbShardLogicDb, logicTableName, fields));
        assertEquals("1", ShardingManager.locateShardId(appId, dbTableShardLogicDb, logicTableName, fields));

        fields = new HashMap<String, Object>();
        fields.put(CITY_ID, "1");
        try {
            assertNull(ShardingManager.locateShardId(appId, dbShardLogicDb, logicTableName, fields));
            fail();
        }catch(Throwable e) {
        }
        try {
            assertNull(ShardingManager.locateShardId(appId, dbTableShardLogicDb, logicTableName, fields));
            fail();
        }catch(Throwable e) {
        }
    }
    
    boolean isAdvanced(String dbName) {
        return dbName.contains("Condition");
    }

    @Test
    public void testLocateTableShardIdByHints() throws SQLException {
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().inTableShard(0)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().inTableShard(1)));
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().inTableShard("0")));
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().inTableShard("1")));

        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(0)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(1)));
        assertEquals("2", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(2)));
        assertEquals("3", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(3)));
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(4)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(5)));
        assertEquals("2", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(6)));
        assertEquals("3", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(7)));
        
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().inTableShard(0)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().inTableShard(1)));
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().inTableShard("0")));
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().inTableShard("1")));

        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(0)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(1)));
        assertEquals("2", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(2)));
        assertEquals("3", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(3)));
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(4)));
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(5)));
        assertEquals("2", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(6)));
        assertEquals("3", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(7)));
    }

    @Test
    public void testLocateTableShardIdByFields() throws SQLException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(CITY_ID, "1");
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, logicTableName, fields));
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, logicTableName, fields));

        fields = new HashMap<String, Object>();
        fields.put(COUNTRY_ID, "1");
        try {
            assertNull(ShardingManager.locateTableShardId(appId, tableShardLogicDb, logicTableName, fields));
            fail();
        }catch(Throwable e) {
        }
        try {
            assertNull(ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, logicTableName, fields));
            fail();
        }catch(Throwable e) {
        }
    }

    @Test
    public void testLocateTableShardIdByHintsFields() throws SQLException {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(CITY_ID, "1");
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, logicTableName, new Hints().inTableShard(0), fields));
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, logicTableName, new Hints().tableShardValue(0), fields));

        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, logicTableName, new Hints(), fields));

        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, logicTableName, new Hints().inTableShard(0), fields));
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, logicTableName, new Hints().tableShardValue(0), fields));

        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, logicTableName, new Hints(), fields));
    }

    @Test
    public void testLocateTableShardIdCondition() throws SQLException {
        ConditionList cond = new SqlBuilder().allOf(p.CityID.eq(1), p.CountryID.eq(10)).buildQueryConditions();
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().inTableShard(0), cond));
        assertEquals("0", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints().tableShardValue(0), cond));
        
        assertEquals("1", ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints(), cond));
        
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().inTableShard(0), cond));
        assertEquals("0", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints().tableShardValue(0), cond));
        
        assertEquals("1", ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints(), cond));
        
        cond = new SqlBuilder().allOf(p.CityID.gt(1), p.CountryID.eq(10)).buildQueryConditions();
        try {
            ShardingManager.locateTableShardId(appId, dbTableShardLogicDb, new Hints(), cond);
            fail();
        }catch(Throwable e) {
        }
        try {
            ShardingManager.locateTableShardId(appId, tableShardLogicDb, new Hints(), cond);
            fail();
        }catch(Throwable e) {
        }
    }

    Map<String, ?> toField(int countryId, int cityId) throws SQLException {
        PPDaiDalParser parser = new PPDaiDalParser(Person.class);

        Person sample = new Person();
        sample.setCountryID(countryId);
        sample.setCityID(cityId);
        sample.setName("test"+System.currentTimeMillis());
        sample.setPeopleID((new Random().nextInt()));
        return parser.getFields(sample);
    }

    @Test
    public void testShuffleEntities() throws SQLException {
        List<Map<String, ?>> pojos = new ArrayList<>();
        
        pojos.add(toField(0,  1));
        pojos.add(toField(0,  0));
        pojos.add(toField(1,  1));
        pojos.add(toField(1,  0));
        
        Map<String, Map<Integer, Map<String, ?>>> pojosInShard = ShardingManager.shuffleEntities(appId, dbShardLogicDb, logicTableName, pojos);
        assertEquals(2, pojosInShard.size());
        assertEquals(2, pojosInShard.get("0").size());
        assertEquals(2, pojosInShard.get("1").size());
        
        pojos = new ArrayList<>();
        
        pojos.add(toField(0,  1));
        pojos.add(toField(0,  0));
        pojos.add(toField(1,  1));
        pojos.add(toField(1,  0));
        
        pojosInShard = ShardingManager.shuffleEntities(appId, dbTableShardLogicDb, logicTableName, pojos);
        assertEquals(2, pojosInShard.size());
        assertEquals(2, pojosInShard.get("0").size());
        assertEquals(2, pojosInShard.get("1").size());
    }

    Set<String> set(int...ids) {
        Set<String> shards = new HashSet<>();
        for(int id: ids)
            shards.add(String.valueOf(id));
        return shards;
    }

    @Test
    public void testLocateShardsOld() throws SQLException {
        if(isAdvanced(dbShardLogicDb))
            return;

        SqlBuilder builder = new SqlBuilder().allOf(p.CityID.eq(1), p.CountryID.eq(11), p.CountryID.eq(12));
        ConditionList cond = builder.buildQueryConditions();
        assertEquals(set(0), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints().inShard(0), cond));
        assertEquals(set(0), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints().shardValue(0), cond));
        assertEquals(set(0), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints().inShard(0), cond));
        assertEquals(set(0), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints().shardValue(0), cond));
        
        assertEquals(set(), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints(), cond));
        assertEquals(set(), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints(), cond));
    }
    
    @Test
    public void testLocateShards() throws SQLException {
        if(!isAdvanced(dbShardLogicDb))
            return;
        
        SqlBuilder builder = new SqlBuilder().allOf(p.CityID.eq(1), p.CountryID.eq(11), p.CountryID.eq(12));
        ConditionList cond = builder.buildQueryConditions();
        assertEquals(set(), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints(), cond));
        assertEquals(set(), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints(), cond));

        builder = new SqlBuilder().anyOf(p.CityID.eq(1), p.CountryID.eq(11), p.CountryID.eq(12));
        cond = builder.buildQueryConditions();
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints(), cond));
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints(), cond));
        
        builder = new SqlBuilder().anyOf(p.CityID.eq(1), p.CountryID.between(0, 10));
        cond = builder.buildQueryConditions();
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints(), cond));
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints(), cond));

        builder = new SqlBuilder().anyOf(p.CityID.eq(1), p.CountryID.in(0, 1, 21));
        cond = builder.buildQueryConditions();
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbShardLogicDb, new Hints(), cond));
        assertEquals(set(0, 1), ShardingManager.locateShards(appId, dbTableShardLogicDb, new Hints(), cond));
    }

    @Test
    public void testShuffleByTable() throws SQLException {
        Map<Integer, Map<String, ?>> pojos = new HashMap<>();
        
        pojos.put(1, toField(0,  1));
        pojos.put(2, toField(0,  0));
        pojos.put(3, toField(1,  1));
        pojos.put(4, toField(1,  0));
        
        Map<String, Map<Integer, Map<String, ?>>> pojosInShard = ShardingManager.shuffleByTable(appId, dbTableShardLogicDb, logicTableName, pojos);
        assertEquals(2, pojosInShard.size());
        assertEquals(2, pojosInShard.get("0").size());
        assertEquals(2, pojosInShard.get("1").size());
        
        pojos = new HashMap<>();
        
        pojos.put(1, toField(0,  1));
        pojos.put(2, toField(0,  0));
        pojos.put(3, toField(1,  1));
        pojos.put(4, toField(1,  0));
        
        pojosInShard = ShardingManager.shuffleByTable(appId, tableShardLogicDb, logicTableName, pojos);
        assertEquals(2, pojosInShard.size());
        assertEquals(2, pojosInShard.get("0").size());
        assertEquals(2, pojosInShard.get("1").size());
    }

    @Test
    public void testLocateTableShards() throws SQLException {
        ConditionList cond = new SqlBuilder().allOf(p.CityID.eq(1), p.CountryID.eq(10)).buildQueryConditions();
        assertEquals(set(0), ShardingManager.locateTableShards(appId, tableShardLogicDb, new Hints().inTableShard(0), cond));
        assertEquals(set(0), ShardingManager.locateTableShards(appId, tableShardLogicDb, new Hints().tableShardValue(0), cond));
        assertEquals(set(0), ShardingManager.locateTableShards(appId, dbTableShardLogicDb, new Hints().inTableShard(0), cond));
        assertEquals(set(0), ShardingManager.locateTableShards(appId, dbTableShardLogicDb, new Hints().tableShardValue(0), cond));
        
        assertEquals(set(1), ShardingManager.locateTableShards(appId, tableShardLogicDb, new Hints(), cond));
        assertEquals(set(1), ShardingManager.locateTableShards(appId, dbTableShardLogicDb, new Hints(), cond));
        
        cond = new SqlBuilder().allOf(p.CityID.gt(1), p.CountryID.eq(10)).buildQueryConditions();
        try {
            ShardingManager.locateTableShards(appId, tableShardLogicDb, new Hints(), cond);
            fail();
        }catch(Throwable e) {
        }
        try {
            ShardingManager.locateTableShards(appId, dbTableShardLogicDb, new Hints(), cond);
            fail();
        }catch(Throwable e) {
        }
    }

    @Test
    public void testDetectDistributedTransactionSetOfString() throws SQLException {
        try {
            ShardingManager.detectDistributedTransaction(set(0, 1));
        } catch (Exception e) {
            fail();
        }
        
        try {
            DasClientFactory.getClient(dbShardLogicDb).execute(()->{
                ShardingManager.detectDistributedTransaction(set(0, 1));
            }, new Hints().inShard(0));
            fail();
        } catch (Exception e) {
        }
        
        try {
            DasClientFactory.getClient(dbShardLogicDb).execute(()->{
                ShardingManager.detectDistributedTransaction(set(0));
            }, new Hints().inShard(0));
        } catch (Exception e) {
            fail();
        }        
    }

    @Test
    public void testDetectDistributedTransactionString() {
        try {
            ShardingManager.detectDistributedTransaction("1");
        } catch (Exception e) {
            fail();
        }
        
        try {
            DasClientFactory.getClient(dbShardLogicDb).execute(()->{
                ShardingManager.detectDistributedTransaction("1");
            }, new Hints().inShard(0));
            fail();
        } catch (Exception e) {
        }

        try {
            DasClientFactory.getClient(dbShardLogicDb).execute(()->{
                ShardingManager.detectDistributedTransaction("0");
            }, new Hints().inShard(0));
        } catch (Exception e) {
            fail();
        }
    }

}
