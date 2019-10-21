package com.ppdai.platform.das.codegen.api;

import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.display.*;

/**
 * 配置中心，安全中心，各个管理页按钮控制
 */
public interface DefaultConfiguration {

    /**
     * 配置中心的名称
     *
     * @return
     */
    String getConfigCenterName();

    /**
     * 安全中心的名称
     *
     * @return
     */
    String getSecurityCenterName();

    /**
     * 获取项目管理按钮信息
     *
     * @param user
     * @return
     */
    ProjectButton getProjectButton(LoginUser user);

    /**
     * 获取物理库管理按钮信息
     *
     * @param user
     * @return
     */
    DataBaseButton getDataBaseButton(LoginUser user);

    /**
     * 获取逻辑库管理按钮信息
     *
     * @param user
     * @return
     */
    DbSetButton getDbSetButton(LoginUser user);

    /**
     * 获取逻辑库映射管理按钮信息
     *
     * @param user
     * @return
     */
    DbSetEntryButton getDbSetEntryButton(LoginUser user);

    /**
     * 获取用户管理按钮信息
     *
     * @param user
     * @return
     */
    UserManageButton getUserManageButton(LoginUser user);

}
