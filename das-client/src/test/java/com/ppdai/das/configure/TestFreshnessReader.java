package com.ppdai.das.configure;

import java.util.HashMap;
import java.util.Map;

import com.ppdai.das.core.FreshnessReader;


/**
 * see :com.ppdai.das.configure.SlaveFreshnessScannerMysqlTest
 */
public class TestFreshnessReader implements FreshnessReader {
    static Map<String, Integer> freshnessMap = new HashMap<>();

    public TestFreshnessReader() {
        freshnessMap.put("MySqlShard_0", 3);
        freshnessMap.put("MySqlShard_1", 5);
        freshnessMap.put("dal_shard_0", 7);
        freshnessMap.put("dal_shard_1", 9);
    }


    @Override
    public int getSlaveFreshness(String logicDbName, String slaveDbName, String shard) {
        // Just return whant we have
        return freshnessMap.get(slaveDbName);
    }
}