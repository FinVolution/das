package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.configCenter.ConfigCheckBase;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCheckSubset;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.AddDbSet;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.DeleteDbSet;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.UpdateDbSet;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.DatabaseSetDao;
import com.ppdai.platform.das.codegen.dao.GroupDao;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.DasGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetView;
import com.ppdai.platform.das.codegen.service.DatabaseSetService;
import com.ppdai.platform.das.codegen.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/groupdbset")
public class DatabaseSetController {

    @Autowired
    private Message message;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DatabaseSetService databaseSetService;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    /**
     * 1、根据groupId查询逻辑数据库列表 dbset
     */
    @RequestMapping(value = "{groupId}/list")
    public ServiceResult<List<DatabaseSet>> lists(@PathVariable("groupId") Long groupId) throws Exception {
        return ServiceResult.success(databaseSetDao.getAllDatabaseSetByGroupId(groupId));
    }

    /**
     * 1、根据project Id查询逻辑数据库列表 dbset
     */
    @RequestMapping(value = "/list")
    public ServiceResult<List<DatabaseSetView>> list(@RequestParam(value = "projectId") Long projectId) throws Exception {
        return ServiceResult.success(databaseSetDao.getAllDatabaseSetByProjectId(projectId));
    }

    /**
     * 2、根据groupId查询逻辑数据库列表 dbset 分页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DatabaseSetView>> loadPageList(@RequestBody Paging<DatabaseSet> paging, @CurrentUser LoginUser user) throws SQLException {
        if (StringUtils.isNotBlank(paging.getData().getApp_id())) {
            if (permissionService.isManagerById(user.getId())) {
                return ServiceResult.success(databaseSetService.findDbSetPageListByAppid(paging));
            } else {
                return ServiceResult.success(ListResult.builder().list(ListUtils.EMPTY_LIST).build());
            }
        }
        return ServiceResult.success(databaseSetService.findDbSetPageList(paging));
    }

    /**
     * 2、新建逻辑数据库 dbset
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddDbSet.class) @RequestBody DatabaseSet dbset, @CurrentUser LoginUser user, Errors errors) throws SQLException {
        return addDbSet(dbset, user, errors);
    }

    /**
     * 3、更新逻辑数据库 dbset
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateDbSet.class) @RequestBody DatabaseSet dbset, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dbset.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> databaseSetService.updateDataCenter(user, dbset))
                .addAssert(() -> databaseSetService.updateDatabaseSet(dbset) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、删除逻辑数据库 dbset
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteDbSet.class) @RequestBody DatabaseSet dbset, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> databaseSetService.deleteCheck(dbset.getId()))
                .addAssert(() -> databaseSetService.deleteDataCenter(user, dbset))
                .addAssert(() -> databaseSetService.deleteDatabaseSet(dbset.getId()), message.db_message_update_operation_failed).validate();
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
        DatabaseSet databaseSet = databaseSetDao.getDatabaseSetById(id);
        return databaseSetService.syncDbSet(user, databaseSet);
    }

    /**
     * 当前一条数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult check(@CurrentUser LoginUser user, @RequestParam("id") Long id) throws Exception {
        DatabaseSet databaseSet = databaseSetDao.getDatabaseSetById(id);
        ConfigCkeckResult<List<ConfigDataResponse>> sr = databaseSetService.getCheckData(user, databaseSet);
        if (sr.getCode() == ConfigCkeckResult.ERROR) {
            return sr;
        }
        return ConfigCheckSubset.checkData(sr.getItem());
    }

    /**
     * 全组数据对比
     */
    @RequestMapping(value = "/groupCheck")
    public ConfigCkeckResult<ConfigCheckItem> groupCheck(@CurrentUser LoginUser user, @RequestParam("gourpId") Long gourpId) throws Exception {
        List<DatabaseSet> dbsets = databaseSetDao.getAllDatabaseSetByGroupId(gourpId);
        if (CollectionUtils.isNotEmpty(dbsets)) {
            List<ConfigDataResponse> list = databaseSetService.getAllCheckData(user, gourpId, dbsets.get(0));
            return ConfigCheckBase.checkData(list);
        }
        return ConfigCkeckResult.fail();
    }

    /**
     * 获取逻辑库配置缺省实现
     */
    /*@RequestMapping(value = "/config")
    public ServiceResult getConfig(@RequestParam("appId") Long appId) throws Exception {
        Project project = projectDao.getProjectByAppId(appId);
        List<DatabaseSet> dbsets = databaseSetDao.getAllDatabaseSetByGroupId(project.getDal_group_id());
        if (CollectionUtils.isNotEmpty(dbsets)) {
            ConfigDataResponse das = apolloDatabaseSet.getDasResponse(project.getDal_group_id(), dbsets.get(0));
            return ServiceResult.success(das);
        }
        return ServiceResult.fail();
    }*/

    /**
     * 同步数据到db
     */
    @RequestMapping(value = "/syncdb")
    public ServiceResult<String> syncdb(@Validated(AddDbSet.class) @RequestBody DatabaseSet dbset, @CurrentUser LoginUser user, Errors errors) throws Exception {
        DasGroup dasGroup = groupDao.getGroupByName(dbset.getGroupName());
        if (dasGroup == null) {
            return ServiceResult.fail("请先同步组！" + dbset.getGroupName());
        }
        dbset.setGroupId(dasGroup.getId());
        return addDbSet(dbset, user, errors);
    }

    private ServiceResult<String> addDbSet(@Validated(AddDbSet.class) @RequestBody DatabaseSet dbset, @CurrentUser LoginUser user, Errors errors) throws SQLException {
        dbset.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> CollectionUtils.isEmpty(databaseSetDao.getAllDatabaseSetByName(dbset.getName())), "databaseSet Name:" + dbset.getName() + " 已经存在，请重新命名!")
                .addAssert(() -> databaseSetService.insertDatabaseSet(dbset), message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseSetService.addDataCenter(user, dbset);
    }

    @RequestMapping("/buttons")
    public ServiceResult getDbSetButton(@CurrentUser LoginUser user) {
        return databaseSetService.getDbSetButton(user);
    }
}