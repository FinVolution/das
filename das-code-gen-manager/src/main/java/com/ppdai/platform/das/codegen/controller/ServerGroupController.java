package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.configCenter.ConfigCheckBase;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.server.AddServerGroup;
import com.ppdai.platform.das.codegen.common.validates.group.server.DeleteServerGroup;
import com.ppdai.platform.das.codegen.common.validates.group.server.UpdateServerGroup;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.ServerGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Server;
import com.ppdai.platform.das.codegen.dto.entry.das.ServerGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.ServerGroupView;
import com.ppdai.platform.das.codegen.service.ServerGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/serverGroup")
public class ServerGroupController {

    @Resource
    private Message message;

    @Autowired
    private ServerGroupService serverGroupService;

    @Autowired
    private ServerGroupDao serverGroupDaoOld;

    @RequestMapping(value = "/loadAllServerGroups")
    public ServiceResult<List<ServerGroup>> loadAllServerGroups() throws SQLException {
        return ServiceResult.success(serverGroupDaoOld.getAllServerGroups());
    }

    /**
     * 未分组的 server
     */
    @RequestMapping(value = "/serversNoGroup")
    public ServiceResult<List<Server>> serversNoGroup(@RequestParam(value = "appGroupId", defaultValue = "0") Integer appGroupId) throws SQLException {
        return ServiceResult.success(serverGroupDaoOld.serversNoGroup(appGroupId));
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<ServerGroupView>> loadPageList(@RequestBody Paging<ServerGroup> paging) throws SQLException {
        return ServiceResult.success(serverGroupService.findServerGroupPageList(paging));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddServerGroup.class) @RequestBody ServerGroup serverGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        serverGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverGroupService.validatePermision(user, errors)
                .addAssert(() -> serverGroupDaoOld.getCountByName(serverGroup.getName()) == 0, serverGroup.getName() + " 已存在！")
                .addAssert(() -> serverGroupDaoOld.insertServerGroup(serverGroup) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return serverGroupService.addDataCenter(user, serverGroup);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateServerGroup.class) @RequestBody ServerGroup serverGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        serverGroup.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverGroupService.validatePermision(user, errors)
                .addAssert(() -> serverGroupService.isNotExistByName(serverGroup), serverGroup.getName() + " 已存在！")
                .addAssert(() -> serverGroupDaoOld.updateServerGroup(serverGroup) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return serverGroupService.updateDataCenter(user, serverGroup);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteServerGroup.class) @RequestBody ServerGroup serverGroup, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = serverGroupService.validatePermision(user, errors)
                .addAssert(() -> serverGroupService.deleteCheck(serverGroup.getId()))
                .addAssert(() -> serverGroupService.deleteDataCenter(user, serverGroup))
                .addAssert(() -> serverGroupService.deleteServerGroup(serverGroup)).validate();
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
        ServerGroup serverGroup = serverGroupDaoOld.getServerGroupById(id);
        return serverGroupService.syncDataCenter(user, serverGroup);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult<ConfigCheckItem> check(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        ServerGroup serverGroup = serverGroupDaoOld.getServerGroupById(id);
        List<ConfigDataResponse> list = serverGroupService.getCheckData(user, serverGroup);
        return ConfigCheckBase.checkData(list);
    }
}