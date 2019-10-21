package com.ppdai.platform.das.codegen.api;

import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.DasGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;

import java.util.List;

/**
 * 逻辑库管理页
 */
public interface DbSetConfiguration {

    /**
     * 添加逻辑库
     *
     * @param user 当前操作人信息
     * @param dbset
     * @return
     * @
     */
    void addDbSet(LoginUser user, DatabaseSet dbset) throws Exception;

    /**
     * 添加逻辑库映射LISt
     *
     * @param user 当前操作人信息
     * @param dbsetEntryList
     * @return
     * @
     */
    void addDbSetEntryList(LoginUser user, List<DatabaseSetEntry> dbsetEntryList) throws Exception;

    /**
     * 跟新逻辑库
     *
     * @param user 当前操作人信息
     * @param oldDbset
     * @param newDbset
     * @return
     */
    void updateDbSet(LoginUser user, DatabaseSet oldDbset, DatabaseSet newDbset) throws Exception;

    /**
     * 跟新逻辑库映射
     *
     * @param user 当前操作人信息
     * @param odlDbSetEntry
     * @param newDbSetEntry
     * @return
     */
    void updateDbSetEntry(LoginUser user, DatabaseSetEntry odlDbSetEntry, DatabaseSetEntry newDbSetEntry) throws Exception;

    /**
     * 删除逻辑库
     *
     * @param user 当前操作人信息
     * @param dbset
     * @return
     * @
     */
    void deleteDbSet(LoginUser user, DatabaseSet dbset) throws Exception;

    /**
     * 删除逻辑库映射
     *
     * @param user 当前操作人信息
     * @param dbsetEntry
     * @return
     * @
     */
    void deleteDbSetEntry(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception;

    /**
     * 同步逻辑库数据
     *
     * @param user 当前操作人信息
     * @param dbset
     * @return
     * @
     */
    void syncDbSet(LoginUser user, DatabaseSet dbset) throws Exception;

    /**
     * 同步逻辑库数据映射
     *
     * @param user 当前操作人信息
     * @param dbsetEntry
     * @return
     * @
     */
    void syncDbsetEntry(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception;

    /**
     * 数据校验, 两组数据或者三组对比，如果传多组，默认最多取前三组对比
     *
     * @param user 当前操作人信息
     * @param dbset
     * @return
     */
    List<ConfigDataResponse> getCheckData(LoginUser user, DatabaseSet dbset) throws Exception;

    /**
     * 数据校验, 两组数据或者三组对比，如果传多组，默认最多取前三组对比
     *
     * @param user 当前操作人信息
     * @param dbsetEntry
     * @return
     */
    List<ConfigDataResponse> getCheckData(LoginUser user, DatabaseSetEntry dbsetEntry) throws Exception;

    /**
     * 数据校验, 两组数据或者三组对比，如果传多组，默认最多取前三组对比
     *
     * @param user 当前操作人信息
     * @param dbset
     * @return
     */
    List<ConfigDataResponse> getAllCheckData(LoginUser user, DasGroup dasGroup, DatabaseSet dbset) throws Exception;

}
