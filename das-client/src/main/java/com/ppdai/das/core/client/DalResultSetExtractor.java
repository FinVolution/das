package com.ppdai.das.core.client;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DalResultSetExtractor<T> {
	T extract(ResultSet rs) throws SQLException;
}
