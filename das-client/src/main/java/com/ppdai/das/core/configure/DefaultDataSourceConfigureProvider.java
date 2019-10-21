package com.ppdai.das.core.configure;

import java.util.Map;
import java.util.Set;

import com.ppdai.das.core.DasConfigure;

public class DefaultDataSourceConfigureProvider implements DataSourceConfigureProvider {

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        DataSourceConfigureParser.getInstance();
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        DataSourceConfigure dataSourceConfigure =
                DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(dbName);
        if (dataSourceConfigure == null)
            return new DataSourceConfigure(dbName);

        return new DataSourceConfigure(dbName, dataSourceConfigure.getProperties());
    }

    @Override
    public void setup(Set<String> dbNames) {}

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {}

    @Override
    public void onConfigChanged(DasConfigure.DataSourceConfigureEvent event) {

    }

}
