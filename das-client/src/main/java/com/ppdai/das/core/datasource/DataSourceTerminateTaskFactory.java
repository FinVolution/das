package com.ppdai.das.core.datasource;

import com.ppdai.das.core.helper.Ordered;

public interface DataSourceTerminateTaskFactory extends Ordered {
    DataSourceTerminateTask createTask(SingleDataSource oldDataSource);
}
