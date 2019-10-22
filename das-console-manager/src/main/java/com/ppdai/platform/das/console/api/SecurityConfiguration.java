package com.ppdai.platform.das.console.api;

import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;

/**
 * 物理库信息接入安全服务
 */
public interface SecurityConfiguration {

    /**
     * 查询配置信息 对象转换成JSON字符串输出
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     */
    Object getInstanceDetail(LoginUser user, Project project) throws Exception;

    /**
     * 获取数据同步到加密服务的TOKEN
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     */
    String getSecurityToken(LoginUser user, Project project) throws Exception;


}
