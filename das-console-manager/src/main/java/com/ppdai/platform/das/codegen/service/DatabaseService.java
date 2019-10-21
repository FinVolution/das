package com.ppdai.platform.das.codegen.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ppdai.platform.das.codegen.api.DataBaseConfiguration;
import com.ppdai.platform.das.codegen.api.DefaultConfiguration;
import com.ppdai.platform.das.codegen.common.codeGen.utils.DbUtils;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.codegen.common.exceptions.TransactionException;
import com.ppdai.platform.das.codegen.common.utils.DasEnv;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.*;
import com.ppdai.platform.das.codegen.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.codegen.dto.entry.das.*;
import com.ppdai.platform.das.codegen.dto.model.ConnectionRequest;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.model.page.PagerUtil;
import com.ppdai.platform.das.codegen.dto.view.DataBaseView;
import com.ppdai.platform.das.codegen.dto.view.treeSelect.TreeNodeView;
import com.ppdai.platform.das.codegen.dto.view.treeSelect.TreeSelectView;
import com.ppdai.platform.das.codegen.enums.DbMasterSlaveEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    @Autowired
    private Message message;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private DataBaseSetEntryDao dataBaseSetEntryDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SetupDataBaseService setupDataBaseService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    @Autowired
    private DataBaseConfiguration dataBaseConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    /**
     * 5 - 数据库一览 物理数据增删改
     * 删改权限：1）判断user是否在管理员组
     */
    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.group_message_no_pemission);
    }

    public ServiceResult isExist(List<String> db_names) throws SQLException {
        List<String> list = dataBaseDao.getAllDbAllinOneNames();
        Set<String> set1 = list.stream().map(i -> i.toLowerCase()).collect(Collectors.toSet());
        Set<String> set2 = db_names.stream().map(i -> i.toLowerCase()).collect(Collectors.toSet());
        SetView<String> intersection = Sets.intersection(set1, set2);
        if (!intersection.isEmpty()) {
            return ServiceResult.success(intersection);
        }
        return ServiceResult.fail(set2);
    }

    /**
     * 1）、添加物理库批准信息groupDb
     * 2）、生成默认的databaseSet
     */
    public ServiceResult<String> addDataBaseInfo(LoginUser user, DataBaseInfo dataBaseInfo) throws SQLException {
        // replace to current User's group
        if (dataBaseInfo.isAddToGroup()) {
            if (null == dataBaseInfo.getDal_group_id()) {
                List<UserGroup> list = userGroupDao.getUserGroupByUserId(user.getId());
                if (CollectionUtils.isNotEmpty(list)) {
                    dataBaseInfo.setDal_group_id(list.get(0).getGroup_id());
                }
            }
        }
        dataBaseInfo.setDb_password(DasEnv.encdecConfiguration.encrypt(dataBaseInfo.getDb_password()));
        Long dbId = dataBaseDao.insertDataBaseInfo(dataBaseInfo);
        if (dbId <= 0) {
            return ServiceResult.fail();
        }

        dataBaseInfo.setId(dbId);
        return genDefaultDbsetAndEntry(dataBaseInfo);
    }

    public ServiceResult<String> genDefaultDbsetAndEntry(DataBaseInfo dataBaseInfo) throws SQLException {
        if (dataBaseInfo.isGenDefault()) {
            dataBaseInfo.setDb_password(DasEnv.encdecConfiguration.encrypt(dataBaseInfo.getDb_password()));
            return genDefaultDbsetAndEntry(dataBaseInfo.getDal_group_id(), dataBaseInfo.getId(), dataBaseInfo.getDbname(), dataBaseInfo.getDb_type());
        }
        return ServiceResult.success();
    }

    /**
     * 生成默认的databaseSet和databaseSet Entry
     */
    public ServiceResult<String> genDefaultDbsetAndEntry(Long groupId, Long dbId, String dbname, Integer dbtype) throws SQLException {
        List<DatabaseSet> exist = databaseSetDao.getAllDatabaseSetByName(dbname);
        if (exist != null && exist.size() > 0) {
            return ServiceResult.fail("数据库" + dbname + "已添加成功。由于已存在名为" + dbname + "的逻辑数据库，所以无法默认生成同名的逻辑库，请到逻辑数据库管理页面中手动添加不同名称的逻辑库。请点击关闭按钮以关闭窗口。");
        }
        boolean isSussess = dataBaseDao.getDasClient().execute(() -> {
            Long dbsetId = databaseSetDao.insertDatabaseSet(DatabaseSet.builder()
                    .name(dbname)
                    .groupId(groupId)
                    .dbType(dbtype)
                    .build());

            if (dbsetId <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            if (dataBaseSetEntryDao.insertDatabaseSetEntry(DatabaseSetEntry.builder()
                    .dbset_id(dbsetId)
                    .db_Id(dbId)
                    .databaseType(DbMasterSlaveEnum.MASTER.getType())
                    .name(dbname)
                    .build()) <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            return true;
        });

        if (isSussess) {
            return ServiceResult.success();
        }
        return ServiceResult.fail("生成默认的databaseSet和databaseSet Entry" + message.db_message_add_operation_failed);
    }

    public boolean isNotExistByName(DataBaseInfo dataBaseInfo) throws SQLException {
        Long n = dataBaseDao.getCountByName(dataBaseInfo.getDbname());
        Long i = dataBaseDao.getCountByIdAndName(dataBaseInfo.getId(), dataBaseInfo.getDbname());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public boolean updateDBInfo(DataBaseInfo dataBaseInfo) throws SQLException {
        DataBaseInfo _dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(dataBaseInfo.getId());
        dataBaseInfo.setDbname(_dataBaseInfo.getDbname());
        dataBaseInfo.setDb_password(DasEnv.encdecConfiguration.encrypt(dataBaseInfo.getDb_password()));
        return dataBaseDao.updateDataBaseInfo(dataBaseInfo) > 0;
    }

    public ListResult<DataBaseView> findDbPageList(Paging<DataBaseInfo> paging) throws SQLException {
        Long count = dataBaseDao.getTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DataBaseView> list = dataBaseDao.findDbPageList(paging);
            for (DataBaseView dataBaseView : list) {
                dataBaseView.setDb_password(DasEnv.encdecConfiguration.decrypt(dataBaseView.getDb_password()));
            }
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public ListResult<DataBaseView> findDbPageListByUserId(Paging<DataBaseInfo> paging, Long userId) throws SQLException {
        Long count = dataBaseDao.getTotalCountByUserId(paging, userId);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DataBaseView> list = dataBaseDao.findDbPageListByUserId(paging, userId);
            for (DataBaseView dataBaseView : list) {
                dataBaseView.setDb_password(DasEnv.encdecConfiguration.decrypt(dataBaseView.getDb_password()));
            }
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    /**
     * 物理库密码加密，添加操作用户
     */
    public ServiceResult encryptAndOptUser(LoginUser user, List<DataBaseInfo> dBList) {
        for (DataBaseInfo dataBaseInfo : dBList) {
            dataBaseInfo.setDb_password(DasEnv.encdecConfiguration.encrypt(dataBaseInfo.getDb_password()));
            dataBaseInfo.setUpdateUserNo(user.getUserNo());
        }
        return ServiceResult.success();
    }

    /**
     * 批量添加物理库
     */
    public ServiceResult<String> addDataBaseList(List<DataBaseInfo> dBList) {
        try {
            for (DataBaseInfo item : dBList) {
                String dbname = item.getDbname().trim();
                if (dbname.length() > 24) {
                    return ServiceResult.fail(dbname + "物理库名称过长，不大于24个字符");
                }
                if (!item.isAddToGroup()) {
                    item.setDal_group_id(null);
                }
                item.setDbname(dbname);
                item.setDb_catalog(item.getDb_catalog().trim());
            }
            List<String> dbNames = dBList.stream().map(i -> i.getDbname()).collect(Collectors.toList());
            List<DataBaseInfo> dalGroupDBList = dataBaseDao.getAllDbsByDbNames(dbNames);
            if (!CollectionUtils.isEmpty(dalGroupDBList)) {
                String existDbs = Joiner.on(",").join(dalGroupDBList.stream().map(i -> i.getDbname()).collect(Collectors.toList()));
                return ServiceResult.fail(existDbs);
            }
            boolean isSussess = dataBaseDao.getDasClient().execute(() -> {
                int[] ids = dataBaseDao.insertDatabaselist(dBList);
                if (ids.length <= 0) {
                    throw new TransactionException(message.db_message_add_operation_failed);
                }
                return true;
            });

            if (isSussess) {
                return ServiceResult.success();
            }
        } catch (Exception e) {
            return ServiceResult.fail("批量添加 addDataBaseList " + StringUtil.getMessage(e));
        }
        return ServiceResult.fail("批量添加 addDataBaseList " + message.db_message_add_operation_failed);
    }

    public ServiceResult<List<String>> getDBCatalogs(Long dbsetId) throws Exception {
        if (null == dbsetId) {
            return ServiceResult.success(ListUtils.EMPTY_LIST);
        }
        DataBaseInfo dataBaseInfo = dataBaseDao.getMasterCologByDatabaseSetId(dbsetId);
        if (null == dataBaseInfo) {
            return ServiceResult.fail("未找到对应的表,请添加表关联！！");
        }
        return getDBCatalogsByDbId(dataBaseInfo.getId());
    }

    public ServiceResult<List<String>> getDBCatalogsByDbId(Long dbid) throws Exception {
        List<String> tables = DbUtils.getAllTableNames(dbid);
        Collections.sort(tables, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        return ServiceResult.success(tables);
    }

    public ServiceResult connectionTest(ConnectionRequest connectionRequest) {
        TreeSelectView treeSelectView = TreeSelectView.builder().key("-1").label(connectionRequest.getDb_address()).value("-1").build();
        List<TreeNodeView> children = new ArrayList<>();
        ServiceResult<Set<String>> sr = setupDataBaseService.connectionTest(connectionRequest);
        if (sr.getCode() == ServiceResult.SUCCESS) {
            for (String dbname : sr.getMsg()) {
                children.add(new TreeNodeView(dbname, dbname, dbname));
            }
            treeSelectView.setChildren(children);
            return ServiceResult.success(treeSelectView);
        }
        return sr;
    }

    public ServiceResult deleteCheck(Long dbId) throws SQLException {
        if (deleteCheckDao.isDbsetEntryIdInProject(dbId)) {
            return ServiceResult.fail("请先删除逻辑库映射和物理库的关联！");
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> addDataCenter(LoginUser user, List<DataBaseInfo> list) {
        try {
            dataBaseConfiguration.batchAddDataBase(user, list);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> updateDataCenter(LoginUser user, DataBaseInfo dataBaseInfo) {
        try {
            dataBaseConfiguration.updateDataBase(user, dataBaseInfo);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> deleteDataCenter(LoginUser user, DataBaseInfo dataBaseInfo) {
        try {
            dataBaseConfiguration.deleteDataBase(user, dataBaseInfo);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ServiceResult<String> syncDataCenter(LoginUser user, DataBaseInfo dataBaseInfo) {
        try {
            dataBaseConfiguration.syncDataBase(user, dataBaseInfo);
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
        return ServiceResult.success();
    }

    public ConfigCkeckResult<List<ConfigDataResponse>> getCheckData(LoginUser user, DataBaseInfo dataBaseInfo) {
        try {
            List<ConfigDataResponse> list = dataBaseConfiguration.getCheckData(user, dataBaseInfo);
            return ConfigCkeckResult.success("success", list);
        } catch (Exception e) {
            return ConfigCkeckResult.fail(StringUtil.getMessage(e), ListUtils.EMPTY_LIST);
        }
    }

    public ServiceResult getDataBaseButton(LoginUser user) {
        return ServiceResult.success(defaultConfiguration.getDataBaseButton(user));
    }
}
