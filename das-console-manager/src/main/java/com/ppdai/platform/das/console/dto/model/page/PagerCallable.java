package com.ppdai.platform.das.console.dto.model.page;

import java.sql.SQLException;
import java.util.List;

public interface PagerCallable<T> {
    List<T> call() throws SQLException;
}
