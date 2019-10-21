package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.tableEntity.AddTableEntity;
import com.ppdai.platform.das.codegen.common.validates.group.tableEntity.DeleteTableEntity;
import com.ppdai.platform.das.codegen.common.validates.group.tableEntity.UpdateTableEntity;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.TableEntityDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.TaskTable;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.TaskTableView;
import com.ppdai.platform.das.codegen.service.TableEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/tableEntity")
public class TableEntityController {

    @Autowired
    private Message message;

    @Autowired
    private TableEntityDao tableEntityDao;

    @Autowired
    private TableEntityService tableEntityService;

    /**
     * 2、根据PROJECT ID获取task列表 翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<TaskTableView>> list(@RequestBody Paging<TaskTable> paging) throws SQLException {
        return ServiceResult.success(tableEntityService.findTableEntityPageList(paging));
    }

    /**
     * 2、新建
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddTableEntity.class) @RequestBody TaskTable taskTable, @CurrentUser LoginUser user, Errors errors) throws Exception {
        taskTable.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = tableEntityService.validatePermision(user, taskTable, errors)
                .addAssert(() -> tableEntityService.insertTask(taskTable) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }


    /**
     * 2、批量新建
     */
    @RequestMapping(value = "/adds", method = RequestMethod.POST)
    public ServiceResult<String> adds(@Validated(AddTableEntity.class) @RequestBody List<TaskTable> list, @CurrentUser LoginUser user, Errors errors) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return ServiceResult.fail("参数为空");
        }
        ValidateResult validateRes = tableEntityService.validatePermision(user, list.get(0), errors)
                .addAssert(() -> tableEntityService.addTaskTableList(user, list)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 3、更新
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateTableEntity.class) @RequestBody TaskTable taskTable, @CurrentUser LoginUser user, Errors errors) throws Exception {
        taskTable.setUpdate_user_no(user.getUserNo());
        ValidateResult validateRes = tableEntityService.validatePermision(user, taskTable, errors)
                .addAssert(() -> tableEntityDao.updateTask(taskTable) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteTableEntity.class) @RequestBody TaskTable taskTable, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = tableEntityService.validatePermision(user, taskTable, errors)
                .addAssert(() -> tableEntityDao.deleteTask(taskTable) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

}
