package com.ppdai.das.core.client;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.ppdai.das.core.TransactionIdGenerator;

public class TransactionIdGeneratorTest {

    @Test
    public void testConcurrent() throws SQLException, InterruptedException {
        final ConcurrentHashMap<String, Object> ids = new ConcurrentHashMap<>();
        final AtomicBoolean fail = new AtomicBoolean(false);
        final Object v = new Object();
        for(int i = 0; i<1000; i++) {
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    TransactionIdGenerator g;
                    try {
                        Thread.sleep(100);
                        g = new TransactionIdGenerator();
                        for(int i = 0; i<10000; i++) {
                            String id = g.getNextId("test", "ph-1", "0", "worker", "worker").getUniqueId();
                            if(ids.putIfAbsent(id, v) != null) {
                                fail.set(true);
                                System.out.println(id);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        fail();
                    }
                    
                }
            }).start();
        }
        
        Thread.sleep(10* 1000);
        System.out.println(ids.size());
        if(fail.get())
            fail();
    }
    
    @Test
    public void testSingle() throws SQLException, InterruptedException {
        final ConcurrentHashMap<String, Object> ids = new ConcurrentHashMap<>();
        final Object v = new Object();
        TransactionIdGenerator g = new TransactionIdGenerator();
        
        for(int i = 0; i<1000; i++) {
            String id = g.getNextId("test", "ph-1", "0", "worker", "worker").getUniqueId();
//            System.out.println(id);
            if(ids.putIfAbsent(id, v) != null) {
                fail();
            }
        }        
    }
}
