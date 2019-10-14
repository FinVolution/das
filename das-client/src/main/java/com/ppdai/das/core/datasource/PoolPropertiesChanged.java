package com.ppdai.das.core.datasource;

import com.ppdai.das.core.configure.PoolPropertiesConfigure;

public interface PoolPropertiesChanged {
    void onChanged(PoolPropertiesConfigure configure);
}
