package com.ppdai.das.console.service;

import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.SelectEntityDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.TaskSql;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.TaskSqlView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class SelectEntityService {

    @Autowired
    private Message message;

    @Autowired
    private SelectEntityDao selectEntityDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, TaskSql taskSql, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> this.validatePermision(taskSql.getProject_id(), user.getId()), message.permisson_user_crud);
    }

    private boolean validatePermision(Long projectId, Long userId) throws SQLException {
        return permissionService.isManagerById(userId) || permissionService.isProjectPermission(projectId, userId);
    }

    public ListResult<TaskSqlView> findSelectEntityPageList(Paging<TaskSql> paging) throws SQLException {
        Long count = selectEntityDao.getSelectEntityTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<TaskSqlView> list = selectEntityDao.findSelectEntityPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public Long insertTask(TaskSql task, LoginUser user) throws SQLException {
        task.setPojo_name(task.getClass_name());
        task.setUpdate_user_no(user.getUserNo());
        task.setMethod_name(task.getClass_name());
        task.setParameters(StringUtils.EMPTY);
        task.setScalarType("List");
        task.setPojoType("EntityType");
        task.setPagination(false);
        task.setSql_style("sql_style");
        task.setApproved(2);
        task.setApproveMsg(StringUtils.EMPTY);
        task.setCrud_type("select");
        task.setGenerated(true);
        task.setVersion(1);
        task.setHints(StringUtils.EMPTY);
        return selectEntityDao.insertTask(task);
    }
}
