package com.ppdai.das.core.client;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A snowflake style ID generator
 * @author hejiehui
 *
 */
public class TransactionIdGenerator {
    private static AtomicLong lastMillisecond = new AtomicLong(0);
    private static AtomicInteger counter = new AtomicInteger(0);

    public TransactionIdGenerator() throws SQLException {
    }

    public TransactionId getNextId(String logicDbName, String physicalDbName, String shardId, String hostAddress, String workId) throws SQLException {
        int index;
        long last;
        synchronized (this.getClass()) {
            last = lastMillisecond.get();
            long curTime = System.currentTimeMillis();
            
            if(last != curTime) {
                lastMillisecond.set(curTime);
                last = curTime;
                counter.set(0);
            }
            
            index = counter.incrementAndGet();
        }

        return  new TransactionId(logicDbName, physicalDbName, shardId, hostAddress, workId, last, index);
    }
}