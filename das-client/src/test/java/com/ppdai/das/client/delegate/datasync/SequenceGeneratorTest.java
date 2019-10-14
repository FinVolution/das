package com.ppdai.das.client.delegate.datasync;

import org.junit.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.assertEquals;

public class SequenceGeneratorTest {

    @Test
    public void testGetSequenceId() throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(5);
        ConcurrentHashMap<Long, String> set = new ConcurrentHashMap<>();
        for (AtomicInteger i = new AtomicInteger(0); i.get() < 1000000; i.incrementAndGet()) {
            es.submit(() -> {
                long s1 = SequenceGenerator.getSequenceId("A");
                set.put(s1, "");
            });
        }
        TimeUnit.SECONDS.sleep(5);
        assertEquals(1000000, set.size());
    }

}
