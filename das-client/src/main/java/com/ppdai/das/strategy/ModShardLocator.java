package com.ppdai.das.strategy;

import java.util.HashSet;
import java.util.Set;

public class ModShardLocator<CTX extends ConditionContext> extends AbstractCommonShardLocator<CTX> {
    private Integer mod;

    public ModShardLocator(Integer mod) {
        this.mod = mod;
    }

    public Set<String> locateByValue(Object value) {
        Set<String> shards = new HashSet<>();
        shards.add(mod(mod, value));
        return shards;
    }

    @Override
    public Set<String> locateForEqual(ConditionContext ctx) {
        return locateByValue(ctx.getValue());
    }

    public Set<String> locateForGreaterThan(CTX ctx) {
        return getAllShards(ctx);
    }

    public Set<String> locateForLessThan(CTX ctx) {
        return getAllShards(ctx);
    }
    
    @Override
    public Set<String> locateForBetween(ConditionContext ctx) {
        long lowerValue = getLongValue(ctx.getValue());
        long upperValue = getLongValue(ctx.getSecondValue());
        
        if(lowerValue > upperValue)
            throw new IllegalArgumentException(String.format("The lower argument %d is greater than upper argument %d in between", lowerValue, upperValue));
        
        // Cross all shards case
        if(upperValue - lowerValue >= (mod -1))
            return ctx.getAllShards();
        
        Set<String> shards = new HashSet<>();
        
        //For same value
        if(upperValue == lowerValue) {
            int shard = Integer.parseInt(mod(mod, ctx.getValue()));
            shards.add(String.valueOf(shard));
            return shards;
        }

        int lowerShard = Integer.parseInt(mod(mod, ctx.getValue()));
        int upperShard = Integer.parseInt(mod(mod, ctx.getSecondValue()));
        
        if(lowerShard < upperShard) {
            while(lowerShard <= upperShard)
                shards.add(String.valueOf(lowerShard++));
        } else {
            while(lowerShard < mod)
                shards.add(String.valueOf(lowerShard++));
            
            int shard = 0;
            while(shard <= upperShard)
                shards.add(String.valueOf(shard++));
        }

        return shards;
    }

    public static String mod(int mod, Object value) {
        Long id = getLongValue(value);
        return String.valueOf(id%mod);
    }

    public static Long getLongValue(Object value) {
        if(value == null)
            throw new IllegalArgumentException("The shard column must not be null");
        
        if(value instanceof Long)
            return (Long)value;
        
        if(value instanceof Number)
            return ((Number)value).longValue();
        
        if(value instanceof String)
            return new Long((String)value);
        
        throw new IllegalArgumentException(String.format("Shard value: %s can not be recoganized as int value", value.toString()));
    }
}
