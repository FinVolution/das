package com.ppdai.das.console.openapi.impl;

import com.ppdai.das.console.openapi.vo.DatabaseSetVO;
import com.ppdai.das.console.openapi.vo.ProjectVO;
import com.ppdai.das.console.openapi.ConfigProvider;
import com.ppdai.das.console.openapi.vo.DataBaseVO;
import com.ppdai.das.console.openapi.vo.DatabaseSetEntryVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * ConfigProvider 的默认实现
 */
@Slf4j
public class ConfigProviderDefaultImpl implements ConfigProvider {

    @Override
    public String getConfigCenterName() {
        return "配置中心";
    }

    @Override
    public void addDataBase(List<DataBaseVO> list) throws Exception {
    }

    @Override
    public void deleteDataBase(String dbName) throws Exception {
    }

    @Override
    public void updateDataBase(DataBaseVO dataBaseVO) throws Exception {
    }

    @Override
    public DataBaseVO getDataBase(String dbName) throws Exception {
        return new DataBaseVO();
    }

    @Override
    public void addDataBaseSet(DatabaseSetVO dbset) throws Exception {
    }

    @Override
    public void deleteDataBaseSet(String dbsetName) throws Exception {
    }

    @Override
    public void updateDatabaseSet(DatabaseSetVO newDbset) throws Exception {
    }

    @Override
    public DatabaseSetVO getDatabaseSet(String dbsetName) throws Exception {
        return new DatabaseSetVO();
    }

    @Override
    public void addDatabaseSetEntries(List<DatabaseSetEntryVO> list) throws Exception {
    }

    @Override
    public void deleteDatabaseSetEntry(String dbsetEntryName) throws Exception {
    }

    @Override
    public void updateDatabaseSetEntry(DatabaseSetEntryVO newDbSetEntry) throws Exception {
    }

    @Override
    public DatabaseSetEntryVO getDatabaseSetEntry(String dbsetEntryName) throws Exception {
        return new DatabaseSetEntryVO();
    }

    @Override
    public void addProject(ProjectVO projectVO) throws Exception {
        System.out.println(projectVO);
    }

    @Override
    public void updateProject(ProjectVO projectVO) throws Exception {
        System.out.println(projectVO);
    }

    @Override
    public void deleteProject(String appId) throws Exception {
        System.out.println(appId);
    }

    @Override
    public ProjectVO getProject(String appId) throws Exception {
        System.out.println(appId);
        return new ProjectVO();
    }
}
