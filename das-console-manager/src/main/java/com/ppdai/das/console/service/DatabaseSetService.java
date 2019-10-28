package com.ppdai.das.console.service;

import com.ppdai.das.console.api.DbSetConfiguration;
import com.ppdai.das.console.api.DefaultConfiguration;
import com.ppdai.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.DatabaseSetDao;
import com.ppdai.das.console.dao.DeleteCheckDao;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.DatabaseSetView;
import com.ppdai.das.console.enums.StrategyTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


@Service
public class DatabaseSetService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private Message message;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    @Autowired
    private DbSetConfiguration dbSetConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    public boolean insertDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (dbset.getStrategyType() == StrategyTypeEnum.NoStrategy.getType()) {
            dbset.setClassName(StringUtils.EMPTY);
            dbset.setStrategySource(StringUtils.EMPTY);
        }
        Long id = databaseSetDao.insertDatabaseSet(dbset);
        dbset.setId(id);
        return id > 0;
    }

    public int updateDatabaseSet(DatabaseSet dbset) throws SQLException {
        if (dbset.getStrategyType() == StrategyTypeEnum.NoStrategy.getType()) {
            dbset.setClassName(StringUtils.EMPTY);
            dbset.setStrategySource(StringUtils.EMPTY);
        }
        return databaseSetDao.updateDatabaseSet(dbset);
    }

    public ServiceResult deleteCheck(Long dbsetId) throws SQLException {
        if (deleteCheckDao.isDbsetIdInDatabasesetentry(dbsetId)) {
            return ServiceResult.fail("请先删除对应的逻辑库映射！");
        }
        if (deleteCheckDao.isDbsetIdInProjectDbsetRelation(dbsetId)) {
            return ServiceResult.fail("请先删除项目关联的逻辑库！");
        }
        if (deleteCheckDao.isDbsetIdInTaskSQL(dbsetId)) {
            return ServiceResult.fail("请先删除查询实体关联的逻辑库！");
        }
        if (deleteCheckDao.isDbsetIdInTaskTable(dbsetId)) {
            return ServiceResult.fail("请先删除表实体关联的逻辑库！");
        }
        return ServiceResult.success();
    }

    public boolean deleteDatabaseSet(Long dbsetId) throws SQLException {
        boolean isSussess = deleteCheckDao.getDasClient().execute(() -> {
            int ret1 = databaseSetDao.deleteDatabaseSetEntryByDbsetId(dbsetId);
            int ret2 = databaseSetDao.deleteDatabaseSetById(dbsetId);
            if (ret1 < 0 || ret2 < 0) {
                return false;
            }
            return true;
        });
        return isSussess;
    }

    public ListResult<DatabaseSetView> findDbSetPageList(Paging<DatabaseSet> paging) throws SQLException {
        Long count = databaseSetDao.getDbSetTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DatabaseSetView> list = databaseSetDao.findDbSetPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public List<DatabaseSet> getAllDatabaseSetByProjectId(Long projectId) throws SQLException {
        Project project = projectDao.getProjectByID(projectId);
        if (null != project) {
            return databaseSetDao.getAllDatabaseSetByGroupId(project.getDal_group_id());
        }
        return Collections.EMPTY_LIST;
    }


    public List<DatabaseSet> getAllDatabaseSetByAppId(String appId) throws SQLException {
        Project project = projectDao.getProjectByAppId(appId);
        return getAllDatabaseSetByProjectId(project.getId());
    }

    public ListResult<DatabaseSetView> findDbSetPageListByAppid(Paging<DatabaseSet> paging) throws SQLException {
        Long count = databaseSetDao.getDbSetTotalCountByAppid(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DatabaseSetView> list = databaseSetDao.findDbSetPageListByAppid(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public ServiceResult<String> addDataCenter(LoginUser user, DatabaseSet dbset) {
        try {
            dbSetConfiguration.addDbSet(user, dbset);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, DatabaseSet newDbset) {
        try {
            DatabaseSet oldDbset = databaseSetDao.getDatabaseSetById(newDbset.getId());
            dbSetConfiguration.updateDbSet(user, oldDbset, newDbset);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, DatabaseSet dbset) {
        try {
            dbSetConfiguration.deleteDbSet(user, dbset);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> syncDbSet(LoginUser user, DatabaseSet dbset) {
        try {
            dbSetConfiguration.syncDbSet(user, dbset);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ConfigCkeckResult<List<ConfigDataResponse>> getCheckData(LoginUser user, DatabaseSet dbset) {
        try {
            List<ConfigDataResponse> list = dbSetConfiguration.getCheckData(user, dbset);
            return ConfigCkeckResult.success("success", list);
        } catch (Exception e) {
            return ConfigCkeckResult.fail(StringUtil.getMessage(e), ListUtils.EMPTY_LIST);
        }
    }

    public ServiceResult<String> getDbSetButton(LoginUser user) {
        return ServiceResult.success(defaultConfiguration.getDbSetButton(user));
    }

    public List<ConfigDataResponse> getAllCheckData(LoginUser user, Long gourpId, DatabaseSet dbset) throws Exception {
        DasGroup dasGroup = groupDao.getDalGroupById(gourpId);
        return dbSetConfiguration.getAllCheckData(user, dasGroup, dbset);
    }
}
