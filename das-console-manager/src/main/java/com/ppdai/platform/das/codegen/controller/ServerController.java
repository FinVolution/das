package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.configCenter.ConfigCheckBase;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.server.AddServer;
import com.ppdai.platform.das.codegen.common.validates.group.server.DeleteServer;
import com.ppdai.platform.das.codegen.common.validates.group.server.UpdateServer;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.ServerDao;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigCheckItem;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Server;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.ServerView;
import com.ppdai.platform.das.codegen.service.ServerService;
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
@RequestMapping(value = "/server")
public class ServerController {

    @Resource
    private Message message;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerDao serverDao;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<ServerView>> loadPageList(@RequestBody Paging<Server> paging) throws SQLException {
        return ServiceResult.success(serverService.findServerPageList(paging));
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddServer.class) @RequestBody Server server, @CurrentUser LoginUser user, Errors errors) throws Exception {
        server.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverService.validatePermision(user, errors)
                .addAssert(() -> serverDao.isNotExistByIpAndPort(server), "此IP和端口已存在！")
                .addAssert(() -> serverService.insertServer(server)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return serverService.addDataCenter(user, server);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateServer.class) @RequestBody Server server, @CurrentUser LoginUser user, Errors errors) throws Exception {
        server.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = serverService.validatePermision(user, errors)
                .addAssert(() -> serverDao.updateServer(server) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return serverService.updateDataCenter(user, server);

    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteServer.class) @RequestBody Server server, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = serverService.validatePermision(user, errors)
                .addAssert(() -> serverService.deleteDataCenter(user, server))
                .addAssert(() -> serverService.deleteServer(server))
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
        Server server = serverDao.getServerById(id);
        return serverService.syncDataCenter(user, server);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult<ConfigCheckItem> check(@CurrentUser LoginUser user, @RequestParam("id") Long id) throws Exception {
        Server server = serverDao.getServerById(id);
        List<ConfigDataResponse> list = serverService.getCheckData(user, server);
        return ConfigCheckBase.checkData(list);
    }
}
