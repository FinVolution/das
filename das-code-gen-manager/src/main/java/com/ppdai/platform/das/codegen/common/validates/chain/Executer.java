package com.ppdai.platform.das.codegen.common.validates.chain;

import java.sql.SQLException;

public interface Executer<T> {
    T execute() throws SQLException;
}
