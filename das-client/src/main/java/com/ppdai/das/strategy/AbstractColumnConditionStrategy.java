package com.ppdai.das.strategy;

import java.util.Map;
import java.util.Set;

/**
 * A helper strategy that implements isDbShardingRelated and isTableShardingRelated by 
 * checking if column is identified for sharding in condition.
 * 
 * @author hejiehui
 *
 */
public abstract class AbstractColumnConditionStrategy extends AbstractConditionStrategy {
    /**
     * Key used to declared columns for locating DB shards.
     */
    public static final String COLUMNS = "columns";

    /**
     * Key used to declared columns for locating table shards.
     */
    public static final String TABLE_COLUMNS = "tableColumns";

    @Override
    public abstract Set<String> locateDbShardsByValue(ShardingContext ctx, Object shardValue);

    @Override
    public abstract Set<String> locateDbShards(ConditionContext ctx);
    
    @Override
    public abstract Set<String> locateTableShardsByValue(TableShardingContext ctx, Object tableShardValue);
    
    @Override
    public abstract Set<String> locateTableShards(TableConditionContext ctx);

    private Set<String> dbShardColumns;
    private Set<String> tableShardColumns;

    @Override
    public void initialize(Map<String, String> settings) {
        super.initialize(settings);
        
        if(settings.containsKey(COLUMNS)) {
            dbShardColumns = parseNames(settings.get(COLUMNS));
            setShardByDb(true);
        }
        
        if(settings.containsKey(TABLE_COLUMNS)) {
            tableShardColumns = parseNames(settings.get(TABLE_COLUMNS));
            setShardByTable(true);
        }
    }

    @Override
    public boolean isDbShardingRelated(ConditionContext ctx) {
        return dbShardColumns.contains(ctx.getColumnName().toLowerCase());
    }

    @Override
    public boolean isTableShardingRelated(TableConditionContext ctx) {
        return tableShardColumns.contains(ctx.getColumnName().toLowerCase());
    }
}