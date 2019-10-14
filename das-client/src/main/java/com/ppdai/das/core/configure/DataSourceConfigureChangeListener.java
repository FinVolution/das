package com.ppdai.das.core.configure;

import java.sql.SQLException;

public interface DataSourceConfigureChangeListener {
    void configChanged(DataSourceConfigureChangeEvent event) throws SQLException;
}
