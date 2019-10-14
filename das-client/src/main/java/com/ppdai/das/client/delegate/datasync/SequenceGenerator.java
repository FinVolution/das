package com.ppdai.das.client.delegate.datasync;

import com.google.common.collect.ConcurrentHashMultiset;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceGenerator {
    private static final ConcurrentHashMultiset<String> sequenceIds = ConcurrentHashMultiset.create();
    private static final AtomicLong globalSequenceId = new AtomicLong(0);

    public static long getGlobalSequenceId(){
        return globalSequenceId.getAndIncrement();
    }

    public static long getSequenceId(String logicDBName){
        return sequenceIds.add(logicDBName, 1);
    }

}
