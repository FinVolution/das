package com.ppdai.das.strategy;

import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractCommonShardLocator<CTX extends ConditionContext> extends AbstractConditionShardLocator<CTX> {
    @Override
    public abstract Set<String> locateForEqual(CTX context);

    @Override
    public abstract Set<String> locateForGreaterThan(CTX context);

    @Override
    public abstract Set<String> locateForLessThan(CTX context);

    @Override
    public abstract Set<String> locateForBetween(CTX ctx);

    @Override
    public Set<String> locateForLike(CTX ctx) {
        return getAllShards(ctx);
    }

    @Override
    public Set<String> locateForNotLike(CTX ctx) {
        return getAllShards(ctx);
    }

    @Override
    public Set<String> locateForIsNull(CTX ctx) {
        return getAllShards(ctx);
    }

    @Override
    public Set<String> locateForIsNotNull(CTX ctx) {
        return getAllShards(ctx);
    }

    @Override
    public Set<String> locateForNotEqual(CTX ctx) {
        return locateForCombination(ctx, OperatorEnum.LESS_THAN, ctx.getValue(), OperatorEnum.GREATER_THAN, ctx.getValue());
    }

    @Override
    public Set<String> locateForGreaterThanOrEqual(CTX ctx) {
        return locateForCombination(ctx, OperatorEnum.EQUAL, ctx.getValue(), OperatorEnum.GREATER_THAN, ctx.getValue());
    }

    @Override
    public Set<String> locateForLessThanOrEqual(CTX ctx) {
        return locateForCombination(ctx, OperatorEnum.EQUAL, ctx.getValue(), OperatorEnum.LESS_THAN, ctx.getValue());
    }

    @Override
    public Set<String> locateForNotBetween(CTX ctx) {
        return locateForCombination(ctx, OperatorEnum.LESS_THAN, ctx.getValue(), OperatorEnum.GREATER_THAN, ctx.getSecondValue());
    }

    @Override
    public Set<String> locateForIn(CTX context) {
        Set<String> allShards = getAllShards(context);
        Set<String> range = new TreeSet<>();
        for(Object value: context.getValues()) {
            range.addAll(locateShards(createConditionContext(context, OperatorEnum.EQUAL, value)));
            if(isAlreadyAllShards(allShards, range))
                break;
        }
        
        return range;
    }

    @Override
    public Set<String> locateForNotIn(CTX context) {
        Set<String> range = new TreeSet<>(getAllShards(context));
        for(Object value: context.getValues()) {
            range.retainAll(locateShards(createConditionContext(context, OperatorEnum.NOT_EQUAL, value)));
        }
        
        return range;
    }
}
