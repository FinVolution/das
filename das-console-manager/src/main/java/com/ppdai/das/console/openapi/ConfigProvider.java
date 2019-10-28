package com.ppdai.das.console.openapi;

import com.ppdai.das.console.openapi.vo.DatabaseSetVO;
import com.ppdai.das.console.openapi.vo.ProjectVO;
import com.ppdai.das.console.openapi.vo.DataBaseVO;
import com.ppdai.das.console.openapi.vo.DatabaseSetEntryVO;

import java.util.List;

/**
 * 接入配置中心需要实现的接口
 */
public interface ConfigProvider {

    /**
     * 配置中心的名称
     *
     * @return
     */
    String getConfigCenterName();

    /**
     * 批量添加物理库
     *
     * @param list
     * @return
     * @
     */
    void addDataBase(List<DataBaseVO> list) throws Exception;

    /**
     * 删除物理库
     *
     * @param dataBaseName
     * @return
     * @
     */
    void deleteDataBase(String dataBaseName) throws Exception;

    /**
     * 更新物理库信息
     *
     * @param dataBaseVO dbName不可修改
     * @return
     * @
     */
    void updateDataBase(DataBaseVO dataBaseVO) throws Exception;

    /**
     * 数据校验, 从配置中心配置的物理库数据
     *
     * @param dataBaseName
     * @return
     * @
     */
    DataBaseVO getDataBase(String dataBaseName) throws Exception;

    /**
     * 添加逻辑库
     *
     * @param databaseSetVO
     * @return
     * @
     */
    void addDataBaseSet(DatabaseSetVO databaseSetVO) throws Exception;

    /**
     * 删除逻辑库
     *
     * @param dataBasesetName
     * @return
     * @
     */
    void deleteDataBaseSet(String dataBasesetName) throws Exception;

    /**
     * 跟新逻辑库
     *
     * @param databaseSetVO
     * @return
     */
    void updateDatabaseSet(DatabaseSetVO databaseSetVO) throws Exception;

    /**
     * 数据校验, 从配置中心配置的逻辑库数据
     *
     * @param databaseSetName
     * @return
     */
    DatabaseSetVO getDatabaseSet(String databaseSetName) throws Exception;

    /**
     * 批量添加逻辑库映射
     *
     * @param list
     * @return
     * @
     */
    void addDatabaseSetEntries(List<DatabaseSetEntryVO> list) throws Exception;

    /**
     * 删除逻辑库映射
     *
     * @param dbsetEntryName
     * @return
     * @
     */
    void deleteDatabaseSetEntry(String dbsetEntryName) throws Exception;

    /**
     * 更新逻辑库映射
     *
     * @param databaseSetEntryVO
     * @return
     */
    void updateDatabaseSetEntry(DatabaseSetEntryVO databaseSetEntryVO) throws Exception;

    /**
     * 数据校验, 从配置中心配置的逻辑库映射数据
     *
     * @param databaseSetEntryName
     * @return
     */
    DatabaseSetEntryVO getDatabaseSetEntry(String databaseSetEntryName) throws Exception;

    /**
     * 添加项目配置
     *
     * @param projectVO
     * @throws Exception
     */
    void addProject(ProjectVO projectVO) throws Exception;

    /**
     * 更新项目配置
     * @throws Exception
     */
    void updateProject(ProjectVO projectVO) throws Exception;

    /**
     * 删除项目配置
     *
     * @param appId
     * @throws Exception
     */
    void deleteProject(String appId) throws Exception;

    /**
     * 获取项目配置信息
     *
     * @param appId
     * @return
     * @throws Exception
     */
    ProjectVO getProject(String appId) throws Exception;
}
