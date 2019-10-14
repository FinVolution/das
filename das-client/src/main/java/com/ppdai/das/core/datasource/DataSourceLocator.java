package com.ppdai.das.core.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.das.core.configure.DataSourceConfigure;
import com.ppdai.das.core.configure.DataSourceConfigureProvider;
import com.ppdai.das.core.configure.DefaultDataSourceConfigureProvider;

public class DataSourceLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceLocator.class);

    private static final ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<>();

    private static final AtomicReference<DataSourceConfigureProvider> providerRef = new AtomicReference<>();

    public DataSourceLocator(DataSourceConfigureProvider provider) {
        providerRef.set(provider);
    }

    // to be refactored
    public static boolean containsKey(String name) {
        return cache.containsKey(name);
    }

    /**
     * This is used for initialize datasource for thirdparty framework
     */
    public DataSourceLocator() {
        this(new DefaultDataSourceConfigureProvider());
    }

    /**
     * Get DataSource by real db source name
     * 
     * @param name
     * @return DataSource
     * @throws NamingException
     */
    public DataSource getDataSource(String name) throws Exception {
        DataSource ds = cache.get(name);

        if (ds != null) {
            return ds;
        }

        synchronized (this.getClass()) {
            ds = cache.get(name);
            if (ds != null) {
                return ds;
            }
            try {
                ds = createDataSource(name);
                cache.put(name, ds);
            } catch (Throwable e) {
                String msg = "Creating DataSource " + name + " error:" + e.getMessage();
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

        return ds;
    }
    
    private DataSource createDataSource(String name) throws SQLException {
        RefreshableDataSource rds = new RefreshableDataSource(name, getDataSourceConfigure(name));
        providerRef.get().register(name, rds);
        return rds;
    }

    public static Map<String, Integer> getActiveConnectionNumber() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, DataSource> entry : cache.entrySet()) {
            DataSource dataSource = entry.getValue();
            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                map.put(entry.getKey(), ds.getActive());
            }
        }

        return map;
    }

    public static DataSourceConfigure getDataSourceConfigure(String name) throws SQLException {
        DataSourceConfigure config = providerRef.get().getDataSourceConfigure(name);
        if (config == null)
            throw new SQLException("Can not find connection configure for " + name);
        return config;
    }
}
