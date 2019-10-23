package com.ppdai.platform.das.console.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceUtil {
    private static final String DBURL_MYSQL_CACHE1 = "jdbc:mysql://%s:%s?useUnicode=true&characterEncoding=utf8";
    private static final String DBURL_SQLSERVER_CACHE1 = "jdbc:sqlserver://%s:%s;sendTimeAsDateTime=false";

    private static final String DBURL_MYSQL_CACHE2 = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf8";
    private static final String DBURL_SQLSERVER_CACHE2 = "jdbc:sqlserver://%s:%s;DatabaseName=%s;sendTimeAsDateTime=false";

    private static final String DRIVER_MYSQL = "com.mysql.jdbc.Driver";
    private static final String DRIVER_SQLSERVRE = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private static final String DBURL_MYSQL_CACHE = "jdbc:mysql://%s:%s/%s";
    public static final String DBURL_SQLSERVER_CACHE = "jdbc:sqlserver://%s:%s;DatabaseName=%s";

    // dbAddress+port+User+password,DataSource
    private static volatile Map<String, DataSource> cache1 = new ConcurrentHashMap<>();

    // dbAddress+catalog+port+User+password,DataSource
    private static volatile Map<String, DataSource> cache2 = new ConcurrentHashMap<>();

    public static Connection getConnection(String address, String port, String userName, String password, String driverClass) throws Exception {
        validateParam(address, port, userName, password, driverClass);
        String key = address.trim() + port.trim() + userName.trim() + password.trim();
        DataSource ds = cache1.get(key);
        if (ds != null) {
            Connection connection = ds.getConnection();
            return connection;
        }
        synchronized (DataSourceUtil.class) {
            ds = cache1.get(key);
            if (ds != null) {
                Connection conn = ds.getConnection();
                return conn;
            } else {
                DataSource newDS = createDataSource(address.trim(), port.trim(), userName.trim(), password.trim(), driverClass.trim());
                cache1.put(key, newDS);
                Connection conn = newDS.getConnection();
                return conn;
            }
        }
    }

    private static DataSource createDataSource(String address, String port, String userName, String password,
                                               String driverClass) throws Exception {
        String url;
        if (DRIVER_MYSQL.equals(driverClass)) {
            url = String.format(DBURL_MYSQL_CACHE1, address, port);
        } else {
            url = String.format(DBURL_SQLSERVER_CACHE1, address, port);
        }
        return createDataSource(url, userName, password, driverClass);
    }

    private static DataSource createDataSource(String url, String userName, String password, String driverClass) throws Exception {
        PoolProperties p = new PoolProperties();
        //System.out.println(url);
        p.setUrl(url);
        p.setUsername(userName);
        p.setPassword(password);
        p.setDriverClassName(driverClass);
        p.setJmxEnabled(false);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setTestOnReturn(false);
        p.setValidationQuery("SELECT 1");
        p.setValidationQueryTimeout(5);
        p.setValidationInterval(30000L);
        p.setTimeBetweenEvictionRunsMillis(300000);
        p.setNumTestsPerEvictionRun(50);
        p.setMaxActive(100);
        p.setMinIdle(0);
        p.setMaxWait(10000);
        p.setMaxAge(0L);
        p.setInitialSize(1);
        p.setRemoveAbandonedTimeout(60);
        p.setRemoveAbandoned(true);
        p.setLogAbandoned(true);
        p.setMinEvictableIdleTimeMillis(3600000);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
        ds.createPool();
        return ds;
    }

    private static void validateParam(String address, String port, String userName, String password, String driverClass) throws Exception {
        address = address.trim();
        if (StringUtils.isBlank(address)) {
            throw new SQLException("The address is null.");
        }
        port = port.trim();
        if (StringUtils.isBlank(port)) {
            throw new SQLException("The port is null.");
        }

        userName = userName.trim();
        if (StringUtils.isBlank(userName)) {
            throw new SQLException("The userName is null.");
        }

        password = password.trim();
        if (StringUtils.isBlank(password)) {
            throw new SQLException("The password is null.");
        }

        driverClass = driverClass.trim();
        if (StringUtils.isBlank(driverClass)) {
            throw new SQLException("The driverClass is null.");
        }
    }

    private static void validateParam(Integer alldbs_id, String address, String port, String catalog, String userName, String password, String driverClass) throws Exception {
        if (alldbs_id == null) {
            throw new SQLException("the allInOneName is null.");
        }
        catalog = catalog.trim();
        if (StringUtils.isBlank(catalog)) {
            throw new SQLException("the catalog is null.");
        }
        validateParam(address, port, userName, password, driverClass);
    }

}
