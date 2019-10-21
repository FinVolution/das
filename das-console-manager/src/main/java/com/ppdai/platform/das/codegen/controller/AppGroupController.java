package com.ppdai.platform.das.codegen.controller;


import com.ppdai.platform.das.codegen.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.appGroup.AddAppGroup;
import com.ppdai.platform.das.codegen.common.validates.group.appGroup.DeleteAppGroup;
import com.ppdai.platform.das.codegen.common.validates.group.appGroup.UpdateAppGroup;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.dao.AppGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.codegen.dto.entry.das.AppGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.AppGroupView;
import com.ppdai.platform.das.codegen.service.AppGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping(value = "/appGroup")
public class AppGroupController {

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private AppGroupService appGroupService;
    /**
     * 翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<AppGroupView>> getGroupUsers(@RequestBody Paging<AppGroup> paging) throws SQLException {
        return ServiceResult.success(appGroupService.findProjectGroupPageList(paging));
    }

    /**
     * 2、新建
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddAppGroup.class) @RequestBody AppGroup appGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        appGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = appGroupService.validatePermision(user, errors)
                .addAssert(() -> appGroupDao.getCountByName(appGroup.getName()) == 0, appGroup.getName() + " 已存在！")
                .addAssert(() -> appGroupService.insertAppGroup(appGroup))
                .validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
        //TODO return apolloAppGroup.replace(user, appGroup);
    }

    /**
     * 3、更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateAppGroup.class) @RequestBody AppGroup appGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        appGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = appGroupService.validatePermision(user, errors)
                .addAssert(() -> appGroupService.isNotExistByName(appGroup), appGroup.getName() + " 已存在！")
                .addAssert(() -> appGroupService.updateAppGroup(appGroup))
                .validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
        //TODO return apolloAppGroup.replace(user, appGroup);
    }

    /**
     * 4、删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteAppGroup.class) @RequestBody AppGroup appGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = appGroupService.validatePermision(user, errors)
                .addAssert(() -> appGroupService.deleteCheck(appGroup.getId()))
               /*TODO .addAssert(() -> apolloAppGroup.delete(user, appGroup.getId()))*/
                .addAssert(() -> appGroupService.deleteAppGroup(appGroup))
                .validate();
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
        AppGroup appGroup = appGroupDao.getAppGroupById(id);
        return ServiceResult.success();
        //TODO return apolloAppGroup.replace(user, appGroup);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult<ConfigCheckItem> check(@RequestParam("id") Long id) throws Exception {
        AppGroup appGroup = appGroupDao.getAppGroupById(id);
        return ConfigCkeckResult.success();
       /* TODO ConfigCheckResponse configCheckResponse = apolloAppGroup.getApolloCheckResponse(appGroup);
        return ConfigCheckBase.checkData(configCheckResponse);*/
    }
}
