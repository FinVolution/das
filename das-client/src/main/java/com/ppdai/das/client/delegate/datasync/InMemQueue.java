package com.ppdai.das.client.delegate.datasync;

import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.*;

/**
 * For demo and testing.
 * Please use reliable queue system in production
 */
public class InMemQueue {

    private static final Logger logger = LoggerFactory.getLogger(InMemQueue.class.getName());

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

   // public static final BlockingQueue<DataSyncContext> queue = new LinkedBlockingQueue();

    private static final ConcurrentMap<String, BlockingQueue<DataSyncContext>> queues = new ConcurrentHashMap<>();

    private static final EventBus eventBus = new EventBus("contextQueue");

    public static void init(Set<String> logicDBs) {
        for(String logicDb : logicDBs) {
            queues.put(logicDb, new LinkedBlockingQueue());
        }
        listenQueue();
    }

    public static void send(DataSyncContext dataSyncContext) {
        BlockingQueue<DataSyncContext> queue = queues.get(dataSyncContext.getLogicDbName());
        if(queue == null) {
            throw new IllegalArgumentException("Cannot find queue to send DataSyncContext [" + dataSyncContext + "]");
        }
        queue.offer(dataSyncContext);
    }

    private static void listenQueue() {
        eventBus.register(DataSyncConfiguration.getInstance());

        queues.values().forEach(q ->
            executorService.submit(() -> {
                try {
                    while (true) {//Consume DataSyncContext from queue
                        DataSyncContext dataSyncContext = q.take();
                        eventBus.post(dataSyncContext);
                    }
                } catch (Exception ie) {
                    logger.error("Exception occurs when taking DataSyncContext from queue.", ie);
                }
            })
        );
    }
}
