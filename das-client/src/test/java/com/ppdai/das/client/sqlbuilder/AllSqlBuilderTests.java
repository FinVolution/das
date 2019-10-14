package com.ppdai.das.client.sqlbuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    ColumnDefinitionTest.class,
    ColumnTest.class,
    ConditionBuilderTest.class,
    MeltdownTest.class,
    ParameterDefinitionTest.class,
    ParameterTest.class,
    SegmentConstantsTest.class,
    SqlBuilderParameterDefinitionTest.class,
    SqlBuilderParameterTest.class,
    SqlBuilderSerializeTest.class,
    SqlBuilderTest.class,
    TableTest.class,
    })
public class AllSqlBuilderTests {
}
