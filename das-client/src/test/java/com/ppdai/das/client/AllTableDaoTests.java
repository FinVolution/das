package com.ppdai.das.client;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    LogicDeletionDaoUtilMethodsTest.class,
    TableDaoTest.class,
    TableDaoShardByDbTableTest.class,
    TableDaoShardByDbTest.class,
    TableDaoShardByTableTest.class,
})
public class AllTableDaoTests {

}
