package com.ppdai.das.core.datasource;

import com.ppdai.das.core.configure.ConnectionString;

public interface ConnectionStringChanged {
    void onChanged(ConnectionString connectionString);
}
