package com.ppdai.das.strategy;

import java.util.Set;

public interface Condition {
    /**
     * Invoked when the condition is decorated by NOT.
     * Important Note: this may be applied multiple time. When this method get called twice, it should be recovered to the initial status
     * @return The reversed condition itself
     */
    public Condition reverse();
    
    /**
     * @return tables used in condition
     */
    public Set<String> getTables();
}
