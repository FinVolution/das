package com.ppdai.platform.das.codegen.service;

import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.ServerConfigDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.ServerConfig;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.model.page.PagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ServerConfigService {

    @Autowired
    private Message message;

    @Autowired
    private ServerConfigDao serverAppConfigDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public ListResult<ServerConfig> findServerAppConfigPageList(Paging<ServerConfig> paging) throws SQLException {
        Long count = serverAppConfigDao.getServerAppConfigTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<ServerConfig> list = serverAppConfigDao.findServerAppConfigPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }
}
