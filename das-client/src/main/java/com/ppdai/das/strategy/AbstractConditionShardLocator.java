package com.ppdai.das.strategy;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractConditionShardLocator<CTX extends ConditionContext> {

    public abstract Set<String> locateForEqual(CTX context);

    public abstract Set<String> locateForNotEqual(CTX ctx);

    public abstract Set<String> locateForGreaterThan(CTX context);

    public abstract Set<String> locateForGreaterThanOrEqual(CTX ctx);
        
    public abstract Set<String> locateForLessThan(CTX context);

    public abstract Set<String> locateForLessThanOrEqual(CTX ctx);
    
    public abstract Set<String> locateForBetween(CTX ctx);

    public abstract Set<String> locateForNotBetween(CTX ctx);
    
    public abstract Set<String> locateForIn(CTX context);
    
    public abstract Set<String> locateForNotIn(CTX context);
    
    public abstract Set<String> locateForLike(CTX context);

    public abstract Set<String> locateForNotLike(CTX context);

    public abstract Set<String> locateForIsNull(CTX context);

    public abstract Set<String> locateForIsNotNull(CTX context);

    public Set<String> getAllShards(CTX context) {
        return context.getAllShards();
    }
    
    @SuppressWarnings("unchecked")
    protected CTX createConditionContext(CTX conditionContext, OperatorEnum newOperator, Object newValue) {
        return (CTX)conditionContext.create(newOperator, newValue);
    }

    protected Set<String> locateForCombination(CTX context, OperatorEnum op1, Object value1, OperatorEnum op2, Object value2) {
        // Wrap into a new Set in case what return is read-only.
        Set<String> range = new HashSet<>(locateShards(createConditionContext(context, op1, value1)));
        
        if(isAlreadyAllShards(getAllShards(context), range))
            return range;
        
        range.addAll(locateShards(createConditionContext(context, op2, value2)));
        return range;
    }

    protected Set<String> locateForIntersection(CTX context, OperatorEnum op1, Object value1, OperatorEnum op2, Object value2) {
        // Wrap into a new Set in case what return is read-only.
        Set<String> range = new HashSet<>(locateShards(createConditionContext(context, op1, value1)));
        
        range.retainAll(locateShards(createConditionContext(context, op2, value2)));
        return range;
    }
    
    protected Set<String> exclude(CTX context, Set<String> shards) {
        Set<String> allShards = new HashSet<>(getAllShards(context));
        allShards.removeAll(shards);
        return allShards;
    }

    protected boolean isAlreadyAllShards(Set<String> allShards, Set<String> shards) {
        if(shards == null || shards.isEmpty())
            return false;
        
        if(!allShards.containsAll(shards)) {
            shards.removeAll(allShards);
            throw new IllegalStateException("Illegal shard detected:" + shards);
        }

        return allShards.equals(shards);
    }

    /**
     * Helper method that wrap a string into set
     * @param id
     * @return
     */
    protected Set<String> toSet(String id) {
        Set<String> shards = new HashSet<>();
        shards.add(id);
        return shards;
    }
    
    public Set<String> locateShards(CTX context) {
        switch (context.getOperator()) {
        case EQUAL:
            return locateForEqual(context);
        case NOT_EQUAL:
            return locateForNotEqual(context);
        case GREATER_THAN:
            return locateForGreaterThan(context);
        case GREATER_THAN_OR_EQUAL:
            return locateForGreaterThanOrEqual(context);
        case LESS_THAN:
            return locateForLessThan(context);
        case LESS_THAN_OR_EQUAL:
            return locateForLessThanOrEqual(context);
        case BEWTEEN:
            return locateForBetween(context);
        case NOT_BETWEEN:
            return locateForNotBetween(context);
        case IN:
            return locateForIn(context);
        case NOT_IN:
            return locateForNotIn(context);
        case LIKE:
            return locateForLike(context);
        case NOT_LIKE:
            return locateForNotLike(context);
        case IS_NULL:
            return locateForIsNull(context);
        case IS_NOT_NULL:
            return locateForIsNotNull(context);
        default:
            throw new IllegalArgumentException("Invalid operator detected: " + context.getOperator());
        }
    }
}
