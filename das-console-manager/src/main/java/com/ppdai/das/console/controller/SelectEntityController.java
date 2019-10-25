package com.ppdai.das.console.controller;

import com.ppdai.das.console.common.validates.chain.ValidateResult;
import com.ppdai.das.console.common.validates.group.selectEntity.AddSelectEntity;
import com.ppdai.das.console.common.validates.group.selectEntity.DeleteSelectEntity;
import com.ppdai.das.console.common.validates.group.selectEntity.UpdateSelectEntity;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dao.SelectEntityDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.TaskSql;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.SelectEntityService;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.TaskSqlView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping(value = "/selectEntity")
public class SelectEntityController {

    @Autowired
    private Message message;

    @Autowired
    private SelectEntityDao selectEntityDao;

    @Autowired
    private SelectEntityService selectEntityService;

    /**
     * 2、根据PROJECT ID获取task列表 翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<TaskSqlView>> list(@RequestBody Paging<TaskSql> paging) throws SQLException {
        return ServiceResult.success(selectEntityService.findSelectEntityPageList(paging));
    }

    /**
     * 2、新建
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddSelectEntity.class) @RequestBody TaskSql taskSql, @CurrentUser LoginUser user, Errors errors) throws Exception {
        taskSql.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = selectEntityService.validatePermision(user, taskSql, errors)
                .addAssert(() -> selectEntityService.insertTask(taskSql, user) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 3、更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateSelectEntity.class) @RequestBody TaskSql taskSql, @CurrentUser LoginUser user, Errors errors) throws Exception {
        taskSql.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = selectEntityService.validatePermision(user, taskSql, errors)
                .addAssert(() -> selectEntityDao.updateTask(taskSql) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteSelectEntity.class) @RequestBody TaskSql taskSql, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = selectEntityService.validatePermision(user, taskSql, errors)
                .addAssert(() -> selectEntityDao.deleteTask(taskSql) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

}
