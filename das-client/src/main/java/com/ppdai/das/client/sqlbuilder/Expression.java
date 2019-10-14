package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

/**
 * Expression reprents a sql segment that can be evaluated. 
 * E.g. the condition used in where or on section
 * 
 * @author hejiehui
 *
 */
public abstract class Expression implements Segment, Includable<Expression> {
    private boolean included = true;
    
    /**
     * Mark this expression as as optional when condition is not meet.
     */
    public Expression when(boolean condition) {
        included = condition;
        return this;
    }
    
    public boolean isIncluded() {
        return included;
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }
}
