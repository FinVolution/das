package com.ppdai.platform.das.console.api;

/**
 * 配置前缀，如果不需要可以全部返回空字符串
 */
public interface AdditionalConfiguration {

    /**
     * Das Team 前缀
     *
     * @return
     */
    String getGlobalDasTeams();

    /**
     * MySql 配置前缀
     *
     * @return
     */
    String getGlobalMysqlDatasource();

    /**
     * SqlServer 配置前缀
     *
     * @return
     */
    String getGlobalSqlserverDatasource();

    /**
     * 应用组配置前缀
     *
     * @return
     */
    String getDasApplicationGroups();

    /**
     * 服务器组配置前缀
     *
     * @return
     */
    String getDasServerGroups();

    /**
     * 服务器查找表前缀
     *
     * @return
     */
    String getDasSeverLookupTable();

    /**
     * 分库分表策略前缀
     *
     * @return
     */
    String getDasShardingStrategies();
}
