package com.ppdai.das.core.helper;

import java.sql.SQLException;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.client.DalResultSetExtractor;

public interface HintsAwareExtractor<T> {
    DalResultSetExtractor<T> extractWith(Hints hints) throws SQLException;
}
