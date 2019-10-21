package com.ppdai.das.core;

public interface FreshnessReader {
    int INVALID = -1;
    
    /**
     * get freshness for given slave db.
     * 
     * @param logicDbName
     * @param slaveConnectionString
     * @param shard
     * @return
     */
    int getSlaveFreshness(String logicDbName, String slaveConnectionString, String shard)  throws Exception;
}
