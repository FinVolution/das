package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.group.AddDalGroup;
import com.ppdai.platform.das.codegen.common.validates.group.group.DeleteGroup;
import com.ppdai.platform.das.codegen.common.validates.group.group.UpdateDalGroup;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.*;
import com.ppdai.platform.das.codegen.dto.entry.das.DasGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.UserGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.DalGroupView;
import com.ppdai.platform.das.codegen.service.GroupService;
import com.ppdai.platform.das.codegen.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * 三、组管理
 */
@Slf4j
@RestController
@RequestMapping(value = "/group")
public class GroupController {

    @Resource
    private GroupService groupService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private Message message;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private PermissionService permissionService;

    /**
     * 树形
     * 1)、普通用户查看获取当前用户所在的组列表
     * 2)、管理员查看所有组列表
     */
    @RequestMapping(value = "/tree")
    public ServiceResult<List<DasGroup>> getGroups(@RequestParam(value = "appid", defaultValue = "0") String appid, @CurrentUser LoginUser loginUser) throws Exception {
        if (permissionService.isManagerById(loginUser.getId())) {
            if (null != appid && !"0".equals(appid)) {
                return ServiceResult.success(groupDao.getAllGroupsByAppoid(appid));
            }
            return ServiceResult.success(groupDao.getAllGroupsWithNotAdmin());
        }
        return ServiceResult.success(groupDao.getGroupsByUserId(loginUser.getId()));
    }

    /**
     * 组员管理
     */
    @RequestMapping(value = "/member/tree")
    public ServiceResult<List<DasGroup>> getMemberGroups(@CurrentUser LoginUser loginUser) throws Exception {
        if (permissionService.isManagerById(Long.valueOf(loginUser.getId()))) {
            return ServiceResult.success(groupDao.getAllGroups());
        }
        return ServiceResult.success(groupDao.getGroupsByUserId(loginUser.getId()));
    }

    /**
     * 当前用户用户加入的所有组列表
     */
    @RequestMapping(value = "/userGroups")
    public ServiceResult<UserGroup> getUserGroups(@CurrentUser LoginUser user) throws Exception {
        return ServiceResult.success(userGroupDao.getUserGroupByUserId(user.getId()));
    }

    /**
     * 1、获取组列表，翻页列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DalGroupView>> getAllGroup(@RequestBody Paging<DasGroup> paging) throws SQLException {
        return ServiceResult.success(groupService.findGroupPageList(paging));
    }

    /**
     * 2、新建组
     */
    @RequestMapping(value = {"", "/", "/add"}, method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddDalGroup.class) @RequestBody DasGroup group, @CurrentUser LoginUser user, Errors errors) throws Exception {
        group.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = groupService.validatePermision(user, errors)
                .addAssert(() -> groupService.isNotExistInProjectAndGroup(group.getGroup_name()), "已存在!且组名和项目名不能重复！")
                .addAssert(() -> groupService.addDalGroup(user, group), message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 更新组，仅更新组名和备注等，APPID不能改
     */
    @RequestMapping(value = {"", "/", "/update"}, method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateDalGroup.class) @RequestBody DasGroup group, @CurrentUser LoginUser user, Errors errors) throws Exception {
        group.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = groupService.validatePermision(user, errors)
                .addAssert(() -> null != groupDao.getDalGroupById(group.getId()), "该组不存在!")
                .addAssert(() -> groupService.isNotExistByName(group), "组 : " + group.getGroup_name() + " 已存在!")
                .addAssert(() -> groupService.updateDalGroup(user, group), message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 删除组
     */
    @RequestMapping(value = {"", "/", "/delete"}, method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteGroup.class) @RequestBody DasGroup group, @CurrentUser LoginUser user, Errors errors) throws Exception {
        Long groupId = group.getId();
        ValidateResult validateRes = groupService.validatePermision(user, errors)
                .addAssert(() -> CollectionUtils.isEmpty(projectDao.getProjectByGroupId(groupId)), "当前DAS Team中还有Project，请清空Project后再操作！")
                .addAssert(() -> CollectionUtils.isEmpty(dataBaseDao.getGroupDBsByGroup(groupId)), "当前DAS Team中还有DataBase，请清空DataBase后再操作！")
                .addAssert(() -> CollectionUtils.isEmpty(databaseSetDao.getAllDatabaseSetByGroupId(groupId)), "当前DAL Team中还有DataBaseSet，请清空DataBaseSet后再操作！")
                .addAssert(() -> CollectionUtils.isEmpty(loginUserDao.getUserByGroupId(groupId)), "当前DAS Team中还有Member，请清空Member后再操作！")
                .addAssert(() -> groupDao.deleteDalGroup(groupId) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 同步数据到阿波罗
     */
    @RequestMapping(value = "/sync")
    public ServiceResult<String> sync(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        return ServiceResult.success();
        // TODO return apolloGroup.sync(user, id);
    }
}
