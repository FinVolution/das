package com.ppdai.das.core;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ppdai.das.core.configure.DataSourceConfigureProvider;
import com.ppdai.das.core.configure.DefaultDataSourceConfigureProvider;
import com.ppdai.das.core.datasource.DataSourceLocator;
import com.ppdai.das.core.helper.ConnectionStringKeyHelper;

public class DefaultConnectionLocator implements ConnectionLocator {
    public static final String DATASOURCE_CONFIG_PROVIDER = "dataSourceConfigureProvider";

    private DataSourceLocator locator;
    private DataSourceConfigureProvider provider;

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        provider = new DefaultDataSourceConfigureProvider();
        if (settings.containsKey(DATASOURCE_CONFIG_PROVIDER)) {
            provider =
                    (DataSourceConfigureProvider) Class.forName(settings.get(DATASOURCE_CONFIG_PROVIDER)).newInstance();
        }

        provider.initialize(settings);

        locator = new DataSourceLocator(provider);
    }

    @Override
    public void setup(Set<String> dbNames) {
        provider.setup(dbNames);
    }

    @Override
    public Connection getConnection(String name) throws Exception {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        DataSource dataSource = locator.getDataSource(keyName);
        return dataSource.getConnection();
    }

    public DataSourceConfigureProvider getProvider() {
        return provider;
    }
}
