package com.ppdai.platform.das.console.service;

import com.google.common.collect.Sets;
import com.ppdai.platform.das.console.api.DefaultConfiguration;
import com.ppdai.platform.das.console.api.ProjectConfiguration;
import com.ppdai.platform.das.console.common.codeGen.generator.java.context.DataBaseGenContext;
import com.ppdai.platform.das.console.common.codeGen.generator.java.context.DbSetGenContext;
import com.ppdai.platform.das.console.common.codeGen.generator.java.generator.DataBaseGenerator;
import com.ppdai.platform.das.console.common.codeGen.generator.java.generator.DbSetGenerator;
import com.ppdai.platform.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.console.common.exceptions.TransactionException;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.*;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;
import com.ppdai.platform.das.console.dto.entry.das.ProjectDbsetRelation;
import com.ppdai.platform.das.console.dto.entry.das.UserProject;
import com.ppdai.platform.das.console.dto.model.Item;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ProjectModel;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.model.page.PagerUtil;
import com.ppdai.platform.das.console.dto.view.ProjectView;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserProjectDao userProjectDao;

    @Autowired
    private ProjectDbsetRelationDao projectDbsetRelationDao;

    @Autowired
    private SelectEntityDao selectEntityDao;

    @Autowired
    private Message message;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    @Autowired
    private ProjectConfiguration projectConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    public ValidatorChain validatePermision(LoginUser user, Project project, Errors errors) throws SQLException {
        return validatePermision(user.getId(), project.getDal_group_id(), errors);
    }

    private ValidatorChain validatePermision(Long userId, Long groupId, Errors errors) throws SQLException {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isGroupManagerById(groupId, userId) || permissionService.isManagerById(userId), message.group_message_no_pemission);
    }

    public ServiceResult<String> insertProject(Project project) throws SQLException {
        boolean isSussess = projectDao.getDasClient().execute(() -> {
            Long id = projectDao.insertProject(project);
            if (id <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            project.setId(id);
            //项目和逻辑库关系
            List<ProjectDbsetRelation> list = toProjectDbsetRelations(project);
            if (CollectionUtils.isNotEmpty(list)) {
                int[] ids = projectDbsetRelationDao.insertRelationList(list);
                if (ids.length <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
            }
            //项目负责人
            List<UserProject> userProjects = toUserProjects(project);
            if (CollectionUtils.isNotEmpty(userProjects)) {
                int[] ids = userProjectDao.insertUserProjectList(userProjects);
                if (ids.length <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail("insertProject fail : " + project.toString());
    }

    public ServiceResult<String> updateProject(Project project) throws SQLException {
        boolean isSussess = projectDbsetRelationDao.getDasClient().execute(() -> {
            //更新项目对应的逻辑库
            List<ProjectDbsetRelation> list = projectDbsetRelationDao.getAllProjectDbsetRelation(project.getId());
            Set<Long> oldDbsetIds = list.stream().map(e -> e.getDbsetId()).collect(Collectors.toSet());
            Set<Long> newDbsetIds = project.getItems().stream().map(e -> e.getId()).collect(Collectors.toSet());
            Set<Long> deleteDbsetIds = Sets.difference(oldDbsetIds, newDbsetIds).stream().collect(Collectors.toSet());
            Set<Long> addDbsetIds = Sets.difference(newDbsetIds, oldDbsetIds).stream().collect(Collectors.toSet());
            if (deleteCheckDao.isDbsetIdInTaskSQL(project.getId(), deleteDbsetIds) || deleteCheckDao.isDbsetIdInTaskTable(project.getId(), deleteDbsetIds)) {
                throw new RuntimeException("请在'代码生成器 --> 实体类管理'页先删除对应的实体类！！");
            }
            if (CollectionUtils.isNotEmpty(deleteDbsetIds) && projectDbsetRelationDao.deleteByProjectAndDbSetIdS(project.getId(), deleteDbsetIds) <= 0) {
                throw new RuntimeException(message.db_message_delete_operation_failed);
            }
            list = toProjectDbsetRelations(project, addDbsetIds);
            if (CollectionUtils.isNotEmpty(addDbsetIds) && projectDbsetRelationDao.insertRelationList(list).length <= 0) {
                throw new RuntimeException(message.db_message_update_operation_failed);
            }

            //更新项目负责人
            List<UserProject> users = userProjectDao.getUsersByProjectId(project.getId());
            Set<Long> oldUserIds = users.stream().map(e -> e.getUserId()).collect(Collectors.toSet());
            Set<Long> newUserIds = project.getUsers().stream().map(e -> e.getId()).collect(Collectors.toSet());
            Set<Long> deleteUserIds = Sets.difference(oldUserIds, newUserIds).stream().collect(Collectors.toSet());
            Set<Long> addUserIds = Sets.difference(newUserIds, oldUserIds).stream().collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(deleteUserIds) && userProjectDao.deleteByProjectAndUserIdS(project.getId(), deleteUserIds) <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            users = toUserProjects(project, addUserIds);
            if (CollectionUtils.isNotEmpty(addUserIds) && userProjectDao.insertUserProjectList(users).length <= 0) {
                throw new TransactionException(message.db_message_update_operation_failed);
            }
            if (projectDao.updateProject(project) <= 0) {
                throw new TransactionException(message.db_message_update_operation_failed);
            }
            return true;
        });

        if (isSussess) {
            return ServiceResult.success();
        }

        return ServiceResult.fail(message.db_message_update_operation_failed);
    }

    public ServiceResult deleteCheck(Long projectId) throws SQLException {
        if (deleteCheckDao.isProjectIdInProjectDbsetRelatio(projectId)) {
            return ServiceResult.fail("请先删除与逻辑库的关联关系！");
        }
        if (deleteCheckDao.isProjectIdInTaskSQL(projectId)) {
            return ServiceResult.fail("请先删除与之相关查询实体！");
        }
        if (deleteCheckDao.isProjectIdInTaskTable(projectId)) {
            return ServiceResult.fail("请先删除与之相关表实体！");
        }

        return ServiceResult.success();
    }

    public ServiceResult<String> deleteProject(Project project) throws SQLException {
        boolean isSussess = projectDao.getDasClient().execute(() -> {
            int id = projectDao.deleteProject(project);
            if (id <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            id = selectEntityDao.deleteByProjectId(project.getId());
            if (id < 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            id = selectEntityDao.deleteByProjectId(project.getId());
            if (id < 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            id = projectDbsetRelationDao.deleteByProjectId(project.getId());
            if (id < 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail("deleteProject fail : " + project.toString());
    }

    public ListResult<ProjectView> findProjectPageList(Paging<ProjectModel> paging) throws SQLException {
        Long count = projectDao.getProjectTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<ProjectView> list = projectDao.findProjectPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public boolean isExistByAppId(String appid) throws SQLException {
        return projectDao.getCountByAppId(appid) > 0;
    }

    public boolean isNotExistByAppId(Project project) throws SQLException {
        Long n = projectDao.getCountByAppId(project.getApp_id());
        Long i = projectDao.getCountByIdAndAppId(project.getId(), project.getApp_id());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public boolean isNotExistByName(Project project) throws SQLException {
        Long n = projectDao.getCountByName(project.getName());
        Long i = projectDao.getCountByIdAndName(project.getId(), project.getName());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    private List<ProjectDbsetRelation> toProjectDbsetRelations(Project project) {
        List<ProjectDbsetRelation> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(project.getItems())) {
            for (Item item : project.getItems()) {
                list.add(ProjectDbsetRelation.builder().dbsetId(item.getId()).projectId(project.getId()).updateUserNo(project.getUpdate_user_no()).build());
            }
        }
        return list;
    }

    private List<ProjectDbsetRelation> toProjectDbsetRelations(Project project, Set<Long> addDbsetIds) {
        List<ProjectDbsetRelation> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(addDbsetIds)) {
            for (Long dbsetId : addDbsetIds) {
                list.add(ProjectDbsetRelation.builder().dbsetId(dbsetId).projectId(project.getId()).updateUserNo(project.getUpdate_user_no()).build());
            }
        }
        return list;
    }

    private List<UserProject> toUserProjects(Project project) {
        List<UserProject> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(project.getUsers())) {
            for (Item item : project.getUsers()) {
                list.add(UserProject.builder().projectId(project.getId()).userId(item.getId()).build());
            }
        }
        return list;
    }

    private List<UserProject> toUserProjects(Project project, Set<Long> addUserIds) {
        List<UserProject> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(addUserIds)) {
            for (Long userId : addUserIds) {
                list.add(UserProject.builder().projectId(project.getId()).userId(userId).build());
            }
        }
        return list;
    }

    public ServiceResult generateProjectXml(LoginUser user, Long projectId, String codeGenFilePath) {
        try {
            Project project = projectDao.getProjectByID(projectId);
            DbSetGenerator generator = new DbSetGenerator();
            DbSetGenContext context = generator.createContext(project.getApp_id(), projectId, true);
            context.setGeneratePath(codeGenFilePath);
            generator.prepareDirectory(context);
            generator.prepareData(context);
            generator.generateCode(context);

            DataBaseGenerator dataBaseGenerator = new DataBaseGenerator();
            DataBaseGenContext dataBaseGenContext = dataBaseGenerator.createContext(project.getApp_id(), projectId, true);
            dataBaseGenContext.setGeneratePath(codeGenFilePath);
            dataBaseGenerator.prepareData(dataBaseGenContext);
            dataBaseGenerator.generateCode(dataBaseGenContext);
        } catch (Exception e) {
            return ServiceResult.fail("生成Xml文件失败 : " + StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> addDataCenter(LoginUser user, Project project) {
        try {
            projectConfiguration.addProject(user, project);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, Project oldProject, ProjectView oldProjectView, Project newProject) {
        try {
            projectConfiguration.updateProject(user, oldProject, oldProjectView, newProject);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, Project projec) {
        try {
            projectConfiguration.deleteProject(user, projec);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> syncProject(LoginUser user, Project project) {
        try {
            projectConfiguration.syncProject(user, project);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ConfigCkeckResult<List<ConfigDataResponse>> getCheckData(LoginUser user, Project project) {
        try {
            List<ConfigDataResponse> list = projectConfiguration.getCheckData(user, project);
            return ConfigCkeckResult.success("SUCCESS", list);
        } catch (Exception e) {
            return ConfigCkeckResult.fail(StringUtil.getMessage(e), e);
        }
    }

    public ServiceResult getProjectButton(LoginUser user) {
        return ServiceResult.success(defaultConfiguration.getProjectButton(user));
    }
}
