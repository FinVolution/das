package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.exceptions.TransactionException;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.AppGroupDao;
import com.ppdai.platform.das.console.dao.DeleteCheckDao;
import com.ppdai.platform.das.console.dao.ServerGroupDao;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.AppGroup;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.ServerGroup;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.model.page.PagerUtil;
import com.ppdai.platform.das.console.dto.view.ServerGroupView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerGroupService {

    @Autowired
    private Message message;

    @Autowired
    private AppGroupDao appGroupDao;

    @Autowired
    private ServerGroupDao serverGroupDaoOld;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public ListResult<ServerGroupView> findServerGroupPageList(Paging<ServerGroup> paging) throws SQLException {
        Long count = serverGroupDaoOld.getServerGroupTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<ServerGroupView> list = serverGroupDaoOld.findServerGroupPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public ServiceResult deleteCheck(Long dbsetId) throws SQLException {
        if (deleteCheckDao.isServerGroupIdInAppGroup(dbsetId)) {
            return ServiceResult.fail("请先删除应用组关联的应用组关系！");
        }

        if (deleteCheckDao.isServerGroupIdInServer(dbsetId)) {
            return ServiceResult.fail("请先删除服务器组关联的Server！");
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteServerGroup(ServerGroup serverGroup) throws SQLException {
        if (serverGroup.getId() == null) {
            return ServiceResult.fail("serverGroup id 为空");
        }
        boolean isSussess = appGroupDao.getDasClient().execute(() -> {
            int id = serverGroupDaoOld.deleteServerGroupAndServerAndServerConfigByServerGroupId(serverGroup.getId());
            if (id <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            List<AppGroup> list = appGroupDao.getAppGroupsByServerGroupId(serverGroup.getId());
            Set<Long> appGroupIds = list.stream().map(i -> i.getId()).collect(Collectors.toSet());
            id = appGroupDao.changeServerGroup(0L, appGroupIds);
            if (id <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            return true;
        });
        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail("deleteServerGroup 删除异常 " + message.db_message_add_operation_failed);
    }

    public boolean isNotExistByName(ServerGroup serverGroup) throws SQLException {
        Long n = serverGroupDaoOld.getCountByName(serverGroup.getName());
        Long i = serverGroupDaoOld.getCountByIdAndName(serverGroup.getId(), serverGroup.getName());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public ServiceResult<String> addDataCenter(LoginUser user, ServerGroup serverGroup) {
        //return apolloServerGroup.replace(user, serverGroup)
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, ServerGroup serverGroup) {
        //        return apolloServerGroup.replace(user, serverGroup);
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, ServerGroup serverGroup) {
        // return apolloServerGroup.delete(user, serverGroup.getId())
        return ServiceResult.success();
    }

    public ServiceResult<String> syncDataCenter(LoginUser user, ServerGroup serverGroup) {
        //return apolloServerGroup.replace(user, serverGroup);
        return ServiceResult.success();
    }

    public List<ConfigDataResponse> getCheckData(LoginUser user, ServerGroup serverGroup) {
        //return apolloServerGroup.getApolloCheckResponse(serverGroup);
        return ListUtils.EMPTY_LIST;
    }
}