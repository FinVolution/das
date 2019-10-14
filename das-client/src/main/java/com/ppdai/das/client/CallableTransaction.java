package com.ppdai.das.client;

import java.sql.SQLException;

public interface CallableTransaction<T> {
    T execute() throws SQLException;
}
