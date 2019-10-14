package com.ppdai.das.strategy;

import static com.ppdai.das.strategy.OperatorEnum.EQUAL;
import static com.ppdai.das.strategy.OperatorEnum.GREATER_THAN;
import static com.ppdai.das.strategy.OperatorEnum.GREATER_THAN_OR_EQUAL;
import static com.ppdai.das.strategy.OperatorEnum.LESS_THAN;
import static com.ppdai.das.strategy.OperatorEnum.LESS_THAN_OR_EQUAL;
import static com.ppdai.das.strategy.OperatorEnum.NOT_EQUAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.Hints;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.strategy.AdvancedModStrategy;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionList;
import com.ppdai.das.strategy.OperatorEnum;
import com.ppdai.das.strategy.ShardingContext;
import com.ppdai.das.strategy.TableShardingContext;

public class AdvancedModStrategyTest {
    private static int MOD = 10;
    private static String logicDbName = "db";

    private static String tableName = "table";
    private static String columnName = "CountryID";
    private static String xolumnName = "XountryID";
    private AdvancedModStrategy strategy;
    
    public static Object[][] data = new Object[][]{
            {all(), xop(EQUAL, 0)},
            {all(), xop(EQUAL, 1)},
            {all(), xop(EQUAL, MOD + 1)},
            {all(), xop(EQUAL, MOD - 1)},
            {all(), not(xop(EQUAL, 0))},
            {all(), not(not(xop(EQUAL, 0)))},
            
            {all(), xop(NOT_EQUAL, 0)},
            {all(), not(xop(NOT_EQUAL, 0))},
            {all(), not(not(xop(NOT_EQUAL, 0)))},

            {all(), xop(GREATER_THAN, 0)},
            {all(), not(xop(GREATER_THAN, 0))},
            {all(), not(not(xop(GREATER_THAN, 0)))},

            {all(), xop(LESS_THAN, 0)},
            {all(), not(xop(LESS_THAN, 0))},
            {all(), not(not(xop(LESS_THAN, 0)))},

            {all(), xop(GREATER_THAN_OR_EQUAL, 0)},
            {all(), not(xop(GREATER_THAN_OR_EQUAL, 0))},
            {all(), not(not(xop(GREATER_THAN_OR_EQUAL, 0)))},

            {all(), xop(LESS_THAN_OR_EQUAL, 0)},
            {all(), not(xop(LESS_THAN_OR_EQUAL, 0))},
            {all(), not(not(xop(LESS_THAN_OR_EQUAL, 0)))},

            {set(0), op(EQUAL, 0)},
            {set(0), op(EQUAL, MOD)},
            {set(0), op(EQUAL, MOD * 2)},
            {set(1), op(EQUAL, 1)},
            {set(1), op(EQUAL, 1 + MOD)},
            {set(1), op(EQUAL, 1 + MOD * 2)},
            {set(MOD - 1), op(EQUAL, MOD -1)},
            {set(MOD - 1), op(EQUAL, MOD * 2 -1)},

            {all(), op(OperatorEnum.GREATER_THAN, 1)},
            {all(), op(OperatorEnum.GREATER_THAN_OR_EQUAL, 1)},
            {all(), op(OperatorEnum.LESS_THAN, 1)},
            {all(), op(OperatorEnum.LESS_THAN_OR_EQUAL, 1)},

            {all(), not(op(OperatorEnum.GREATER_THAN, 1))},
            {all(), not(op(OperatorEnum.GREATER_THAN_OR_EQUAL, 1))},
            {all(), not(op(OperatorEnum.LESS_THAN, 1))},
            {all(), not(op(OperatorEnum.LESS_THAN_OR_EQUAL, 1))},

            {all(), not(not(op(OperatorEnum.GREATER_THAN, 1)))},
            {all(), not(not(op(OperatorEnum.GREATER_THAN_OR_EQUAL, 1)))},
            {all(), not(not(op(OperatorEnum.LESS_THAN, 1)))},
            {all(), not(not(op(OperatorEnum.LESS_THAN_OR_EQUAL, 1)))},

            {set(1, 2, 3), between(1, 3)},
            {set(1, 2, 3), between(MOD + 1, MOD + 3)},
            {set(1, 2, 3), between(MOD * 2 + 1, MOD * 2 + 3)},
            {set(0, 1, 2, 3, 8, 9), between(8,  13)},
            {set(0, 1, 2, 3, 8, 9), between(MOD + 8,  MOD + 13)},
            {set(0, 1, 2, 3, 8, 9), between(MOD * 2 + 8,  MOD * 2 + 13)},
            
            {all(), notBetween(1, 3)},
            {all(), notBetween(MOD + 1, MOD + 3)},
            {all(), not(between(MOD * 2 + 1, MOD * 2 + 3))},
            {set(1, 2, 3), not(not(between(1, 3)))},
            {set(1, 2, 3), not(not(between(MOD + 1, MOD + 3)))},
            {set(1, 2, 3), not(notBetween(MOD * 2 + 1, MOD * 2 + 3))},
            
            {set(1, 2, 3), in(1, 2, 3)},
            {set(1, 2, 3), in(MOD + 1, MOD + 2, MOD + 3)},

            {all(), notIn(1, 2, 3)},
            {all(), not(in(1, 2, 3))},
            {set(1, 2, 3), not(not(in(1, 2, 3)))},
            {set(1, 2, 3), not(notIn(MOD + 1, MOD + 2, MOD + 3))},
            
            //Test for combination EQUAL
            {set(0, 1), or(op(EQUAL, 0), op(EQUAL, 1))},
            {set(0, 1), or(op(EQUAL, MOD), op(EQUAL, MOD + 1))},
            {set(0, 1), or(op(EQUAL, MOD * 2), op(EQUAL, MOD * 2 + 1))},
            
            {set(0, 1, 2), or(op(EQUAL, 0), op(EQUAL, 1), op(EQUAL, 2))},
            {set(0, 1, 2), or(op(EQUAL, MOD), op(EQUAL, MOD + 1), op(EQUAL, MOD + 2))},
            {set(0, 1, 2), or(op(EQUAL, MOD * 2), op(EQUAL, MOD * 2 + 1), op(EQUAL, MOD * 2 + 2))},

            {non(), and(op(EQUAL, 0), op(EQUAL, 1))},
            {non(), and(op(EQUAL, MOD), op(EQUAL, MOD + 1))},
            {non(), and(op(EQUAL, MOD * 2), op(EQUAL, MOD * 2 + 1))},
            
            {non(), and(op(EQUAL, 0), op(EQUAL, 1), op(EQUAL, 2))},
            {non(), and(op(EQUAL, MOD), op(EQUAL, MOD + 1), op(EQUAL, MOD + 2))},
            {non(), and(op(EQUAL, MOD * 2), op(EQUAL, MOD * 2 + 1), op(EQUAL, MOD * 2 + 2))},
            
            {all(), or(op(EQUAL, 0), op(NOT_EQUAL, 1))},
            {all(), or(op(EQUAL, 0), op(GREATER_THAN, 1))},
            {all(), or(op(EQUAL, 0), op(GREATER_THAN_OR_EQUAL, 1))},
            {all(), or(op(EQUAL, 0), op(LESS_THAN, 1))},
            {all(), or(op(EQUAL, 0), op(LESS_THAN_OR_EQUAL, 1))},
            {all(), or(op(EQUAL, 0), notBetween(0, 2))},
            {all(), or(op(EQUAL, 0), notIn(0, 2))},

            {set(0), and(op(EQUAL, 0), op(NOT_EQUAL, 1))},
            {set(0), and(op(EQUAL, 0), op(GREATER_THAN, 1))},
            {set(0), and(op(EQUAL, 0), op(GREATER_THAN_OR_EQUAL, 1))},
            {set(0), and(op(EQUAL, 0), op(LESS_THAN, 1))},
            {set(0), and(op(EQUAL, 0), op(LESS_THAN_OR_EQUAL, 1))},
            {set(0), and(op(EQUAL, 0), notBetween(0, 2))},
            {set(0), and(op(EQUAL, 0), notIn(0, 2))},

            //Test for combination BETWEEN
            {set(0, 1, 2), or(between(1, 2), op(EQUAL, 0))},
            
            {all(), or(between(0, 2), op(NOT_EQUAL, 1))},
            {all(), or(between(0, 2), op(GREATER_THAN, 1))},
            {all(), or(between(0, 2), op(GREATER_THAN_OR_EQUAL, 1))},
            {all(), or(between(0, 2), op(LESS_THAN, 1))},
            {all(), or(between(0, 2), op(LESS_THAN_OR_EQUAL, 1))},
            {all(), or(between(0, 2), notBetween(0, 2))},
            {all(), or(between(0, 2), notIn(0, 2))},

            {set(1), and(between(0, 2), op(EQUAL, 1))},
            {non(), and(between(0, 2), op(EQUAL, 3))},
            
            {set(0, 1, 2), and(between(0, 2), op(NOT_EQUAL, 1))},
            {set(0, 1, 2), and(between(0, 2), op(GREATER_THAN, 1))},
            {set(0, 1, 2), and(between(0, 2), op(GREATER_THAN_OR_EQUAL, 1))},
            {set(0, 1, 2), and(between(0, 2), op(LESS_THAN, 1))},
            {set(0, 1, 2), and(between(0, 2), op(LESS_THAN_OR_EQUAL, 1))},
            {set(0, 1, 2), and(between(0, 2), notBetween(0, 2))},
            {set(0, 1, 2), and(between(0, 2), notBetween(0, 2))},

            //Test for combination IN
            {set(0, 1, 2), or(in(1, 2), op(EQUAL, 0))},
            
            {all(), or(in(0, 2), op(NOT_EQUAL, 1))},
            {all(), or(in(0, 2), op(GREATER_THAN, 1))},
            {all(), or(in(0, 2), op(GREATER_THAN_OR_EQUAL, 1))},
            {all(), or(in(0, 2), op(LESS_THAN, 1))},
            {all(), or(in(0, 2), op(LESS_THAN_OR_EQUAL, 1))},
            {all(), or(in(0, 2), notBetween(0, 2))},
            {all(), or(in(0, 2), notIn(0, 2))},

            {set(1), and(in(0, 1, 2), op(EQUAL, 1))},
            {non(), and(in(0, 2), op(EQUAL, 3))},
            
            {set(0, 2), and(in(0, 2), op(NOT_EQUAL, 1))},
            {set(0, 2), and(in(0, 2), op(GREATER_THAN, 1))},
            {set(0, 2), and(in(0, 2), op(GREATER_THAN_OR_EQUAL, 1))},
            {set(0, 2), and(in(0, 2), op(LESS_THAN, 1))},
            {set(0, 2), and(in(0, 2), op(LESS_THAN_OR_EQUAL, 1))},
            {set(0, 2), and(in(0, 2), notBetween(0, 2))},
            {set(0, 2), and(in(0, 2), notBetween(0, 2))},

        };

    @Before
    public void setup() throws Exception {
        strategy = new AdvancedModStrategy();
        Map<String, String> settings = new HashMap<>();
        
        settings.put(AdvancedModStrategy.MOD, String.valueOf(MOD));
        settings.put(AdvancedModStrategy.COLUMNS, columnName);

        settings.put(AdvancedModStrategy.TABLE_MOD, String.valueOf(MOD));
        settings.put(AdvancedModStrategy.TABLE_COLUMNS, columnName);

        strategy.initialize(settings);
    }

    private static Set<String> non() {
        return new HashSet<>();
    }

    private static Set<String> all() {
        return set(0, 1, 2, 3, 4, 5,6, 7,8, 9);
    }
    
    private static Set<String> set(int...shards) {
        Set<String> shardsSet = new HashSet<>();
        for(int s: shards)
            shardsSet.add(String.valueOf(s));
        return shardsSet;
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

    private static Condition xop(OperatorEnum op, Object value) {
        return new ColumnCondition(op, tableName, xolumnName, value);
    }

    private static Condition op(OperatorEnum op, Object value) {
        return new ColumnCondition(op, tableName, columnName, value);
    }

    private static Condition between(Object value, Object value2) {
        return new ColumnCondition(OperatorEnum.BEWTEEN, tableName, columnName, value, value2);
    }
    
    private static Condition notBetween(Object value, Object value2) {
        return between(value, value2).reverse();
    }
    
    private static Condition in(Object...values) {
        List<Object> vl = new ArrayList<>();
        for(Object v: values)
            vl.add(v);
        return new ColumnCondition(OperatorEnum.IN, tableName, columnName, vl);
    }
    
    private static Condition notIn(Object...values) {
        return in(values).reverse();
    }
//
//    private void testShards(Set<String> exp, Condition condition) throws SQLException {
//        ConditionList conditions = new ConditionList(true);
//        conditions.add(condition);
//        Set<String> shards = strategy.locateDbShards(new ShardingContext("appId", logicDbName, all(), new DalHints(), conditions));
//
//        if(shards.size() == exp.size() && shards.containsAll(exp))
//           return;
//
//        fail("Expected: "+ exp.toString() + "\nActual: " + shards.toString());
//    }

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
            assertEquals(exp, strategy.locateDbShards(new ShardingContext("appId", "logicDbName", all(), new Hints(), conditions)));
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
            assertEquals(exp, strategy.locateTableShards(new TableShardingContext("appId", "logicDbName", null, all(), new Hints(), conditions)));

        }
    }

}
