package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.ppdai.das.client.Hints;

public class AbstractConditionShardLocatorTest {

    private class TestAbstractConditionShardLocator extends AbstractConditionShardLocator<ConditionContext> {

        @Override
        public Set<String> locateForEqual(ConditionContext context) {
            return set("locateForEqual");
        }

        @Override
        public Set<String> locateForNotEqual(ConditionContext context) {
            return set("locateForNotEqual");
        }

        @Override
        public Set<String> locateForGreaterThan(ConditionContext context) {
            return set("locateForGreaterThan");
        }

        @Override
        public Set<String> locateForGreaterThanOrEqual(ConditionContext context) {
            return set("locateForGreaterThanOrEqual");
        }

        @Override
        public Set<String> locateForLessThan(ConditionContext context) {
            return set("locateForLessThan");
        }

        @Override
        public Set<String> locateForLessThanOrEqual(ConditionContext context) {
            return set("locateForLessThanOrEqual");
        }

        @Override
        public Set<String> locateForBetween(ConditionContext context) {
            return set("locateForBetween");
        }

        @Override
        public Set<String> locateForNotBetween(ConditionContext context) {
            return set("locateForNotBetween");
        }

        @Override
        public Set<String> locateForIn(ConditionContext context) {
            return set("locateForIn");
        }

        @Override
        public Set<String> locateForNotIn(ConditionContext context) {
            return set("locateForNotIn");
        }

        @Override
        public Set<String> locateForLike(ConditionContext context) {
            return set("locateForLike");
        }

        @Override
        public Set<String> locateForNotLike(ConditionContext context) {
            return set("locateForNotLike");
        }        

        @Override
        public Set<String> locateForIsNull(ConditionContext context) {
            return set("locateForIsNull");
        }

        @Override
        public Set<String> locateForIsNotNull(ConditionContext context) {
            return set("locateForIsNotNull");
        }
        
        public ConditionContext createConditionContext(ConditionContext conditionContext, OperatorEnum newOperator, Object newValue) {
            return super.createConditionContext(conditionContext, newOperator, newValue);
        }
        
        protected Set<String> locateForCombination(ConditionContext context, OperatorEnum op1, Object value1, OperatorEnum op2, Object value2) {
            return super.locateForCombination(context, op1, value1, op2, value2);
        }

        protected Set<String> locateForIntersection(ConditionContext context, OperatorEnum op1, Object value1, OperatorEnum op2, Object value2) {
            return super.locateForIntersection(context, op1, value1, op2, value2);
        }
        
        protected Set<String> exclude(ConditionContext context, Set<String> shards) {
            return super.exclude(context, shards);
        }

        protected boolean isAlreadyAllShards(Set<String> allShards, Set<String> shards) {
            return super.isAlreadyAllShards(allShards, shards);
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
        return new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints(), ConditionList.andList()).create(new ColumnCondition(op, "tableName", "col", value));
    }

    @Test
    public void testLocateShards() {
        assertEquals(set("locateForEqual"), test.locateShards(exp(OperatorEnum.EQUAL, "eq")));
        assertEquals(set("locateForNotEqual"), test.locateShards(exp(OperatorEnum.NOT_EQUAL, "eq")));

        assertEquals(set("locateForGreaterThan"), test.locateShards(exp(OperatorEnum.GREATER_THAN, "eq")));
        assertEquals(set("locateForGreaterThanOrEqual"), test.locateShards(exp(OperatorEnum.GREATER_THAN_OR_EQUAL, "eq")));

        assertEquals(set("locateForLessThan"), test.locateShards(exp(OperatorEnum.LESS_THAN, "eq")));
        assertEquals(set("locateForLessThanOrEqual"), test.locateShards(exp(OperatorEnum.LESS_THAN_OR_EQUAL, "eq")));

        assertEquals(set("locateForBetween"), test.locateShards(exp(OperatorEnum.BEWTEEN, "eq")));
        assertEquals(set("locateForNotBetween"), test.locateShards(exp(OperatorEnum.NOT_BETWEEN, "eq")));
        
        assertEquals(set("locateForIn"), test.locateShards(exp(OperatorEnum.IN, "eq")));
        assertEquals(set("locateForNotIn"), test.locateShards(exp(OperatorEnum.NOT_IN, "eq")));
        
        assertEquals(set("locateForLike"), test.locateShards(exp(OperatorEnum.LIKE, "eq")));
        assertEquals(set("locateForNotLike"), test.locateShards(exp(OperatorEnum.NOT_LIKE, "eq")));

        assertEquals(set("locateForIsNull"), test.locateShards(exp(OperatorEnum.IS_NULL, "eq")));
        assertEquals(set("locateForIsNotNull"), test.locateShards(exp(OperatorEnum.IS_NOT_NULL, "eq")));
    }

    @Test
    public void testGetAllShards() {
        assertEquals(set("1",  "2", "3"), test.getAllShards(exp(OperatorEnum.EQUAL, "sa")));
    }

    @Test
    public void testCreateConditionContext() {
        ConditionContext ctx = test.createConditionContext(exp(OperatorEnum.EQUAL, "1"), OperatorEnum.BEWTEEN, "2");
        assertEquals("2", ctx.getValue());
        assertEquals(OperatorEnum.BEWTEEN, ctx.getOperator());

    }

    @Test
    public void testLocateForCombination() {
        assertEquals(set("2",  "3"), 
                test2.locateForCombination(exp(OperatorEnum.EQUAL, "1"), OperatorEnum.EQUAL, "2", OperatorEnum.EQUAL, "3"));
        
        ConditionContext ctx = new ShardingContext("appId", "logicDbName", set("2", "3"), new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", "col", ""));
        
        // Test for full shards
        assertEquals(set("2", "3"), test2.locateForCombination(ctx, OperatorEnum.EQUAL, "2", OperatorEnum.EQUAL, "3"));
    }

    @Test
    public void testLocateForIntersection() {
        ConditionContext ctx = new ShardingContext("appId", "logicDbName", set("1", "2", "3"), new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", "col", ""));
        assertEquals(set(), 
                test2.locateForIntersection(ctx, OperatorEnum.EQUAL, "2", OperatorEnum.EQUAL, "3"));
        
        // Test for full shards
        assertEquals(set("2"), test2.locateForIntersection(ctx, OperatorEnum.EQUAL, "2", OperatorEnum.EQUAL, "2"));
    }

    @Test
    public void testExclude() {
        
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
