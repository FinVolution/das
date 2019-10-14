package com.ppdai.das.strategy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    AbstractConditionShardLocatorTest.class,
    AbstractCommonShardLocatorTest.class,
    AbstractConditionStrategyTest.class,
//    AbstractCycledShardLocatorTest.class,
    AbstractShardingStrategyTest.class,
    AdvancedModStrategyTest.class,
    ModShardLocatorTest.class,
    HintsStrategyTest.class
})
public class AllStrategyTests {

}
