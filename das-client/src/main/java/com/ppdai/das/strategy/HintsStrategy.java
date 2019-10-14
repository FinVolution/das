package com.ppdai.das.strategy;

import java.util.Map;
import java.util.Set;

public class HintsStrategy extends AbstractShardingStrategy {
    public static final String SHARD_BY_DB = "shardByDb";
    public static final String SHARD_BY_TABLE = "shardByTable";

    @Override
    public void initialize(Map<String, String> settings) {
        super.initialize(settings);

        if(settings.containsKey(SHARD_BY_DB)) {
            setShardByDb(Boolean.parseBoolean(settings.get(SHARD_BY_DB)));
        }
        
        if(settings.containsKey(SHARD_BY_TABLE)) {
            setShardByTable(Boolean.parseBoolean(settings.get(SHARD_BY_TABLE)));
        }
    }

    @Override
    public Set<String> locateDbShards(ShardingContext ctx) {
        return toSet(ctx.getHints().getShard());
    }

    @Override
    public Set<String> locateTableShards(TableShardingContext ctx) {
        return toSet(ctx.getHints().getTableShard());
    }
}
