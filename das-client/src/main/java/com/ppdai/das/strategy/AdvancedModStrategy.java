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
        
        if(settings.containsKey(MOD)) {
            dbLoactor = new ModShardLocator<>(Integer.parseInt(settings.get(MOD)));
        }
        
        if(settings.containsKey(TABLE_MOD)) {
            Integer mod = Integer.parseInt(settings.get(TABLE_MOD));
            tableLoactor = new ModShardLocator<>(mod);
            
            Set<String> allShards = new HashSet<>();
            for(int i = 0; i < mod; i++)
                allShards.add(String.valueOf(i));
            
            setAllTableShards(allShards);
            setShardByTable(true);
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