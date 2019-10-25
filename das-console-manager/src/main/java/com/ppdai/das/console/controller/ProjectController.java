package com.ppdai.das.console.controller;

import com.ppdai.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.das.console.common.configCenter.ConfigCheckBase;
import com.ppdai.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.das.console.common.utils.DateUtil;
import com.ppdai.das.console.common.utils.FileUtils;
import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.common.validates.chain.ValidateResult;
import com.ppdai.das.console.common.validates.group.project.AddProject;
import com.ppdai.das.console.common.validates.group.project.DeleteProject;
import com.ppdai.das.console.common.validates.group.project.UpdateProject;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.DatabaseSetDao;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.*;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.ProjectView;
import com.ppdai.das.console.service.GroupService;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    private Consts consts;

    @Autowired
    private Message message;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private GroupDao groupDao;

/*    @Autowired
    private AppDataProject appDataProject;*/

    /**
     * 1、根据name模糊查询Project
     */
    @RequestMapping(value = "/projects")
    public ServiceResult<List<Project>> getProjects(@RequestParam(value = "name", defaultValue = "") String name) throws SQLException {
        return ServiceResult.success(projectDao.getProjectsListByLikeName(name));
    }

    /**
     * 未分组的project
     */
    @RequestMapping(value = "/projectsNoGroup")
    public ServiceResult<List<Project>> getProjectsNoGroup(@RequestParam(value = "appGroupId", defaultValue = "0") Long appGroupId) throws SQLException {
        return ServiceResult.success(projectDao.getProjectsNoGroup(appGroupId));
    }

    @RequestMapping(value = "/projectsByAppGroupId")
    public ServiceResult<List<Project>> getProjectsByAppGroupId(@RequestParam(value = "appGroupId") Long appGroupId) throws SQLException {
        if (null == appGroupId) {
            return ServiceResult.fail("appGroupId is null");
        }
        return ServiceResult.success(projectDao.getProjectsByAppGroupId(appGroupId));
    }

    @RequestMapping(value = "/group")
    public ServiceResult<List<Project>> getGroupProjects(@RequestParam("groupId") Long groupId) throws Exception {
        return ServiceResult.success(projectDao.getProjectByGroupId(groupId));
    }

    /**
     * 2、根据GROUP ID获取项目列表 翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<ProjectView>> getGroupUsers(@RequestBody Paging<ProjectModel> paging, @CurrentUser LoginUser user) throws SQLException {
        if (null == paging.getData().getDal_group_id() || paging.getData().getDal_group_id() == 0) {
            if (permissionService.isManagerById(user.getId())) {
                return ServiceResult.success(projectService.findProjectPageList(paging));
            } else {
                return ServiceResult.success(ListResult.builder().list(ListUtils.EMPTY_LIST).build());
            }
        } else if (-1 == paging.getData().getDal_group_id()) {

        }
        return ServiceResult.success(projectService.findProjectPageList(paging));
    }

    /**
     * 2、新建project
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddProject.class) @RequestBody Project project, @CurrentUser LoginUser user, Errors errors) throws Exception {
        return addProject(project, user, errors);
    }

    /**
     * 3、更新project
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateProject.class) @RequestBody Project project, @CurrentUser LoginUser user, Errors errors) throws Exception {
        project.setUpdate_user_no(user.getUserNo());
        Project oldProject = projectDao.getProjectByID(project.getId());
        ProjectView oldProjectView = projectDao.getProjectViewById(project.getId());
        ValidateResult validateRes = projectService.validatePermision(user, project, errors)
                .addAssert(() -> projectService.isNotExistByName(project), project.getName() + " 已存在！")
                .addAssert(() -> projectService.isNotExistByAppId(project), "APPID:" + project.getApp_id() + " 已存在！")
                .addAssert(() -> projectService.updateProject(project))
                .addAssert(() -> projectService.updateDataCenter(user, oldProject, oldProjectView, project)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、删除 project
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteProject.class) @RequestBody Project project, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = projectService.validatePermision(user, project, errors)
                .addAssert(() -> projectService.deleteCheck(project.getId()))
                .addAssert(() -> projectService.deleteDataCenter(user, project))
                .addAssert(() -> projectService.deleteProject(project).getCode() == 200, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 同步数据到阿波罗，单条
     */
    @RequestMapping(value = "/sync")
    public ServiceResult<String> sync(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        Project project = projectDao.getProjectByID(id);
        return projectService.syncProject(user, project);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult check(@CurrentUser LoginUser user, @RequestParam("id") Long id) throws Exception {
        Project project = projectDao.getProjectByID(id);
        ConfigCkeckResult<List<ConfigDataResponse>> sr = projectService.getCheckData(user, project);
        if (sr.getCode() == ConfigCkeckResult.ERROR) {
            return sr;
        }
        return ConfigCheckBase.checkData(sr.getItem());
    }

    /**
     * 获取项目配置缺省实现
     */
 /*   @RequestMapping(value = "/config")
    public ServiceResult getConfig(@RequestParam("appId") long appId) throws Exception {
        Project project = projectDao.getProjectByAppId(appId);
        ConfigDataResponse configDataResponse = appDataProject.getDasResponse(project);
        return ServiceResult.success(configDataResponse);
    }*/

    /**
     * 跨环境同步数据到db
     */
    @RequestMapping(value = "/syncdb")
    public ServiceResult<String> syncdb(@Validated(AddProject.class) @RequestBody Project project, @CurrentUser LoginUser user, Errors errors) throws Exception {
        DasGroup dasGroup = groupDao.getGroupByName(project.getGroupName());
        if (dasGroup == null) {
            return ServiceResult.fail("请先同步组！" + project.getGroupName());
        }
        List<DatabaseSet> list = databaseSetDao.getAllDatabaseSetByNames(StringUtil.toList(project.getDbsetNamees()));
        if (CollectionUtils.isEmpty(list)) {
            return ServiceResult.fail("请先同步逻辑库！" + project.getDbsetNamees());
        }
        String dbSetNames = project.getDbsetNamees();
        if (dbSetNames.contains(",") && list.size() < dbSetNames.split(",").length) {
            return ServiceResult.fail("请先同步全部的逻辑库！" + project.getDbsetNamees());
        }
        List<Item> items = list.stream().map(i -> new Item(i.getId(), i.getName())).collect(Collectors.toList());
        project.setDal_group_id(dasGroup.getId());
        project.setItems(items);
        return addProject(project, user, errors);
    }

    private ServiceResult<String> addProject(@Validated(AddProject.class) @RequestBody Project project, @CurrentUser LoginUser user, Errors errors) throws Exception {
        project.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = projectService.validatePermision(user, project, errors)
                .addAssert(() -> groupService.isNotExistInProjectAndGroup(project.getName()), project.getName() + " 已存在！且组名和项目名不能重复！")
                .addAssert(() -> projectDao.getCountByAppId(project.getApp_id()) == 0, "APPID:" + project.getApp_id() + " 已存在！")
                .addAssert(() -> projectService.insertProject(project))
                .addAssert(() -> projectService.addDataCenter(user, project)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    public ServiceResult generateProject(@CurrentUser LoginUser user, @RequestBody GenerateCodeModel generateCodeRequest) {
        return projectService.generateProjectXml(user, generateCodeRequest.getProjectId(), consts.codeGenFilePath);
    }

    @RequestMapping("/download")
    public String download(@RequestParam(value = "projectId") Long projectId, @CurrentUser LoginUser user, HttpServletResponse response) throws Exception {
        ServiceResult sr = projectService.generateProjectXml(user, projectId, consts.codeGenFilePath);
        if (sr.getCode() == ServiceResult.ERROR) {
            throw new Exception(sr.getMsg().toString());
        }
        File file = new File(new File(new File(consts.codeGenFilePath, String.valueOf(projectId)), CodeGenConsts.JAVA), "db");
        Project project = projectDao.getProjectByID(projectId);
        String date = DateUtil.getCurrentTime();
        final String zipFileName = project.getName() + "-db-" + date + ".zip";
        return FileUtils.download(response, file, zipFileName, consts.codeGenFilePath);
    }

    @RequestMapping("/buttons")
    public ServiceResult getProjectButton(@CurrentUser LoginUser user) {
        return projectService.getProjectButton(user);
    }
}
