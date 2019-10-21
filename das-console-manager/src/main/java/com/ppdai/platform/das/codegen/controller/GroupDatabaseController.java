package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.codegen.common.validates.group.groupdb.AddGroupDB;
import com.ppdai.platform.das.codegen.common.validates.group.groupdb.DeleteGroupDB;
import com.ppdai.platform.das.codegen.common.validates.group.groupdb.TransferGroupDB;
import com.ppdai.platform.das.codegen.common.validates.group.groupdb.UpdateGroupDB;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.DataBaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.service.DatabaseService;
import com.ppdai.platform.das.codegen.service.GroupDatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/groupdb")
public class GroupDatabaseController {

    @Autowired
    private Message message;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private GroupDatabaseService groupDatabaseService;

    @Autowired
    private DataBaseDao dataBaseDao;

    /**
     * 1、根据groupid获取数据库列表
     */
    @RequestMapping(value = "/dblist")
    public ServiceResult<List<DataBaseInfo>> getDBListByGroupId(@RequestParam("groupId") Long id) throws SQLException {
        return ServiceResult.success(dataBaseDao.getGroupDBsByGroup(id));
    }

    /**
     * 2、添加数物理据库信息到组
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddGroupDB.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dataBaseInfo.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = groupDatabaseService.validatePermision(user, errors)
                .addAssert(() -> !groupDatabaseService.isGroupHadDB(dataBaseInfo), "已存在此DB!!!")
                .addAssert(() -> dataBaseDao.updateDataBaseInfo(dataBaseInfo.getId(), dataBaseInfo.getDal_group_id(), dataBaseInfo.getComment()) > 0, message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }

        return databaseService.genDefaultDbsetAndEntry(dataBaseInfo);
    }

    /**
     * 3、修改组的数物理据库信息
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateGroupDB.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dataBaseInfo.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = groupDatabaseService.validatePermision(user, errors)
                .addAssert(() -> groupDatabaseService.isGroupHadDB(dataBaseInfo.getId(), dataBaseInfo.getDal_group_id()), message.message_operation_pemission)
                .addAssert(() -> dataBaseDao.updateDataBaseInfo(dataBaseInfo.getId(), dataBaseInfo.getComment()) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 4、删除组对应的数据库
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteGroupDB.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = groupDatabaseService.validatePermision(user, errors)
                .addAssert(() -> groupDatabaseService.isGroupHadDB(dataBaseInfo.getId(), dataBaseInfo.getDal_group_id()), message.message_operation_pemission)
                .addAssert(() -> dataBaseDao.updateDataBaseInfo(dataBaseInfo.getId(), -1L) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    /**
     * 5、转移数据库
     */
    @RequestMapping(value = "/transfer", method = RequestMethod.PUT)
    public ServiceResult<String> transfer(@Validated(TransferGroupDB.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = groupDatabaseService.validatePermision(user, errors)
                .addAssert(() -> dataBaseDao.updateDataBaseInfo(dataBaseInfo.getId(), dataBaseInfo.getTarget_dal_group_id()) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

}
