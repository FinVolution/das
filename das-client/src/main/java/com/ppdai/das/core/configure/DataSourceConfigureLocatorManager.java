package com.ppdai.das.core.configure;

import com.ppdai.das.core.DataSourceConfigureLocator;
import com.ppdai.das.core.DefaultDataSourceConfigureLocator;
import com.ppdai.das.core.helper.ServiceLoaderHelper;

public class DataSourceConfigureLocatorManager {
    private volatile static DataSourceConfigureLocator locator = ServiceLoaderHelper.getInstance(DataSourceConfigureLocator.class, new DefaultDataSourceConfigureLocator());

    public static DataSourceConfigureLocator getInstance() {
        return locator;
    }

}
