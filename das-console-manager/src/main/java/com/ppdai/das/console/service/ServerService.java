package com.ppdai.das.console.service;

import com.ppdai.das.console.common.exceptions.TransactionException;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.ServerDao;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Server;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.ServerView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ServerService {

    @Autowired
    private Message message;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public ListResult<ServerView> findServerPageList(Paging<Server> paging) throws SQLException {
        Long count = serverDao.getServerTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<ServerView> list = serverDao.findServerPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public ServiceResult insertServer(Server server) throws SQLException {
        Long id = serverDao.insertServer(server);
        if (id > 0) {
            server.setId(id);
            return ServiceResult.success();
        }
        return ServiceResult.fail(message.db_message_add_operation_failed);
    }

    public ServiceResult<String> deleteServer(Server server) {
        try {
            if (server.getId() == null) {
                return ServiceResult.fail("server id 为空");
            }
            boolean isSussess = serverDao.getDasClient().execute(() -> {
                int id = serverDao.deleteServerAndServerConfigByServerId(server.getId());
                if (id <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
                return true;
            });
            if (isSussess) {
                return ServiceResult.success();
            }
        } catch (Exception e) {
            return ServiceResult.fail("deleteServer 删除异常 " + message.db_message_add_operation_failed);
        }
        return ServiceResult.fail("deleteServer 删除异常 " + message.db_message_add_operation_failed);
    }


    public ServiceResult<String> addDataCenter(LoginUser user, Server server) {
        //return apolloServer.replace(user, server)
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, Server server) {
        //return  apolloServer.replace(user, server);
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, Server server) {
        //return apolloServer.delete(user, server.getId());
        return ServiceResult.success();
    }

    public ServiceResult<String> syncDataCenter(LoginUser user, Server server) {
        //return apolloServer.replace(user, server);
        return ServiceResult.success();
    }

    public List<ConfigDataResponse> getCheckData(LoginUser user, Server server) {
        //return apolloServer.getApolloCheckResponse(server);
        return ListUtils.EMPTY_LIST;
    }

}