package com.ppdai.das.client;

import java.sql.SQLException;

public interface Transaction {
    void execute() throws SQLException;
}
