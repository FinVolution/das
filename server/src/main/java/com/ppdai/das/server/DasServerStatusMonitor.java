package com.ppdai.das.server;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.ppdai.das.core.TransactionServer;
import com.ppdai.das.service.DasRequest;
import com.ppdai.das.service.DasServerStatus;

public class DasServerStatusMonitor {
    private TransactionServer transServer;
    
    private final long serverStartTime;
    private AtomicLong totalCount = new AtomicLong();
    private AtomicLong totalCost = new AtomicLong();
    private AtomicLong failures = new AtomicLong();
    private final AtomicInteger clientConnection = new AtomicInteger(0);

    public DasServerStatusMonitor(TransactionServer transServer) {
        this.serverStartTime = System.currentTimeMillis();
        this.transServer = transServer;
    }

    public DasServerStatus getStatues() {
        DasServerStatus status = new DasServerStatus();
        status.online = true;
        status.avgResponse = totalCount.longValue() == 0 ? 0 : totalCost.longValue() / totalCount.longValue();
        status.avgThroughput = totalCount.longValue()/((System.currentTimeMillis() - serverStartTime)/1000);
        status.transactionCount = transServer.getCurrentCount();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double load = osBean.getSystemLoadAverage();
        status.cpuRate = (int) Math.round(load);
        status.memRate = Math.round(100 - ((float)Runtime.getRuntime().freeMemory() / (float)Runtime.getRuntime().totalMemory()) * 100 );
        status.clientCount = clientConnection.get();

        //TODO
        //status.healthyPoint = ?;
        return status;
    }
    
    public void receive(DasRequest request) {
        clientConnection.incrementAndGet();
        request.receiveTime = System.currentTimeMillis();
    }
    
    public void complete(DasRequest request, Throwable e) {
        totalCount.incrementAndGet();
        totalCost.addAndGet(System.currentTimeMillis() - request.receiveTime);
        clientConnection.decrementAndGet();

        if(e!= null)
            failures.incrementAndGet();
    }
}
