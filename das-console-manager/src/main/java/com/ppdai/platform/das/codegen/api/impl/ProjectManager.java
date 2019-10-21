package com.ppdai.platform.das.codegen.api.impl;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.api.ProjectConfiguration;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.common.utils.Transform;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ItemResponse;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.TitleResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import com.ppdai.platform.das.codegen.dto.view.ProjectView;
import com.ppdai.platform.das.codegen.openapi.ConfigProvider;
import com.ppdai.platform.das.codegen.openapi.vo.ProjectVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProjectManager implements ProjectConfiguration {

    @Autowired
    private Transform transform;

    @Autowired
    private ConfigProvider configProvider;

    @Override
    public void addProject(LoginUser user, Project project) throws Exception {
        configProvider.addProject(transform.toProjectVO(project));
    }

    @Override
    public void updateProject(LoginUser user, Project oldProject, ProjectView oldProjectView, Project newProject) throws Exception {
        if (!oldProjectView.getName().equals(newProject.getName())) {
            configProvider.deleteProject(newProject.getApp_id());
            configProvider.addProject(transform.toProjectVO(newProject));
        } else {
            configProvider.updateProject(transform.toProjectVO(newProject));
        }
    }

    @Override
    public void deleteProject(LoginUser user, Project project) throws Exception {
        configProvider.deleteProject(project.getApp_id());
    }

    @Override
    public void syncProject(LoginUser user, Project project) throws Exception {
        configProvider.updateProject(transform.toProjectVO(project));
    }

    @Override
    public List<ConfigDataResponse> getCheckData(LoginUser user, Project project) throws Exception {
        ProjectVO dasProjectVO = transform.toProjectVO(project);
        ProjectVO conProjectVO = configProvider.getProject(project.getApp_id());
        if (null == conProjectVO || StringUtils.isBlank(conProjectVO.getProjectName())) {
            throw new Exception("数据错误，项目信息为空！！！");
        }

        List<TitleResponse> dastitles = Lists.newArrayList(new TitleResponse("Appid", dasProjectVO.getAppId()), new TitleResponse("Project Name", dasProjectVO.getProjectName()));
        List<TitleResponse> contitles = Lists.newArrayList(new TitleResponse("Appid", conProjectVO.getAppId()), new TitleResponse("Project Name", conProjectVO.getProjectName()));
        ConfigDataResponse das = new ConfigDataResponse("DAS", dastitles, toList(project));
        ConfigDataResponse con = new ConfigDataResponse(configProvider.getConfigCenterName(), contitles, toList(conProjectVO));
        return Lists.newArrayList(das, con);
    }

    private List<ItemResponse> toList(Project project) {
        return toList(transform.toProjectVO(project));
    }

    private List<ItemResponse> toList(ProjectVO projectVO) {
        List<ItemResponse> list = Lists.newArrayList(
                new ItemResponse("projectName", projectVO.getProjectName()),
                new ItemResponse("teamName", projectVO.getTeamName()),
                new ItemResponse("databaseSets", StringUtil.joinCollectByComma(projectVO.getDatabaseSetNames()))
        );
        return list;
    }
}
