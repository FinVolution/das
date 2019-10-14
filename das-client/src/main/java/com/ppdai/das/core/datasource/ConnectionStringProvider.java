package com.ppdai.das.core.datasource;

import java.util.Map;
import java.util.Set;

import com.ppdai.das.core.configure.ConnectionString;

public interface ConnectionStringProvider {
    Map<String, ConnectionString> getConnectionStrings(Set<String> names) throws Exception;

    void addConnectionStringChangedListener(final String name, final ConnectionStringChanged callback);

}
