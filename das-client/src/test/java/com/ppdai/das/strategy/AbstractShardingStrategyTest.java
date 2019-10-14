package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.ppdai.das.strategy.AbstractShardingStrategy;
import com.ppdai.das.strategy.ShardingContext;
import com.ppdai.das.strategy.ShardingStrategy;
import com.ppdai.das.strategy.TableShardingContext;

public class AbstractShardingStrategyTest {
    class TestShardingStrategy extends AbstractShardingStrategy {

        @Override
        public Set<String> locateDbShards(ShardingContext ctx) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<String> locateTableShards(TableShardingContext ctx) {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    @Before
    public void setup() throws Exception {
    }
    
    @Test
    public void testInitialize() throws SQLException {
        try {
            ShardingStrategy test = new TestShardingStrategy();
            Map<String, String> settings = new HashMap<>();
            test.initialize(settings);
        } catch (Exception e) {
            fail();
        }
    }
            
    @Test
    public void testInitializeSeparator() throws SQLException {
        try {
            ShardingStrategy test = new TestShardingStrategy();
            Map<String, String> settings = new HashMap<>();
            settings.put(AbstractShardingStrategy.SEPARATOR, "X");
            test.initialize(settings);
            assertEquals("AX1", test.getTableName("A", "1"));
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void testInitializeShardedTabled() throws SQLException {
        try {
            ShardingStrategy test = new TestShardingStrategy();
            Map<String, String> settings = new HashMap<>();
            settings.put(AbstractShardingStrategy.SHARDED_TABLES, "A, B, C");
            test.initialize(settings);
            assertTrue(test.isShardingEnable("A"));
            assertTrue(test.isShardingEnable("a"));
            assertTrue(test.isShardingEnable("B"));
            assertTrue(test.isShardingEnable("b"));
            assertTrue(test.isShardingEnable("C"));
            assertTrue(test.isShardingEnable("c"));
            assertFalse(test.isShardingEnable("D"));
            assertFalse(test.isShardingEnable("d"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSeparatorAndTableName() {
        AbstractShardingStrategy test = new TestShardingStrategy();
        
        //Test default
        assertEquals("A_1", test.getTableName("A", "1"));
        
        test.setSeparator(null);
        assertEquals("A1", test.getTableName("A", "1"));
        
        test.setSeparator("");
        assertEquals("A1", test.getTableName("A", "1"));
        
        test.setSeparator("aaa");
        assertEquals("Aaaa1", test.getTableName("A", "1"));

        test.setSeparator("_");
        assertEquals("A_1", test.getTableName("A", "1"));
    }

    @Test
    public void testIsShardByDb() {
        AbstractShardingStrategy test = new TestShardingStrategy();
        assertFalse(test.isShardByDb());
        
        test.setShardByDb(true);
        assertTrue(test.isShardByDb());
    }

    @Test
    public void testIsShardByTable() {
        AbstractShardingStrategy test = new TestShardingStrategy();
        assertFalse(test.isShardByTable());
        
        test.setShardByTable(true);
        assertTrue(test.isShardByTable());
    }

    @Test
    public void testShardedTables() {
        AbstractShardingStrategy test = new TestShardingStrategy();
        
        Set<String> shardedTables = new HashSet<>();
        shardedTables.add("t1");
        shardedTables.add("t2");
        shardedTables.add("T1");
        shardedTables.add("T2");
        test.setShardedTables(shardedTables);
        assertTrue(test.isShardingEnable("t1"));
        assertTrue(test.isShardingEnable("t2"));
        assertTrue(test.isShardingEnable("T1"));
        assertTrue(test.isShardingEnable("T1"));
        assertFalse(test.isShardingEnable("t3"));
        assertFalse(test.isShardingEnable("T3"));
    }

    @Test
    public void testAllTableShards() {
        AbstractShardingStrategy test = new TestShardingStrategy();
        Set<String> testShards = null;
        try {
            assertNull(test.getAllTableShards());
            test.setAllTableShards(testShards);
            fail();
        } catch (Exception e) {
        }
            
        try {
            assertNull(test.getAllTableShards());
            testShards = new HashSet<>();
            test.setAllTableShards(testShards);
            fail();
        } catch (Exception e) {
        }
            
        testShards = new HashSet<>();
        testShards.add("1");
        testShards.add("2");
        testShards.add("3");
        
        test.setAllTableShards(testShards);
        assertEquals(testShards, test.getAllTableShards());
    }
}