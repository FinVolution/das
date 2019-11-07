package com.ppdai.das.strategy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author hejiehui
 *
 */
public class AdvancedModStrategy extends AbstractConditionStrategy {
    /**
     * Key used to declared mod for locating DB shards.
     */
    public static final String MOD = "mod";

    /**
     * Key used to declared mod for locating table shards.
     */
    public static final String TABLE_MOD = "tableMod";
    
    private ModShardLocator<ConditionContext> dbLoactor;
    private ModShardLocator<TableConditionContext> tableLoactor;
    
    @Override
    public void initialize(Map<String, String> settings) {
        super.initialize(settings);
        
        if(isShardByDb()) {
            if(!settings.containsKey(MOD))
                throw new IllegalArgumentException("Property " + MOD + " is required for shard by database");

            dbLoactor = new ModShardLocator<>(Integer.parseInt(settings.get(MOD)));
        }
        
        if(isShardByTable()) {
            if(!settings.containsKey(TABLE_MOD))
                throw new IllegalArgumentException("Property " + TABLE_MOD + " is required for shard by table");

            Integer mod = Integer.parseInt(settings.get(TABLE_MOD));
            tableLoactor = new ModShardLocator<>(mod);
            
            Set<String> allShards = new HashSet<>();
            for(int i = 0; i < mod; i++)
                allShards.add(String.valueOf(i));
            
            setAllTableShards(allShards);
        }
    }

    @Override
    public Set<String> locateDbShardsByValue(ShardingContext ctx, Object shardValue) {
        return dbLoactor.locateByValue(shardValue);
    }

    @Override
    public Set<String> locateDbShards(ConditionContext ctx) {
        return dbLoactor.locateShards(ctx);
    }

    @Override
    public Set<String> locateTableShardsByValue(TableShardingContext ctx, Object tableShardValue) {
        return tableLoactor.locateByValue(tableShardValue);
    }

    @Override
    public Set<String> locateTableShards(TableConditionContext ctx) {
        return tableLoactor.locateShards(ctx);
    }
}