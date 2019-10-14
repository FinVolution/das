package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ppdai.das.client.Hints;

public class HintsStrategyTest {
    private static Set<String> set(String...values) {
        Set<String> shardsSet = new HashSet<>();
        for(String s: values)
            shardsSet.add(s);
        return shardsSet;
    }

    @Test
    public void testLocateDbShards() {
        HintsStrategy test = new HintsStrategy();
        Map<String, String> settings = new HashMap<>();
        settings.put(HintsStrategy.SHARD_BY_DB, "true");
        test.initialize(settings);
        assertTrue(test.isShardByDb());
        assertTrue(test.locateDbShards(new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints(), ConditionList.andList())).size() == 0);
        assertEquals(set("1"), test.locateDbShards(new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints().inShard(1), ConditionList.andList())));
        assertEquals(set("1"), test.locateDbShards(new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints().inShard("1"), ConditionList.andList())));
    }
    
    private static ShardingContext ctx() {
        return new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints(), ConditionList.andList());
    }


    @Test
    public void testLocateTableShards() {
        HintsStrategy test = new HintsStrategy();
        Map<String, String> settings = new HashMap<>();
        settings.put(HintsStrategy.SHARD_BY_TABLE, "true");
        

        Set<String> shardedTables = new HashSet<>();
        shardedTables.add("t1");
        shardedTables.add("t2");
        shardedTables.add("T1");
        shardedTables.add("T2");

        test.initialize(settings);
        assertTrue(test.isShardByTable());
        assertEquals(0, test.locateTableShards(new TableShardingContext("appId", "logicDbName", "t1", set("1", "2", "3"), new Hints(), ConditionList.andList())).size());
        assertEquals(set("1"), test.locateTableShards(new TableShardingContext("appId", "logicDbName", "t1", set("1", "2", "3"), new Hints().inTableShard(1), ConditionList.andList())));
        assertEquals(set("1"), test.locateTableShards(new TableShardingContext("appId", "logicDbName", "t1", set("1", "2", "3"), new Hints().inTableShard("1"), ConditionList.andList())));
    }
}
