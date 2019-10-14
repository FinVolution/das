package com.ppdai.das.core.configure;

import com.ppdai.das.core.helper.ServiceLoaderHelper;

public class DataSourceConfigureLocatorManager {
    private volatile static DataSourceConfigureLocator locator = ServiceLoaderHelper.getInstance(DataSourceConfigureLocator.class);

    public static DataSourceConfigureLocator getInstance() {
        return locator;
    }

}
