package com.ppdai.platform.das.console.dto.model.page;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public final class PagerUtil {

    public static <T> ListResult<T> find(long count, int offset, int limit, PagerCallable<T> callable) throws SQLException {
        List<T> items = Collections.emptyList();
        if (offset <= count && limit > 0) {
            items = callable.call();
        }
        return new ListResult<>(items, count, offset, limit);
    }

    private PagerUtil() {
    }
}
