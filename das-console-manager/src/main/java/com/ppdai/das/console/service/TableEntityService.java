package com.ppdai.das.console.service;

import com.google.common.base.Joiner;
import com.ppdai.das.console.common.exceptions.TransactionException;
import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.TableEntityDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.TaskTable;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.TaskTableView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TableEntityService {

    @Autowired
    private Message message;

    @Autowired
    private TableEntityDao tableEntityDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, TaskTable taskTable, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> this.validatePermision(taskTable.getProject_id(), user.getId()), message.permisson_user_crud);
    }

    private boolean validatePermision(Long projectId, Long userId) throws SQLException {
        return permissionService.isManagerById(userId) || permissionService.isProjectPermission(projectId, userId);
    }

    public ListResult<TaskTableView> findTableEntityPageList(Paging<TaskTable> paging) throws SQLException {
        Long count = tableEntityDao.getTableEntityTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<TaskTableView> list = tableEntityDao.findTableEntityPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public Long insertTask(TaskTable task) throws SQLException {
        this.initTaskTable(task);
        return tableEntityDao.insertTask(task);
    }

    /**
     * 批量添加物理库
     */
    public ServiceResult<String> addTaskTableList(LoginUser user, List<TaskTable> list) {
        try {
            for (TaskTable taskTable : list) {
                taskTable.setUpdate_user_no(user.getUserNo());
                this.initTaskTable(taskTable);
            }
            List<String> names = list.stream().map(i -> i.getCustom_table_name()).collect(Collectors.toList());
            List<TaskTable> taskTableList = tableEntityDao.getTaskTableByDbNames(list.get(0).getProject_id(), names);
            if (!CollectionUtils.isEmpty(taskTableList)) {
                String existDbs = Joiner.on(",").join(taskTableList.stream().map(i -> i.getCustom_table_name()).collect(Collectors.toList()));
                return ServiceResult.fail(existDbs + "，已经存在! 请在表实体管理页删除此实体类，再新建！！");
            }

            boolean isSussess = tableEntityDao.getDasClient().execute(() -> {
                int[] ids = tableEntityDao.insertTaskTablelist(list);
                if (ids.length <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
                return true;
            });

            if (isSussess) {
                return ServiceResult.success();
            }
        } catch (Exception e) {
            return ServiceResult.fail("批量添加 addTaskTableList " + StringUtil.getMessage(e));
        }
        return ServiceResult.fail("批量添加 addTaskTableList " + message.db_message_add_operation_failed);
    }


    private void initTaskTable(TaskTable task) {
        task.setApi_list("selectAllCreateMethodAPIChk,selectAllRetrieveMethodAPIChk,selectAllUpdateMethodAPIChk,selectAllDeleteMethodAPIChk");
        task.setSql_style("java");
        task.setSp_names(StringUtils.EMPTY);
        task.setPrefix(StringUtils.EMPTY);
        task.setSuffix(StringUtils.EMPTY);
        task.setCud_by_sp(false);
        task.setPagination(true);
        task.setGenerated(false);
        task.setVersion(1);
        task.setApproved(2);
        task.setApproveMsg(StringUtils.EMPTY);
    }

}
