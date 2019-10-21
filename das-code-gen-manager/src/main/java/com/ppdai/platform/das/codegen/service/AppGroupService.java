package com.ppdai.platform.das.codegen.service;

import com.google.common.collect.Sets;
import com.ppdai.platform.das.codegen.common.exceptions.TransactionException;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.AppGroupDao;
import com.ppdai.platform.das.codegen.dao.DeleteCheckDao;
import com.ppdai.platform.das.codegen.dao.ProjectDao;
import com.ppdai.platform.das.codegen.dto.entry.das.AppGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.model.page.PagerUtil;
import com.ppdai.platform.das.codegen.dto.view.AppGroupView;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AppGroupService {

    @Autowired
    private Message message;

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public ListResult<AppGroupView> findProjectGroupPageList(Paging<AppGroup> paging) throws SQLException {
        Long count = appGroupDao.getAppGroupTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<AppGroupView> list = appGroupDao.findAppGroupPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public ServiceResult<String> insertAppGroup(AppGroup appGroup) throws SQLException {
        boolean isSussess = appGroupDao.getDasClient().execute(() -> {
            Long id = appGroupDao.insertAppGroup(appGroup);
            if (id <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            appGroup.setId(id);
            Set<Long> projectIds = appGroup.getItems().stream().map(e -> e.getId()).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(projectIds)) {
                int rs = projectDao.updateProjectAppGroupIdById(id, projectIds);
                System.out.println(rs);
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail(message.db_message_update_operation_failed);
    }

    public ServiceResult deleteCheck(Long dbsetId) throws SQLException {
        if (deleteCheckDao.isDbsetIdInProject(dbsetId)) {
            return ServiceResult.fail("请先删除应用组关联的项目！");
        }

        return ServiceResult.success();
    }

    public ServiceResult<String> deleteAppGroup(AppGroup appGroup) throws SQLException {
        boolean isSussess = appGroupDao.getDasClient().execute(() -> {
            List<Project> list = projectDao.getProjectByAppGroupId(appGroup.getId());
            if (CollectionUtils.isNotEmpty(list) && projectDao.deleteProjectAppGroupIdById(appGroup.getId()) <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            int rs = appGroupDao.deleteAppGroup(appGroup);
            if (rs <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail(message.db_message_delete_operation_failed);
    }

    public ServiceResult<String> updateAppGroup(AppGroup appGroup) throws SQLException {
        boolean isSussess = projectDao.getDasClient().execute(() -> {
            List<Project> list = projectDao.getProjectByAppGroupId(appGroup.getId());
            Set<Long> oldProjectIds = list.stream().map(e -> e.getId()).collect(Collectors.toSet());
            Set<Long> newProjectIds = appGroup.getItems().stream().map(e -> e.getId()).collect(Collectors.toSet());
            Set<Long> deleteProjectIds = Sets.difference(oldProjectIds, newProjectIds).stream().collect(Collectors.toSet());
            Set<Long> addProjectIds = Sets.difference(newProjectIds, oldProjectIds).stream().collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(deleteProjectIds) && projectDao.deleteProjectAppGroupIdByIdS(deleteProjectIds) <= 0) {
                throw new TransactionException(message.db_message_delete_operation_failed);
            }
            if (CollectionUtils.isNotEmpty(addProjectIds) && projectDao.updateProjectAppGroupIdById(appGroup.getId(), addProjectIds) <= 0) {
                throw new TransactionException(message.db_message_update_operation_failed);
            }

            if (appGroupDao.updateAppGroup(appGroup) <= 0) {
                throw new TransactionException(message.db_message_update_operation_failed);
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail(message.db_message_update_operation_failed);
    }

    public boolean isNotExistByName(AppGroup appGroup) throws SQLException {
        Long n = appGroupDao.getCountByName(appGroup.getName());
        Long i = appGroupDao.getCountByIdAndName(appGroup.getId(), appGroup.getName());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }


}
