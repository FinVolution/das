package com.ppdai.das.strategy;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

import com.ppdai.das.client.sqlbuilder.AbstractColumn;

/**
 * This strategy provides common implementation for locating shards by conditions.
 * 
 * @author hejiehui
 *
 */
public abstract class AbstractConditionStrategy extends AbstractShardingStrategy {

    public abstract boolean isDbShardingRelated(ConditionContext ctx);

    public abstract Set<String> locateDbShardsByValue(ShardingContext ctx, Object shardValue);

    public abstract Set<String> locateDbShards(ConditionContext ctx);
    
    public abstract boolean isTableShardingRelated(TableConditionContext ctx);

    public abstract Set<String> locateTableShardsByValue(TableShardingContext ctx, Object tableShardValue);
    
    public abstract Set<String> locateTableShards(TableConditionContext ctx);

    @Override
    public Set<String> locateDbShards(ShardingContext ctx) {
        return ctx.hasShardValue() ?
                locateDbShardsByValue(ctx, ctx.getShardValue()):
                    locateShards(this::isDbShardingRelated, this::locateDbShards, ctx, ctx.getConditions());
    }

    @Override
    public Set<String> locateTableShards(TableShardingContext ctx) {
        return ctx.hasTableShardValue() ? 
                locateTableShardsByValue(ctx, ctx.getTableShardValue()) :
                    locateShards((tableCtx)->isTableShardingRelated((TableConditionContext)tableCtx), (tableCtx)->locateTableShards((TableConditionContext)tableCtx), ctx, ctx.getConditions());
    }
    
    private Set<String> locateShards(Predicate<ConditionContext> isRelated, Function<ConditionContext, Set<String>> locator, ShardingContext shardingContext, ConditionList conditions) {
        Set<String> allShards = shardingContext.getAllShards();
        Set<String> shards = conditions.isIntersected() ? new TreeSet<>(allShards) : new TreeSet<>();
        
        for(Condition condition: conditions.getConditions()) {
            Set<String> range;
            if(condition instanceof ConditionList)
                range = locateShards(isRelated, locator, shardingContext, (ConditionList)condition);
            else
                range = locateShards(isRelated, locator, shardingContext, (ColumnCondition)condition);

            if(!allShards.containsAll(range)) {
                range.removeAll(allShards);
                throw new IllegalStateException("Illegal shard detected:" + range);
            }

            if(conditions.isIntersected()) {
                shards.retainAll(range);

                if(shards.isEmpty())
                    break;
            }else {
                shards.addAll(range);

                if(allShards.size() == shards.size())
                    break;
            }
        }
        
        return shards;
    }
    
    private Set<String> locateShards(Predicate<ConditionContext> isRelated, Function<ConditionContext, Set<String>> locator, ShardingContext shardingContext, ColumnCondition condition) {
        ConditionContext context = shardingContext.create(condition);

        if(context.getValue() instanceof AbstractColumn)
            return shardingContext.getAllShards();

        if(!isRelated.test(context))
            return shardingContext.getAllShards();
        
        return locator.apply(context);
    }
}