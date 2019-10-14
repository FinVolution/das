package com.ppdai.das.core.datasource;

import java.sql.Connection;

import com.ppdai.das.core.helper.Ordered;

public interface ConnectionListener extends Ordered {
    void onCreateConnection(String poolDesc, Connection connection);

    void onReleaseConnection(String poolDesc, Connection connection);

    void onAbandonConnection(String poolDesc, Connection connection);

}
