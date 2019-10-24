package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ppdai.das.client.Hints;
import org.junit.Test;


import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionContext;
import com.ppdai.das.strategy.ConditionList;
import com.ppdai.das.strategy.ModShardLocator;
import com.ppdai.das.strategy.OperatorEnum;
import com.ppdai.das.strategy.ShardingContext;

public class ModShardLocatorTest {
    private static String M = "m";
    private static String N = "n";
    
    private final int mod = 3;
    private ModShardLocator<ConditionContext> test = new ModShardLocator(mod);
    
    private final Set<String> all = set(0,1,2);

    private static Set<String> set(Object...values) {
        Set<String> shardsSet = new HashSet<>();
        for(Object s: values)
            shardsSet.add(s.toString());
        return shardsSet;
    }

    private static ConditionContext exp(String col, Object value) {
        return new ShardingContext("appId", "logicDbName", set(0, 1, 2), new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", col, value));
    }

    private static ConditionContext exp(String col, Object value, Object v2) {
        return new ShardingContext("appId", "logicDbName", set(0, 1, 2), new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", col, value, v2));
    }
    
    @Test
    public void testLocateForEqual() {
        assertEquals(set(0),  test.locateForEqual(exp(M, 0)));
        assertEquals(set(1),  test.locateForEqual(exp(M, 1)));
        assertEquals(set(2),  test.locateForEqual(exp(M, 2)));
        assertEquals(set(0),  test.locateForEqual(exp(M, 3)));
        assertEquals(set(1),  test.locateForEqual(exp(M, 4)));
    }

    @Test
    public void testLocateForGreaterThan() {
        assertEquals(all,  test.locateForGreaterThan(exp(M, 0)));
        assertEquals(all,  test.locateForGreaterThan(exp(M, 1)));
        assertEquals(all,  test.locateForGreaterThan(exp(M, 2)));
        assertEquals(all,  test.locateForGreaterThan(exp(M, 3)));
        assertEquals(all,  test.locateForGreaterThan(exp(M, 4)));
    }

    @Test
    public void testLocateForLessThan() {
        assertEquals(all,  test.locateForLessThan(exp(M, 0)));
        assertEquals(all,  test.locateForLessThan(exp(M, 1)));
        assertEquals(all,  test.locateForLessThan(exp(M, 2)));
        assertEquals(all,  test.locateForLessThan(exp(M, 3)));
        assertEquals(all,  test.locateForLessThan(exp(M, 4)));
    }

    @Test
    public void testLocateForBetween() {
        assertEquals(set(0, 1),  test.locateForBetween((exp(M, 0, 1))));
        assertEquals(all,  test.locateForBetween((exp(M, 0, 2))));
        assertEquals(all,  test.locateForBetween((exp(M, 0, 3))));
        assertEquals(all,  test.locateForBetween((exp(M, 0, 4))));
        
        assertEquals(set(1, 2),  test.locateForBetween((exp(M, 1, 2))));
        assertEquals(all,  test.locateForBetween((exp(M, 1, 3))));
        assertEquals(all,  test.locateForBetween((exp(M, 1, 4))));
        
        assertEquals(set(2, 0),  test.locateForBetween((exp(M, 2, 3))));
        assertEquals(all,  test.locateForBetween((exp(M, 2, 4))));
        assertEquals(all,  test.locateForBetween((exp(M, 2, 5))));
    }
    
    @Test
    public void testLocateForBetweenSame() {
        //For same value
        assertEquals(set(1),  test.locateForBetween((exp(M, 1, 1))));
    }        

    @Test
    public void testLocateForBetweenIllegal() {
        //For illegal value
        assertEquals(set(),  test.locateForBetween((exp(M, 2, 1))));
    }

    @Test
    public void testValueType() {
        try {
            test.locateForEqual(exp(M, null));
            fail();
        }catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        assertEquals(set(0),  test.locateForEqual(exp(M, 0L)));
        assertEquals(set(0),  test.locateForEqual(exp(M, new Long(0))));
        assertEquals(set(0),  test.locateForEqual(exp(M, "0")));

        try {
            test.locateForEqual(exp(M, new Object()));
            fail();
        }catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
    }
}
