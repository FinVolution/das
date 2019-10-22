package com.ppdai.platform.das.console.api.impl;

import com.ppdai.platform.das.console.api.AdditionalConfiguration;
import org.apache.commons.lang.StringUtils;

public class AdditionalManager implements AdditionalConfiguration {

    @Override
    public String getGlobalDasTeams() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getGlobalMysqlDatasource() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getGlobalSqlserverDatasource() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDasApplicationGroups() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDasServerGroups() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDasSeverLookupTable() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getDasShardingStrategies() {
        return StringUtils.EMPTY;
    }

}
