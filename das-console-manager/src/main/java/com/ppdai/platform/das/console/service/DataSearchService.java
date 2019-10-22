package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.utils.JsonUtil;
import com.ppdai.platform.das.console.common.utils.RequestUtil;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.DataSearchLogDao;
import com.ppdai.platform.das.console.dto.entry.das.DataSearchLog;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.dataSearch.DataSearchRequest;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.model.page.PagerUtil;
import com.ppdai.platform.das.console.dto.view.DataSearchLogView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DataSearchService {

    @Autowired
    private Message message;

    @Autowired
    private DataSearchLogDao dataSearchLogDao;

    @Autowired
    private PermissionService permissionService;

    /**
     * 5 - 数据库一览 物理数据增删改
     * 删改权限：1）判断user是否在管理员组
     */
    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.group_message_no_pemission);
    }

    public Long addLog(HttpServletRequest request, DataSearchRequest dataSearchRequest, LoginUser user, int type, Boolean success, String result) throws SQLException {
        DataSearchLog dataSearchLog = DataSearchLog.builder()
                .user_no(user.getUserNo())
                .request_type(type)
                .request(JsonUtil.toJSONString(dataSearchRequest))
                .success(success)
                .result(result)
                .build();
        dataSearchLog.setIp(RequestUtil.getIpAddress(request));
        return dataSearchLogDao.insertDataSearchLog(dataSearchLog);
    }

    public ListResult<DataSearchLogView> findLogPageList(Paging<DataSearchLogView> paging) throws SQLException {
        Long count = dataSearchLogDao.getTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DataSearchLogView> list = dataSearchLogDao.findLogPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }
}
