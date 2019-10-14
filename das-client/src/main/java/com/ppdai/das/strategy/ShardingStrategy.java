package com.ppdai.das.strategy;

import java.util.Map;
import java.util.Set;

/**
 * Top level abstraction of sharding logic
 * 
 * @author hejiehui
 *
 */
public interface ShardingStrategy {
    void initialize(Map<String, String> settings);

    boolean isShardByDb();

    Set<String> locateDbShards(ShardingContext ctx);

    boolean isShardByTable();
    
    Set<String> getAllTableShards();

    boolean isShardingEnable(String logicTableName);
    
    Set<String> locateTableShards(TableShardingContext ctx);
    
    String getTableName(String logicTableName, String shard);
}
