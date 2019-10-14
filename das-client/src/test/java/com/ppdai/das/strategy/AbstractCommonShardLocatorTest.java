package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.ppdai.das.client.Hints;

public class AbstractCommonShardLocatorTest {
    private static final String EQUAL = "equal";
    private static final String GREATER_THAN = "greaterThan";
    private static final String LESS_THAN = "lessThan";
    private static final String BETWEEN = "between";
    
    private static Set<String> all = set(EQUAL, GREATER_THAN, LESS_THAN, BETWEEN);
    
    private class TestAbstractConditionShardLocator extends AbstractCommonShardLocator<ConditionContext> {

        @Override
        public Set<String> locateForEqual(ConditionContext context) {
            return set(EQUAL);
        }

        @Override
        public Set<String> locateForGreaterThan(ConditionContext context) {
            return set(GREATER_THAN);
        }

        @Override
        public Set<String> locateForLessThan(ConditionContext context) {
            return set(LESS_THAN);
        }
        
        @Override
        public Set<String> locateForBetween(ConditionContext ctx) {
            return set(BETWEEN);
        }
    };

    private TestAbstractConditionShardLocator test = new TestAbstractConditionShardLocator();
        
    private TestAbstractConditionShardLocator test2 = new TestAbstractConditionShardLocator() {
        @Override
        public Set<String> locateForEqual(ConditionContext context) {
            return set(context.getValue().toString());
        }
    };
    
    private static Set<String> set(String...values) {
        Set<String> shardsSet = new HashSet<>();
        for(String s: values)
            shardsSet.add(s);
        return shardsSet;
    }

    private static ConditionContext exp(OperatorEnum op, Object value) {
        Set<String> all = set(EQUAL, GREATER_THAN, LESS_THAN, BETWEEN);
        return new ShardingContext("appId", "logicDbName", all, new Hints(), ConditionList.andList()).create(new ColumnCondition(op, "tableName", "col", value));
    }

    @Test
    public void testLocateShards() {
        assertEquals(set(EQUAL), test.locateShards(exp(OperatorEnum.EQUAL, "eq")));
        assertEquals(set(GREATER_THAN, LESS_THAN), test.locateShards(exp(OperatorEnum.NOT_EQUAL, "eq")));

        assertEquals(set(GREATER_THAN), test.locateShards(exp(OperatorEnum.GREATER_THAN, "eq")));
        assertEquals(set(GREATER_THAN, EQUAL), test.locateShards(exp(OperatorEnum.GREATER_THAN_OR_EQUAL, "eq")));

        assertEquals(set(LESS_THAN), test.locateShards(exp(OperatorEnum.LESS_THAN, "eq")));
        assertEquals(set(LESS_THAN, EQUAL), test.locateShards(exp(OperatorEnum.LESS_THAN_OR_EQUAL, "eq")));

        assertEquals(set(BETWEEN), test.locateShards(exp(OperatorEnum.BEWTEEN, "eq")));
        assertEquals(set(GREATER_THAN, LESS_THAN), test.locateShards(exp(OperatorEnum.NOT_BETWEEN, "eq")));
        
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        assertEquals(set(EQUAL), test.locateShards(exp(OperatorEnum.IN, list)));
        assertEquals(set(GREATER_THAN, LESS_THAN), test.locateShards(exp(OperatorEnum.NOT_IN, list)));
        
        assertEquals(all, test.locateShards(exp(OperatorEnum.LIKE, "eq")));
        assertEquals(all, test.locateShards(exp(OperatorEnum.NOT_LIKE, "eq")));

        assertEquals(all, test.locateShards(exp(OperatorEnum.IS_NULL, "eq")));
        assertEquals(all, test.locateShards(exp(OperatorEnum.IS_NOT_NULL, "eq")));
    }

    @Test
    public void testGetAllShards() {
        assertEquals(all, test.getAllShards(exp(OperatorEnum.EQUAL, "sa")));
    }

    @Test
    public void testIsAlreadyAllShards() {
        assertFalse(test.isAlreadyAllShards(set("1"), null));
        assertFalse(test.isAlreadyAllShards(set("1"), set()));
        
        try {
            test.isAlreadyAllShards(set("1"), set("2"));
            fail();
        } catch (Exception e) {
            assertEquals(IllegalStateException.class, e.getClass());
        }
        
        assertTrue(test.isAlreadyAllShards(set("1"), set("1")));
    }
}
