package com.ppdai.platform.das.console.api;

import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;

import java.util.List;

/**
 * 物理库管理页
 */
public interface DataBaseConfiguration {

    /**
     * 批量添加物理库
     *
     * @param user 当前操作人信息
     * @param list
     * @return
     * @
     */
    void batchAddDataBase(LoginUser user, List<DataBaseInfo> list) throws Exception;

    /**
     * 更新物理库信息
     *
     * @param user         当前操作人信息
     * @param dataBaseInfo
     * @return
     * @
     */
    void updateDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception;

    /**
     * 删除物理库
     *
     * @param user         当前操作人信息
     * @param dataBaseInfo
     * @return
     * @
     */
    void deleteDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception;

    /**
     * 同步物理库信息到配置中心
     *
     * @param user         当前操作人信息
     * @param dataBaseInfo
     * @return
     * @
     */
    void syncDataBase(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception;

    /**
     * 数据校验, 两组数据或者三组对比，如果传多组，默认最多取前三组对比
     *
     * @param user         当前操作人信息
     * @param dataBaseInfo
     * @return
     * @
     */
    List<ConfigDataResponse> getCheckData(LoginUser user, DataBaseInfo dataBaseInfo) throws Exception;

    /**
     * 设置物理库名称长度最大值
     *
     * @return
     */
    int getDataBaseNameMaxLength();

}
