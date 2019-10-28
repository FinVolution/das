package com.ppdai.das.console.api.impl;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.utils.Transform;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.configCheck.ItemResponse;
import com.ppdai.das.console.dto.entry.configCheck.TitleResponse;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.enums.StrategyTypeEnum;
import com.ppdai.das.console.openapi.ConfigProvider;
import com.ppdai.das.console.openapi.vo.DatabaseSetEntryVO;
import com.ppdai.das.console.openapi.vo.DatabaseSetVO;
import com.ppdai.das.console.api.DbSetConfiguration;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DbSetManager implements DbSetConfiguration {

    @Autowired
    private Transform transform;

    @Autowired
    private ConfigProvider configProvider;

    @Override
    public void addDbSet(LoginUser user, DatabaseSet dbset) throws Exception {
        configProvider.addDataBaseSet(transform.toDatabaseSetVO(dbset));
    }

    @Override
    public void updateDbSet(LoginUser user, DatabaseSet oldDbset, DatabaseSet newDbset) throws Exception {
        if (!oldDbset.getName().equals(newDbset.getName())) {
            configProvider.deleteDataBaseSet(oldDbset.getName());
            configProvider.addDataBaseSet(transform.toDatabaseSetVO(newDbset));
        } else {
            configProvider.updateDatabaseSet(transform.toDatabaseSetVO(newDbset));
        }
    }

    @Override
    public void deleteDbSet(LoginUser user, DatabaseSet dbset) throws Exception {
        configProvider.deleteDataBaseSet(dbset.getName());
    }

    @Override
    public void syncDbSet(LoginUser user, DatabaseSet dbset) throws Exception {
        configProvider.updateDatabaseSet(transform.toDatabaseSetVO(dbset));
    }

    @Override
    public void addDbSetEntryList(LoginUser user, List<DatabaseSetEntry> dbsetEntryList) throws Exception {
        configProvider.addDatabaseSetEntries(transform.toDatabaseSetEntryVOList(dbsetEntryList));
    }

    @Override
    public void updateDbSetEntry(LoginUser user, DatabaseSetEntry odlDbSetEntry, DatabaseSetEntry newDbSetEntry) throws Exception {
        if (!odlDbSetEntry.getName().equals(newDbSetEntry.getName())) {
            configProvider.deleteDatabaseSetEntry(odlDbSetEntry.getName());
            configProvider.addDatabaseSetEntries(Lists.newArrayList(transform.toDatabaseSetEntryVO(newDbSetEntry)));
        } else {
            configProvider.updateDatabaseSetEntry(transform.toDatabaseSetEntryVO(newDbSetEntry));
        }
    }

    @Override
    public void deleteDbSetEntry(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception {
        configProvider.deleteDatabaseSetEntry(dbsetEntry.getName());
    }

    @Override
    public void syncDbsetEntry(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception {
        configProvider.updateDatabaseSetEntry(transform.toDatabaseSetEntryVO(dbsetEntry));
    }

    @Override
    public List<ConfigDataResponse> getCheckData(LoginUser user, DatabaseSet dbset) throws Exception {
        DatabaseSetVO dasdatabaseSetVO = transform.toDatabaseSetVO(dbset);
        DatabaseSetVO condatabaseSetVO = configProvider.getDatabaseSet(dbset.getName());
        if (null == condatabaseSetVO || StringUtils.isBlank(condatabaseSetVO.getDatabaseSetName())) {
            throw new Exception("数据错误，逻辑库信息为空！！！");
        }
        if (dasdatabaseSetVO.getShardingStrategy().size() != condatabaseSetVO.getShardingStrategy().size()) {
            throw new Exception("数据错误，逻辑库策略信息与DAS不匹配！！！");
        }
        List<TitleResponse> dastitles = Lists.newArrayList(new TitleResponse("DataBaseSet Name", dasdatabaseSetVO.getDatabaseSetName()));
        List<TitleResponse> contitles = Lists.newArrayList(new TitleResponse("DataBaseSet Name", condatabaseSetVO.getDatabaseSetName()));
        ConfigDataResponse das = new ConfigDataResponse("DAS", dastitles, toList(dbset));
        ConfigDataResponse con = new ConfigDataResponse(configProvider.getConfigCenterName(), contitles, toList(condatabaseSetVO));
        return Lists.newArrayList(das, con);
    }

    @Override
    public List<ConfigDataResponse> getCheckData(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception {
        DatabaseSetEntryVO dasdatabaseSetEntryVO = transform.toDatabaseSetEntryVO(dbsetEntry);
        DatabaseSetEntryVO condatabaseSetEntryVO = configProvider.getDatabaseSetEntry(dbsetEntry.getName());
        if (null == condatabaseSetEntryVO || StringUtils.isBlank(condatabaseSetEntryVO.getDatabaseSetName()) || StringUtils.isBlank(condatabaseSetEntryVO.getDatabasesetEntryName())) {
            throw new Exception("数据错误，逻辑库映射信息为空！！！");
        }
        List<TitleResponse> dastitles = Lists.newArrayList(new TitleResponse("DatabaseSetEntry Name", dasdatabaseSetEntryVO.getDatabasesetEntryName()));
        List<TitleResponse> contitles = Lists.newArrayList(new TitleResponse("DatabaseSetEntry Name", condatabaseSetEntryVO.getDatabasesetEntryName()));
        ConfigDataResponse das = new ConfigDataResponse("DAS", dastitles, toList(dbsetEntry));
        ConfigDataResponse con = new ConfigDataResponse(configProvider.getConfigCenterName(), contitles, toList(condatabaseSetEntryVO));
        return Lists.newArrayList(das, con);
    }

    @Override
    public List<ConfigDataResponse> getAllCheckData(LoginUser user, DasGroup dasGroup, DatabaseSet dbset) {
        return ListUtils.EMPTY_LIST;
    }

    private List<ItemResponse> toList(DatabaseSet dbset) {
        return toList(transform.toDatabaseSetVO(dbset));
    }

    private List<ItemResponse> toList(DatabaseSetVO dbset) {
        List<ItemResponse> list = Lists.newArrayList(
                new ItemResponse("databaseSetName", dbset.getDatabaseSetName()),
                new ItemResponse("strategyType", dbset.getStrategyType().getDetail()),
                new ItemResponse("dataBaseType", dbset.getDataBaseType().getName())
        );
        if (MapUtils.isNotEmpty(dbset.getShardingStrategy())) {
            for (Map.Entry<String, String> entry : dbset.getShardingStrategy().entrySet()) {
                list.add(new ItemResponse(entry.getKey(), entry.getValue()));
            }
        }
        if (dbset.getStrategyType() != StrategyTypeEnum.NoStrategy) {
            list.add(new ItemResponse("StrategyClassName", dbset.getStrategyClassName()));
        }
        return list;
    }

    private List<ItemResponse> toList(DatabaseSetEntry dbsetEntry) throws SQLException {
        return toList(transform.toDatabaseSetEntryVO(dbsetEntry));
    }

    private List<ItemResponse> toList(DatabaseSetEntryVO databaseSetEntryVO) {
        List<ItemResponse> list = Lists.newArrayList(
                new ItemResponse("databaseName", databaseSetEntryVO.getDatabaseName()),
                new ItemResponse("databasesetEntryName", databaseSetEntryVO.getDatabasesetEntryName()),
                new ItemResponse("databaseMasterSlaveType", databaseSetEntryVO.getDatabaseMasterSlaveType().getName()),
                new ItemResponse("sharding", databaseSetEntryVO.getSharding())
        );
        return list;
    }
}
