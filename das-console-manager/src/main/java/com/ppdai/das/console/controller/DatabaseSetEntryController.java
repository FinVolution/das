package com.ppdai.das.console.controller;

import com.ppdai.das.console.common.configCenter.ConfigCheckSubset;
import com.ppdai.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.das.console.common.validates.chain.ValidateResult;
import com.ppdai.das.console.common.validates.group.dbSet.AddDbSetEntry;
import com.ppdai.das.console.common.validates.group.dbSet.DeleteDbSetEntry;
import com.ppdai.das.console.common.validates.group.dbSet.UpdateDbSetEntry;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dao.DataBaseDao;
import com.ppdai.das.console.dao.DataBaseSetEntryDao;
import com.ppdai.das.console.dao.DatabaseSetDao;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.service.DatabaseSetService;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dto.view.DatabaseSetEntryView;
import com.ppdai.das.console.service.DatabaseSetEntryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/groupdbSetEntry")
public class DatabaseSetEntryController {

    @Autowired
    private Message message;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private DatabaseSetService databaseSetService;

    @Autowired
    private DataBaseSetEntryDao dataBaseSetEntryDao;

    @Autowired
    private DatabaseSetEntryService databaseSetEntryService;

    /**
     * 1、根据逻辑数据库查询列表 DbsetEntry
     */
    @RequestMapping(value = "/{dbsetId}/list")
    public ServiceResult<List<DatabaseSetEntry>> list(@PathVariable("dbsetId") Long dbsetId) throws Exception {
        if (null == dbsetId || dbsetId == 0) {
            return ServiceResult.success(ListUtils.EMPTY_LIST);
        }
        return ServiceResult.success(dataBaseSetEntryDao.getAllDbSetEntryByDbSetId(dbsetId));
    }

    /**
     * 2、根据groupId查询逻辑数据库列表 DbsetEntry 分页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DatabaseSetEntryView>> loadPageList(@RequestBody Paging<DatabaseSetEntry> paging) throws SQLException {
        if (null == paging.getData().getDbset_id() || paging.getData().getDbset_id() == 0) {
            return ServiceResult.success(ListUtils.EMPTY_LIST);
        }
        return ServiceResult.success(databaseSetEntryService.findDbSetEntryPageList(paging));
    }

    /**
     * 2、新建逻辑数据库 DbsetEntry 单个，停用
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@Validated(AddDbSetEntry.class) @RequestBody DatabaseSetEntry dbsetEntry, @CurrentUser LoginUser user, Errors errors) throws Exception {
        return addDbSetEntry(dbsetEntry, user, errors);
    }

    /**
     * 2、批量新建逻辑数据库 DbsetEntry list
     */
    @RequestMapping(value = "/adds", method = RequestMethod.POST)
    public ServiceResult<String> adds(@Validated(AddDbSetEntry.class) @RequestBody List<DatabaseSetEntry> list, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> databaseSetEntryService.addDatabaseSetEntryList(user, list)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseSetEntryService.addDataCenterEntryList(user, list);
    }

    /**
     * 3、更新逻辑数据库 DbsetEntry
     */
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateDbSetEntry.class) @RequestBody DatabaseSetEntry dbsetEntry, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dbsetEntry.setUpdate_user_no(user.getUserNo());
        String oldName = dataBaseSetEntryDao.getDataBaseSetEntryById(dbsetEntry.getId()).getName();
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> databaseSetEntryService.isNotExistByName(dbsetEntry), dbsetEntry.getName() + " 已存在！")
                .addAssert(() -> dataBaseSetEntryDao.updateDatabaseSetEntry(dbsetEntry) > 0, message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseSetEntryService.updateDataCenter(user, dbsetEntry);
    }

    /**
     * 4、删除逻辑数据库 DbsetEntry
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteDbSetEntry.class) @RequestBody DatabaseSetEntry dbsetEntry, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> databaseSetEntryService.deleteDataCenter(user, dbsetEntry))
                .addAssert(() -> dataBaseSetEntryDao.deleteDatabaseSetEntryById(dbsetEntry.getId()) > 0, message.db_message_delete_operation_failed).validate();
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
        DatabaseSetEntry dbsetEntry = dataBaseSetEntryDao.getDataBaseSetEntryById(id);
        return databaseSetEntryService.syncDbsetEntry(user, dbsetEntry);
    }

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult check(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        DatabaseSetEntry databaseSetEntry = dataBaseSetEntryDao.getDataBaseSetEntryById(id);
        ConfigCkeckResult<List<ConfigDataResponse>> sr = databaseSetEntryService.getCheckData(user, databaseSetEntry);
        if (sr.getCode() == ServiceResult.ERROR) {
            return sr;
        }
        return ConfigCheckSubset.checkData(sr.getItem());
    }

    /**
     * 同步数据到db
     */
    @RequestMapping(value = "/syncdb")
    public ServiceResult<String> syncdb(@Validated(AddDbSetEntry.class) @RequestBody DatabaseSetEntry dbsetEntry, @CurrentUser LoginUser user, Errors errors) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDatabaseByName(dbsetEntry.getDbName());
        if (dataBaseInfo == null) {
            return ServiceResult.fail("请先同步物理库！" + dbsetEntry.getDbName());
        }
        DatabaseSet databaseSet = databaseSetDao.getDatabaseSetByName(dbsetEntry.getDbsetName());
        if (databaseSet == null) {
            return ServiceResult.fail("请先同步逻辑库！" + dbsetEntry.getDbsetName());
        }

        dbsetEntry.setDb_Id(dataBaseInfo.getId());
        dbsetEntry.setDbset_id(databaseSet.getId());
        return addDbSetEntry(dbsetEntry, user, errors);
    }

    private ServiceResult<String> addDbSetEntry(@Validated(AddDbSetEntry.class) @RequestBody DatabaseSetEntry dbsetEntry, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dbsetEntry.setUpdate_user_no(user.getUserNo());
        List<DatabaseSetEntry> list = new ArrayList<>();
        list.add(dbsetEntry);
        ValidateResult validateRes = databaseSetService.validatePermision(user, errors)
                .addAssert(() -> dataBaseSetEntryDao.getCountByName(dbsetEntry.getName()) == 0, dbsetEntry.getName() + " 已存在！")
                .addAssert(() -> databaseSetEntryService.insertDatabaseSetEntry(dbsetEntry), message.db_message_add_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseSetEntryService.addDataCenterEntryList(user, list);
    }

    @RequestMapping("/buttons")
    public ServiceResult getProjectButton(@CurrentUser LoginUser user) {
        return databaseSetEntryService.getDbSetEntryButton(user);
    }
}
