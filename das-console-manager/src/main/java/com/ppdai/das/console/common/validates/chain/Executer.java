package com.ppdai.das.console.common.validates.chain;

import java.sql.SQLException;

public interface Executer<T> {
    T execute() throws SQLException;
}
