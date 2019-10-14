package com.ppdai.das.core.datasource;

public abstract class AbstractDataSourceTerminateTask implements DataSourceTerminateTask {
    abstract void log(String dataSourceName, boolean isForceClosing, long startTimeMilliseconds);
}
