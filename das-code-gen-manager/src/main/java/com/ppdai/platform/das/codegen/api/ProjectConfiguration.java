package com.ppdai.platform.das.codegen.api;

import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import com.ppdai.platform.das.codegen.dto.view.ProjectView;

import java.util.List;

/**
 * 项目管理页
 */
public interface ProjectConfiguration {

    /**
     * 添加项目
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     * @
     */
    void addProject(LoginUser user, Project project) throws Exception;

    /**
     * 更新项目信息
     *
     * @param user 当前操作人信息
     * @return
     * @
     */
    void updateProject(LoginUser user, Project oldProject, ProjectView oldProjectView, Project newProject) throws Exception;

    /**
     * 删除项目
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     * @
     */
    void deleteProject(LoginUser user, Project project) throws Exception;

    /**
     * 如果新建或修改更新配置中心失败，点击同步按钮同步项目信息到配置中心
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     * @
     */
    void syncProject(LoginUser user, Project project) throws Exception;

    /**
     * 数据校验, 两组数据或者三组对比，如果传多组，默认最多取前三组对比
     *
     * @param user    当前操作人信息
     * @param project
     * @return
     * @
     */
    List<ConfigDataResponse> getCheckData(LoginUser user, Project project) throws Exception;

}
