package com.ppdai.das.core.datasource;

import java.util.concurrent.ScheduledExecutorService;

public interface DataSourceTerminateTask extends Runnable {
    void setScheduledExecutorService(ScheduledExecutorService service);
}
