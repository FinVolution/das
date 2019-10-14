package com.ppdai.das.client.sqlbuilder;

public interface Includable<T> {
    T when(boolean condition);
    
    boolean isIncluded();
}
