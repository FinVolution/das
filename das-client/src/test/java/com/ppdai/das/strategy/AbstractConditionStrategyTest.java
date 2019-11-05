package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.Hints;
import org.junit.Before;
import org.junit.Test;

import com.ppdai.das.strategy.AbstractConditionStrategy;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionContext;
import com.ppdai.das.strategy.ConditionList;
import com.ppdai.das.strategy.OperatorEnum;
import com.ppdai.das.strategy.ShardingContext;
import com.ppdai.das.strategy.TableConditionContext;
import com.ppdai.das.strategy.TableShardingContext;

public class AbstractConditionStrategyTest {
    private static String SHARD_COL = "SHARD_COL";
    private static String NORMAL_COL = "NORMAL_COL";
    
    private static String S0 = "0";
    private static String S1 = "1";
    private static String S2 = "2";
    private static String S3 = "3";
    private static String SX = "X";
    
    private Object[][] data = new Object[][]{
            //Single expression
            {set(S0), exp(SHARD_COL, S0)},
            {set(S1), exp(SHARD_COL, S1)},
            {set(S2), exp(SHARD_COL, S2)},
            {set(S3), exp(SHARD_COL, S3)},
            
            //For duplicate case
            {set(S0), and(exp(SHARD_COL, S0), exp(SHARD_COL, S0))},
            {set(S0), or(exp(SHARD_COL, S0), exp(SHARD_COL, S0))},
            
            //Base and or test
            {non(), and(exp(SHARD_COL, S0), exp(SHARD_COL, S1))},
            {set(S0, S1), or(exp(SHARD_COL, S0), exp(SHARD_COL, S1))},
            
            //For shard empty short-circuit case 
            {non(), and(exp(SHARD_COL, S0), exp(SHARD_COL, S1), exp(SHARD_COL, S1))},
            {non(), and(exp(SHARD_COL, S0), exp(SHARD_COL, S1), exp(SHARD_COL, S2), exp(SHARD_COL, S3))},
            
            //For shard full short-circuit case
            {all(), or(exp(SHARD_COL, S0), exp(SHARD_COL, S1), exp(SHARD_COL, S2), exp(SHARD_COL, S3))},

            //For shard irrelevant column
            {all(), exp(NORMAL_COL, S0)},
            {set(S0), and(exp(SHARD_COL, S0), exp(NORMAL_COL, S0))},
            {all(), or(exp(SHARD_COL, S0), exp(NORMAL_COL, S1))},
            
            //For nested case
            {set(S1), and(exp(SHARD_COL, S1), and(exp(SHARD_COL, S1), and(exp(SHARD_COL, S1))))},
            {set(S1), or(exp(SHARD_COL, S1), or(exp(SHARD_COL, S1), or(exp(SHARD_COL, S1))))},
            
            {non(), and(exp(SHARD_COL, S0), and(exp(SHARD_COL, S0), exp(SHARD_COL, S1)))},
            {set(S0), and(exp(SHARD_COL, S0), or(exp(SHARD_COL, S0), exp(SHARD_COL, S1)))},
            
            //For deep nested case
            {non(), and(exp(SHARD_COL, S0), and(exp(SHARD_COL, S0), exp(SHARD_COL, S1)))},
            {set(S1), or(exp(SHARD_COL, S1), or(exp(SHARD_COL, S1), exp(SHARD_COL, S1)))},
    };

    AbstractConditionStrategy test = new AbstractConditionStrategy() {

        @Override
        public boolean isDbShardingRelated(ConditionContext ctx) {
            return ctx.getColumnName().equalsIgnoreCase(SHARD_COL);
        }

        @Override
        public Set<String> locateDbShards(ConditionContext ctx) {
            return set(ctx.getValue().toString());
        }

        @Override
        public boolean isTableShardingRelated(TableConditionContext ctx) {
            return ctx.getColumnName().equalsIgnoreCase(SHARD_COL);
        }

        @Override
        public Set<String> locateTableShards(TableConditionContext ctx) {
            return set(ctx.getValue().toString());
        }

        @Override
        public Set<String> locateDbShardsByValue(ShardingContext ctx, Object shardValue) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<String> locateTableShardsByValue(TableShardingContext ctx, Object tableShardValue) {
            // TODO Auto-generated method stub
            return null;
        }
    };

    private static Set<String> set(String...values) {
        Set<String> shardsSet = new HashSet<>();
        for(String s: values)
            shardsSet.add(s);
        return shardsSet;
    }
    
    private static Set<String> non() {
        return new HashSet<>();
    }

    private static Set<String> all() {
        return set(S0, S1, S2, S3);
    }

    @Before
    public void setup() throws Exception {
    }
    
    private static Condition exp(String columnName, Object value) {
        return new ColumnCondition(OperatorEnum.EQUAL, "tableName", columnName, value);
    }

    private static Condition and(Condition...conditions) {
        ConditionList cl = ConditionList.andList();
        for(Condition c: conditions)
            cl.add(c);
        return cl;
    }

    private static Condition or(Condition...conditions) {
        ConditionList cl = ConditionList.orList();
        for(Condition c: conditions)
            cl.add(c);
        return cl;
    }
    
    private static Condition not(Condition condition) {
        return condition.reverse();
    }
    
    @Test
    public void testLocateDbShards() throws SQLException {
        Set<String> exp;
        Condition condition;

        for(Object[] testData: data) {
            exp = (Set<String>)testData[0];
            condition = (Condition)testData[1];
            
            ConditionList conditions = ConditionList.andList();
            conditions.add(condition);

            //Test DB shard locating
            assertEquals(exp, test.locateDbShards(new ShardingContext("appId", "logicDbName", all(), new Hints(), conditions)));
        }
    }

    @Test
    public void testLocateTableShards() throws SQLException {
        Set<String> exp;
        Condition condition;

        for(Object[] testData: data) {
            exp = (Set<String>)testData[0];
            condition = (Condition)testData[1];
            
            ConditionList conditions = ConditionList.andList();
            conditions.add(condition);

            //Test table shard locating
            assertEquals(exp, test.locateTableShards(new TableShardingContext("appId", "logicDbName", null, all(), new Hints(), conditions)));

        }
    }

    @Test
    public void testIllegalShard() throws SQLException {
        ConditionList conditions = ConditionList.andList();
        conditions.add(exp(SHARD_COL, SX));

        try {
            test.locateDbShards(new ShardingContext("appId", "logicDbName", all(), new Hints(), conditions));
            fail();
        } catch (Exception e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
    }

    @Test
    public void testWrongDbTableConfig() throws Exception {
        Map<String, String> settings = new HashMap<>();
        
        try {
            test.initialize(settings);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), IllegalArgumentException.class);
        }
    }
}
