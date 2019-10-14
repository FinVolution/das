package com.ppdai.das.core.datasource;

import com.ppdai.das.core.configure.PoolPropertiesConfigure;

public interface PoolPropertiesProvider {
    PoolPropertiesConfigure getPoolProperties();

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
