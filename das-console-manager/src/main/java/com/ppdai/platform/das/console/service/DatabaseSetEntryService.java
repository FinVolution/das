package com.ppdai.platform.das.console.service;

import com.google.common.base.Joiner;
import com.ppdai.platform.das.console.api.DbSetConfiguration;
import com.ppdai.platform.das.console.api.DefaultConfiguration;
import com.ppdai.platform.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.console.common.exceptions.TransactionException;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.DataBaseSetEntryDao;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.model.page.PagerUtil;
import com.ppdai.platform.das.console.dto.view.DatabaseSetEntryView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DatabaseSetEntryService {

    @Autowired
    private Message message;

    @Autowired
    private DataBaseSetEntryDao dataBaseSetEntryDao;

    @Autowired
    private DbSetConfiguration dbSetConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    public ListResult<DatabaseSetEntryView> findDbSetEntryPageList(Paging<DatabaseSetEntry> paging) throws SQLException {
        Long count = dataBaseSetEntryDao.getDbSetEntryTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DatabaseSetEntryView> list = dataBaseSetEntryDao.findDbSetEntryPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public boolean isNotExistByName(DatabaseSetEntry databasesetentry) throws SQLException {
        Long n = dataBaseSetEntryDao.getCountByName(databasesetentry.getName());
        Long i = dataBaseSetEntryDao.getCountByIdAndName(databasesetentry.getId(), databasesetentry.getName());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public boolean insertDatabaseSetEntry(DatabaseSetEntry databasesetentry) throws SQLException {
        Long id = dataBaseSetEntryDao.insertDatabaseSetEntry(databasesetentry);
        databasesetentry.setId(id);
        return id > 0;
    }

    /**
     * 批量添加物理库
     */
    public ServiceResult<String> addDatabaseSetEntryList(LoginUser user, List<DatabaseSetEntry> dBList) {
        try {
            for (DatabaseSetEntry item : dBList) {
                item.setName(item.getName().trim());
                item.setUpdate_user_no(user.getUserNo());
            }
            List<String> names = dBList.stream().map(i -> i.getName()).collect(Collectors.toList());
            List<DatabaseSetEntry> dalGroupDBList = dataBaseSetEntryDao.getDatabaseSetEntrysByDbNames(names);
            if (!CollectionUtils.isEmpty(dalGroupDBList)) {
                String existDbs = Joiner.on(",").join(dalGroupDBList.stream().map(i -> i.getName()).collect(Collectors.toList()));
                return ServiceResult.fail(existDbs + "，已经存在!");
            }
            boolean isSussess = dataBaseSetEntryDao.getDasClient().execute(() -> {
                int[] ids = dataBaseSetEntryDao.insertDatabaseSetEntrylist(dBList);
                if (ids.length <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
                return true;
            });
            if (isSussess) {
                return ServiceResult.success();
            }
        } catch (Exception e) {
            return ServiceResult.fail("批量添加 addDatabaseSetEntryList " + StringUtil.getMessage(e));
        }
        return ServiceResult.fail("批量添加 addDatabaseSetEntryList " + message.db_message_add_operation_failed);
    }

    public List<String> getNamesByEntryIds(List<Integer> dbset_ids) throws SQLException {
        if (CollectionUtils.isEmpty(dbset_ids)) {
            return ListUtils.EMPTY_LIST;
        }
        List<DatabaseSetEntry> list = dataBaseSetEntryDao.getAllDbSetEntryByDbSetIds(dbset_ids);
        List<String> shardings = list.stream().map(i -> i.getSharding()).collect(Collectors.toList());
        return shardings;
    }


    public ServiceResult<String> addDataCenterEntryList(LoginUser user, List<DatabaseSetEntry> dBList) {
        try {
            dbSetConfiguration.addDbSetEntryList(user, dBList);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, DatabaseSetEntry newDbSetEntry) {
        try {
            DatabaseSetEntry odlDbSetEntry = dataBaseSetEntryDao.getDataBaseSetEntryById(newDbSetEntry.getId());
            dbSetConfiguration.updateDbSetEntry(user, odlDbSetEntry, newDbSetEntry);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, DatabaseSetEntry dbsetEntry) {
        try {
            dbSetConfiguration.deleteDbSetEntry(user, dbsetEntry);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> syncDbsetEntry(LoginUser user, DatabaseSetEntry dbsetEntry) {
        try {
            dbSetConfiguration.syncDbsetEntry(user, dbsetEntry);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ConfigCkeckResult<List<ConfigDataResponse>> getCheckData(LoginUser user, DatabaseSetEntry dbsetEntry) {
        try {
            List<ConfigDataResponse> list = dbSetConfiguration.getCheckData(user, dbsetEntry);
            return ConfigCkeckResult.success("SUCCESS", list);
        } catch (Exception e) {
            return ConfigCkeckResult.fail(StringUtil.getMessage(e), e);
        }
    }

    public ServiceResult getDbSetEntryButton(LoginUser user) {
        return ServiceResult.success(defaultConfiguration.getDbSetEntryButton(user));
    }
}
