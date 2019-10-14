package com.ppdai.das.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ppdai.das.client.Hints;
import org.junit.Test;


public class AbstractCycledShardLocatorTest {
    private AbstractCommonShardLocator test = new AbstractCommonShardLocator() {

        @Override
        public Set locateForEqual(ConditionContext ctx) {
            return set("Equal");
        }

        @Override
        public Set locateForGreaterThan(ConditionContext ctx) {
            return set("GreaterThan");
        }

        @Override
        public Set locateForLessThan(ConditionContext ctx) {
            return set("LessThan");
        }

        @Override
        public Set locateForBetween(ConditionContext ctx) {
            return set("Between");
        }
    };

    private static Set<String> set(String...values) {
        Set<String> shardsSet = new HashSet<>();
        for(String s: values)
            shardsSet.add(s);
        return shardsSet;
    }
    
    private static Set<String> all = set("Equal", "GreaterThan", "LessThan", "Between");
    
    private static ConditionContext exp() {
        return new ShardingContext("appId", "logicDbName", all, new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", "col", ""));
    }
    
    @Test
    public void testLocateForEqual() {
        assertEquals(set("Equal"), test.locateForEqual(exp()));
    }

    @Test
    public void testLocateForNotEqual() {
        assertEquals(set("GreaterThan", "LessThan"), test.locateForNotEqual(exp()));
    }

    @Test
    public void testLocateForLessThan() {
        assertEquals(set("LessThan"), test.locateForLessThan(exp()));
    }

    @Test
    public void testLocateForLessThanOrEqual() {
        assertEquals(set("Equal", "LessThan"), test.locateForLessThanOrEqual(exp()));
    }

    @Test
    public void testLocateForGreaterThan() {
        assertEquals(set("GreaterThan"), test.locateForGreaterThan(exp()));
    }

    @Test
    public void testLocateForGreaterThanOrEqual() {
        assertEquals(set("Equal", "GreaterThan"), test.locateForGreaterThanOrEqual(exp()));
    }

    @Test
    public void testLocateForBetween() {
        assertEquals(set("Between"), test.locateForBetween(exp()));
    }

    @Test
    public void testLocateForNotBetween() {
        assertEquals(set("GreaterThan", "LessThan"), test.locateForNotBetween(exp()));
    }

    @Test
    public void testLocateForIn() {
        List<String> values = Arrays.asList("1", "2");
        ConditionContext ctx = new ShardingContext("appId", "logicDbName", all, new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", "col", values));
        
        assertEquals(set("Equal"), test.locateForIn(ctx));
    }

    @Test
    public void testLocateForNotIn() {
        List<String> values = Arrays.asList("1", "2");
        ConditionContext ctx = new ShardingContext("appId", "logicDbName", all, new Hints(), ConditionList.andList()).create(new ColumnCondition(OperatorEnum.EQUAL, "tableName", "col", values));
        
        assertEquals(set("GreaterThan", "LessThan"), test.locateForNotIn(ctx));
    }

    @Test
    public void testLocateForLike() {
        assertEquals(all, test.locateForLike(exp()));
    }

    @Test
    public void testLocateForNotLike() {
        assertEquals(all, test.locateForNotLike(exp()));
    }
}
