package com.ppdai.platform.das.console.controller;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.common.codeGen.utils.DbUtils;
import com.ppdai.platform.das.console.common.configCenter.ConfigCheckBase;
import com.ppdai.platform.das.console.common.configCenter.ConfigCkeckResult;
import com.ppdai.platform.das.console.common.validates.chain.ValidateResult;
import com.ppdai.platform.das.console.common.validates.group.db.AddDataBase;
import com.ppdai.platform.das.console.common.validates.group.db.DeleteDataBase;
import com.ppdai.platform.das.console.common.validates.group.db.UpdateDataBase;
import com.ppdai.platform.das.console.common.validates.sql.SQLValidateResult;
import com.ppdai.platform.das.console.common.validates.sql.SQLValidation;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.constant.Consts;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dao.GroupDao;
import com.ppdai.platform.das.console.dto.entry.configCheck.ConfigDataResponse;
import com.ppdai.platform.das.console.dto.entry.das.DasGroup;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.ConnectionRequest;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.SqlValidateRequest;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.view.DataBaseView;
import com.ppdai.platform.das.console.dto.view.SqlValidateView;
import com.ppdai.platform.das.console.dto.view.tabStruct.TableStructure;
import com.ppdai.platform.das.console.service.DatabaseService;
import com.ppdai.platform.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/db")
public class DatabaseController {

    @Autowired
    private Consts consts;

    @Resource
    private Message message;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PermissionService permissionService;

    /**
     * 1、根据groupid获取数据库列表,翻页
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DataBaseView>> list(@RequestBody Paging<DataBaseInfo> paging, @CurrentUser LoginUser loginUser) throws SQLException {
        return ServiceResult.success(databaseService.findDbPageList(paging));
    }

    @RequestMapping(value = "/page/list", method = RequestMethod.POST)
    public ServiceResult<ListResult<DataBaseView>> getDBListByGroupId(@RequestBody Paging<DataBaseInfo> paging, @CurrentUser LoginUser loginUser) throws SQLException {
        if (permissionService.isManagerById(loginUser.getId())) {
            return ServiceResult.success(databaseService.findDbPageList(paging));
        }
        return ServiceResult.success(databaseService.findDbPageListByUserId(paging, loginUser.getId()));
    }

    /**
     * 获取物理库treeSelect数据表结构数据
     *
     * @param connectionRequest
     * @return
     */
    @RequestMapping(value = "/connectionTest", method = RequestMethod.POST)
    public ServiceResult connectionTest(@RequestBody ConnectionRequest connectionRequest) {
        return databaseService.connectionTest(connectionRequest);
    }

    /**
     * 获取物理库的结构
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getTableAttributes")
    public ServiceResult getTableAttributes(@RequestParam(value = "id", defaultValue = "0") Long id, @RequestParam(value = "name", defaultValue = "") String name) throws Exception {
        if (id == null || name == null) {
            return ServiceResult.fail("参数为空错误！");
        }
        TableStructure tableAttributes = DbUtils.getTableAttributes(id, name);
        return ServiceResult.success(tableAttributes);
    }

    /**
     * 判断物理库标识符是否存在
     *
     * @param db_names
     * @return
     */
    @RequestMapping(value = "/isExist", method = RequestMethod.POST)
    public ServiceResult isExist(@RequestBody List<String> db_names) throws SQLException {
        return databaseService.isExist(db_names);
    }

    /**
     * 模糊查询
     */
    @RequestMapping(value = "/dbs")
    public ServiceResult<List<DataBaseInfo>> getUsers(@RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "groupId", defaultValue = "") Long groupId) throws SQLException {
        if (StringUtils.isNotBlank(name)) {
            return ServiceResult.success(dataBaseDao.getDatabaseListByLikeName(name));
        } else if (null != groupId) {
            return ServiceResult.success(dataBaseDao.getGroupDBsByGroup(groupId));
        }
        return ServiceResult.fail(Collections.emptyList());
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        return addDataBase(dataBaseInfo, user, errors);
    }

    /**
     * 批量添加物理库,阿波罗已存在则覆盖
     */
    @RequestMapping(value = "/addDbs", method = RequestMethod.POST)
    public ServiceResult<String> addDbs(@RequestBody List<DataBaseInfo> list, @CurrentUser LoginUser user, Errors errors) throws Exception {
        ValidateResult validateRes = databaseService.validatePermision(user, errors)
                .addAssert(() -> databaseService.encryptAndOptUser(user, list))
                .addAssert(() -> databaseService.addDataCenter(user, list))
                .addAssert(() -> databaseService.addDataBaseList(list)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ServiceResult<String> update(@Validated(UpdateDataBase.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dataBaseInfo.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = databaseService.validatePermision(user, errors)
                .addAssert(() -> databaseService.isNotExistByName(dataBaseInfo), dataBaseInfo.getDbname() + " 已存在！")
                .addAssert(() -> databaseService.updateDBInfo(dataBaseInfo), message.db_message_update_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseService.updateDataCenter(user, dataBaseInfo);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ServiceResult<String> delete(@Validated(DeleteDataBase.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dataBaseInfo.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = databaseService.validatePermision(user, errors)
                .addAssert(() -> databaseService.deleteCheck(dataBaseInfo.getId()))
                .addAssert(() -> databaseService.deleteDataCenter(user, dataBaseInfo))
                .addAssert(() -> dataBaseDao.deleteDataBaseInfo(dataBaseInfo.getId()) > 0, message.db_message_delete_operation_failed).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return ServiceResult.success();
    }

    @RequestMapping(value = "/catalogs")
    public ServiceResult<List<String>> getDBCatalogs(@RequestParam("dbset_id") Long dbsetId) throws Exception {
        return databaseService.getDBCatalogs(dbsetId);
    }

    @RequestMapping(value = "/catalogsByDbId")
    public ServiceResult<List<String>> getDBCatalogsByDbId(@RequestParam("dbId") Long dbId) throws Exception {
        return databaseService.getDBCatalogsByDbId(dbId);
    }

    /**
     * 检验sql正确性
     */
    @RequestMapping(value = "/sqlValidate", method = RequestMethod.POST)
    public ServiceResult validateSQL(@RequestBody SqlValidateRequest sqlValidateRequest) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getMasterCologByDatabaseSetId(sqlValidateRequest.getDbset_id());
        String values[] = {};
        int sqlTypes[] = {};
        SQLValidateResult validResult = SQLValidation.queryValidate(dataBaseInfo.getId(), sqlValidateRequest.getSql_content(), sqlTypes, values);
        if (validResult != null && validResult.isPassed()) {
            return ServiceResult.success(SqlValidateView.builder()
                    .dbType(validResult.getDbType())
                    .rows(validResult.getAffectRows())
                    .msg(validResult.getMessage())
                    .build());
        } else {
            return ServiceResult.fail(validResult.getMessage());
        }
    }

    /**
     * 同步数据到阿波罗
     */
    @RequestMapping(value = "/sync")
    public ServiceResult<String> sync(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(id);
        return databaseService.syncDataCenter(user, dataBaseInfo);
    }

    @RequestMapping(value = "/getdbnames")
    public ServiceResult<String> getdbnames(@RequestParam("appid") String appid) throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getAllDbByAppId(appid);
        String names = Joiner.on(",").skipNulls().join(list.stream().map(s -> s.getDb_catalog()).collect(Collectors.toSet()));
        return ServiceResult.success(names);
    }

    /**
     * 获取带前缀的dbname
     */
/*
    @RequestMapping(value = "/getApolloDbnames")
    public ServiceResult<String> getApolloDbnames(@RequestParam("appid") Long appid) throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getAllDbByAppId(appid);
        String names = Joiner.on(",").skipNulls().join(list.stream().map(s -> ApolloConfigEnums.getDataBaseNameSpaceByType(s.getDb_type()).getNameSpace() + s.getDbname()).collect(Collectors.toList()));
        return ServiceResult.success(names);
    }
*/

    /**
     * 数据对比
     */
    @RequestMapping(value = "/check")
    public ConfigCkeckResult check(@RequestParam("id") Long id, @CurrentUser LoginUser user) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(id);
        ConfigCkeckResult<List<ConfigDataResponse>> sr = databaseService.getCheckData(user, dataBaseInfo);
        if (sr.getCode() == ConfigCkeckResult.ERROR) {
            return sr;
        }
        return ConfigCheckBase.checkData(sr.getItem());
    }

    /**
     * 获取物理库配置缺省实现
     */
 /*    @RequestMapping(value = "/config")
   public ServiceResult getConfig(@RequestParam("name") String name) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDatabaseByName(name);
        ConfigDataResponse configDataResponse = apolloDatabase.getDasResponse(dataBaseInfo);
        return ServiceResult.success(configDataResponse);
    }*/

    /**
     * 同步数据到db
     */
    @RequestMapping(value = "/syncdb")
    public ServiceResult<String> syncdb(@Validated(AddDataBase.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        DasGroup dasGroup = groupDao.getGroupByName(dataBaseInfo.getGroup_name());
        if (dasGroup == null) {
            return ServiceResult.fail("请先同步组！" + dataBaseInfo.getGroup_name());
        }
        dataBaseInfo.setDal_group_id(dasGroup.getId());
        return addDataBase(dataBaseInfo, user, errors);
    }

    private ServiceResult<String> addDataBase(@Validated(AddDataBase.class) @RequestBody DataBaseInfo dataBaseInfo, @CurrentUser LoginUser user, Errors errors) throws Exception {
        dataBaseInfo.setUpdateUserNo(user.getUserNo());
        ValidateResult validateRes = databaseService.validatePermision(user, errors)
                .addAssert(() -> dataBaseDao.getCountByName(dataBaseInfo.getDbname()) == 0, dataBaseInfo.getDbname() + "已经存在!")
                .addAssert(() -> databaseService.addDataBaseInfo(user, dataBaseInfo)).validate();
        if (!validateRes.isValid()) {
            return ServiceResult.fail(validateRes.getSummarize());
        }
        return databaseService.addDataCenter(user, Lists.newArrayList(dataBaseInfo));
    }

    @RequestMapping("/buttons")
    public ServiceResult getDataBaseButton(@CurrentUser LoginUser user) {
        return databaseService.getDataBaseButton(user);
    }
}
