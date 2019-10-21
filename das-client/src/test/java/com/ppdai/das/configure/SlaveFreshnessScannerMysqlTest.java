package com.ppdai.das.configure;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.core.DasConfigure;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasVersionInfo;
import com.ppdai.das.core.DatabaseSelector;
import com.ppdai.das.core.FreshnessSelector;

public class SlaveFreshnessScannerMysqlTest {
    private static final String GET_DB_NAME = "select DATABASE() as id";
    private static final String NO_FRESHNESS_DATABASE_NAME = "MySqlSimple";//"MysqlNoFreshness";
    
    private static final String DATABASE_NAME = "SimpleMysqlFreshness";//"SimpleMysqlFreshness";
    private static final Map<String, Integer> freshnessMap = new HashMap<>();
    
    private static final String SHARD_DATABASE_NAME = "SimpleMysqlShardFreshness";
    private static final String[] masterShard = new String[]{"dal_shard_0", "dal_shard_1"};
    private static final Map<Integer, Map<String, Integer>> freshnessShardMap = new HashMap<>();
    private static final DasVersionInfo versionInfo = new DasVersionInfo();
    private static final boolean NULLABLE = true;

    private static DatabaseSelector defaultDatabaseSelector;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DasClientFactory.initClientFactory();
        versionInfo.setDasClientVersion("test-version");
        freshnessMap.put("shard_0", 3);
        freshnessMap.put("shard_1", 5);
        freshnessMap.put("dal_shard_0", 3);
        freshnessMap.put("dal_shard_1", 5);
        
        Map<String, Integer> shardMap = new HashMap<>();
        shardMap.put("dal_shard_0", 3);
        shardMap.put("dal_shard_1", 5);
        
        freshnessShardMap.put(0, shardMap);
        
        shardMap = new HashMap<>();
        shardMap.put("dal_shard_0", 9);
        shardMap.put("dal_shard_1", 7);
        
        freshnessShardMap.put(1, shardMap);

        //Setup FreshnessSelector, backup defaultDatabaseSelector
        DasConfigure freshDalConfigure = DasConfigureFactory.getConfigure("das-test");
        defaultDatabaseSelector = DasConfigureFactory.getConfigure("das-test").getDatabaseSelector();

        Field selectorField = freshDalConfigure.getClass().getDeclaredField("selector");
        selectorField.setAccessible(true);
        FreshnessSelector freshnessSelector = new FreshnessSelector();
        freshnessSelector.initialize(ImmutableMap.of(
                "freshnessReader", "com.ppdai.das.configure.TestFreshnessReader",
                "updateInterval", "2"));
        selectorField.set(freshDalConfigure, freshnessSelector);

        String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                    new Hints().setVersionInfo(versionInfo), String.class, "das-test", DATABASE_NAME);

        // make sure warmup is done.
        Thread.sleep(1 * 1000);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        //restore defaultDatabaseSelector
        DasConfigure freshDalConfigure = DasConfigureFactory.getConfigure("das-test");
        Field selectorField = freshDalConfigure.getClass().getDeclaredField("selector");
        selectorField.setAccessible(true);
        selectorField.set(freshDalConfigure, defaultDatabaseSelector);
    }

    @Test
    public void testNoFreshness() throws SQLException {
        String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                new Hints().freshness(10).setVersionInfo(versionInfo), String.class,"das-test",NO_FRESHNESS_DATABASE_NAME);
        Assert.assertEquals("dal_shard_0", id);
        try {
               queryForObject(GET_DB_NAME, new ArrayList<>(),
                    new Hints().freshness(10).setVersionInfo(versionInfo).slaveOnly(), String.class, "das-test",NO_FRESHNESS_DATABASE_NAME);
            Assert.fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testBelow() throws SQLException {
        testBelow(3);
        testBelow(5);
        testBelow(7);
        testBelow(9);
    }
    
    @Test
    public void testMaster() throws SQLException {
        testMaster(2);
        testMaster(1);
        testMaster(0);
        testMaster(-1);
    }
    
    private void testBelow(int freshness) throws SQLException {
        for(int i = 0; i < 100; i++){
            String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                    new Hints().setVersionInfo(versionInfo).freshness(freshness), String.class, "das-test", DATABASE_NAME);
            Assert.assertTrue(freshnessMap.get(id) <= freshness);
        }
    }
    
    private void testMaster(int freshness) throws SQLException {
        for(int i = 0; i < 100; i++){
            String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                    new Hints().freshness(freshness).setVersionInfo(versionInfo), String.class, "das-test", DATABASE_NAME);
            Assert.assertEquals("dal_shard_0", id);
        }
    }

    public static <T> T queryForObject(String sql, List<Parameter> parameters, Hints hints, Class<T> clazz, String appId, String logicDbName)
            throws SQLException {
        return DasClientFactory.getClient(logicDbName).queryObject(new SqlBuilder().append(sql).into(String.class).setHints(hints));
    }

    @Test
    public void testShardBelow() throws SQLException {
        testShardBelow(0, 3);
        testShardBelow(0, 5);
        testShardBelow(1, 7);
        testShardBelow(1, 9);
    }
    
    @Test
    public void testShardMaster() throws SQLException {
        testShardMaster(2);
        testShardMaster(1);
        testShardMaster(0);
        testShardMaster(-1);
    }
    
    private void testShardBelow(int shardId, int freshness) throws SQLException {
        for(int i = 0; i < 100; i++){
            String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                    new Hints().freshness(freshness).setVersionInfo(versionInfo).inShard(shardId), String.class, "das-test", SHARD_DATABASE_NAME);
            Assert.assertTrue(freshnessShardMap.get(shardId).get(id) <= freshness);
        }
    }
    
    private void testShardMaster(int freshness) throws SQLException {
        int shardId = 0;
        while(shardId<2) {
            for(int i = 0; i < 100; i++){
                String id = queryForObject(GET_DB_NAME, new ArrayList<>(),
                        new Hints().freshness(freshness).setVersionInfo(versionInfo).inShard(shardId), String.class, "das-test", SHARD_DATABASE_NAME);
                Assert.assertEquals(masterShard[shardId], id);
            }
            shardId++;
        }
    }   
}