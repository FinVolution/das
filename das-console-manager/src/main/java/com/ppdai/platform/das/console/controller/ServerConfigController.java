package com.ppdai.platform.das.console.controller;

import com.ppdai.platform.das.console.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.console.common.validates.group.server.AddServerConfig;
import com.ppdai.platform.das.console.common.validates.group.server.DeleteServerConfig;
import com.ppdai.platform.das.console.common.validates.group.server.UpdateServerConfig;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.ServerConfigDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Server;
import com.ppdai.platform.das.console.dto.entry.das.ServerConfig;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.service.ServerConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping(value = "/serverConfig")
public class ServerConfigController {

    @Resource
    private Message message;

    @Autowired
    private ServerConfigService serverConfigService;

    @Autowired
    private ServerConfigDao serverConfigDao;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<Server>> loadPageList(@RequestBody Paging<ServerConfig> paging) throws SQLException {
        return ServiceResult.success(serverConfigService.findServerAppConfigPageList(paging));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddServerConfig.class) @RequestBody ServerConfig serverConfig, @CurrentUser LoginUser user, Errors errors) throws Exception {
        serverConfig.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverConfigService.validatePermision(user, errors)
                .addAssert(() -> serverConfigDao.insertServerAppConfig(serverConfig) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateServerConfig.class) @RequestBody ServerConfig serverConfig, @CurrentUser LoginUser user, Errors errors) throws Exception {
        serverConfig.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverConfigService.validatePermision(user, errors)
                .addAssert(() -> serverConfigDao.updateServerAppConfig(serverConfig) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteServerConfig.class) @RequestBody ServerConfig serverConfig, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = serverConfigService.validatePermision(user, errors)
                .addAssert(() -> serverConfigDao.deleteServerAppConfig(serverConfig) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success("阿波罗配置请自行删除！！");
    }
}
