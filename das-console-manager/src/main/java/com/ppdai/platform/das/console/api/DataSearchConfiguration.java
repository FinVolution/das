package com.ppdai.platform.das.console.api;

import com.ppdai.das.core.enums.DatabaseCategory;

/**
 * 数据库查询工具，待完成
 */
public interface DataSearchConfiguration {

    DatabaseCategory setUp(String appId, String dbName) throws Exception;

    void cleanUp(String appId) throws Exception;
}
