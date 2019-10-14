package com.ppdai.das.core.configure;

import java.util.Map;
import java.util.Set;

import com.ppdai.das.core.enums.IPDomainStatus;
import com.ppdai.das.core.helper.Ordered;

public interface DataSourceConfigureLocator extends Ordered {
    void addUserPoolPropertiesConfigure(String name, PoolPropertiesConfigure configure);

    PoolPropertiesConfigure getUserPoolPropertiesConfigure(String name);

    DataSourceConfigure getDataSourceConfigure(String name);

    void addDataSourceConfigureKeySet(Set<String> names);

    Set<String> getDataSourceConfigureKeySet();

    void setIPDomainStatus(IPDomainStatus status);

    IPDomainStatus getIPDomainStatus();

    void setConnectionStrings(Map<String, ConnectionString> map);

    void setPoolProperties(PoolPropertiesConfigure configure);

    DataSourceConfigure mergeDataSourceConfigure(ConnectionString connectionString);

}
