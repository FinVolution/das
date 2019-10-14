package com.ppdai.das;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ppdai.das.client.AllDasClientTests;


@RunWith(Suite.class)
@SuiteClasses({
        AllDasClientTests.class,
})
public class AllTests  {

}
