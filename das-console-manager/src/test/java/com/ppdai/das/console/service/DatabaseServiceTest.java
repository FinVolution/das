package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.*;
import com.ppdai.das.console.service.DatabaseService;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.DataBaseView;
import org.apache.commons.collections.ListUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PermissionDao.class, LoginUserDao.class, Message.class, UserGroupDao.class, DataBaseDao.class, DatabaseSetDao.class, DataBaseSetEntryDao.class, PermissionService.class, DeleteCheckDao.class})
public class DatabaseServiceTest {

    @Mock
    private DatabaseService databaseService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(groupDb, "groupDb", true, 256);
        when(databaseService.validatePermision(user, errors)).thenReturn(chain);

        when(databaseService.addDataBaseInfo(user, groupDb)).thenReturn(ServiceResult.success());

        when(databaseService.genDefaultDbsetAndEntry(groupDb)).thenReturn(ServiceResult.success());

        when(databaseService.genDefaultDbsetAndEntry(1L, 1L, "db", 1)).thenReturn(ServiceResult.success());

        when(databaseService.isNotExistByName(groupDb)).thenReturn(true);

        when(databaseService.updateDBInfo(groupDb)).thenReturn(true);

        ListResult<DataBaseView> listResult = new ListResult<>();
        List<DataBaseView> list = Lists.newArrayList(DataBaseView.builder().id(1).dbname("tom").build());
        listResult.setList(list);
        Paging<DataBaseInfo> paging = new Paging<>();
        paging.setData(new DataBaseInfo());
        when(databaseService.findDbPageList(paging)).thenReturn(listResult);

        List<DataBaseInfo> dBList = Lists.newArrayList(DataBaseInfo.builder().id(1L).dbname("tom").build());
        when(databaseService.encryptAndOptUser(user, dBList)).thenReturn(ServiceResult.success());

        when(databaseService.addDataBaseList(dBList)).thenReturn(ServiceResult.success());

        when(databaseService.getDBCatalogs(1L)).thenReturn(ServiceResult.success(ListUtils.EMPTY_LIST));

        when(databaseService.deleteCheck(1L)).thenReturn(ServiceResult.success());

    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(groupDb, "groupDb", true, 256);
        Assert.assertTrue(databaseService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void addDalGroupDBTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Assert.assertTrue(databaseService.addDataBaseInfo(user, groupDb).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void genDefaultDbsetAndEntryTest() throws SQLException {
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Assert.assertTrue(databaseService.genDefaultDbsetAndEntry(groupDb).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void genDefaultDbsetAndEntryTest2() throws SQLException {
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Assert.assertTrue(databaseService.genDefaultDbsetAndEntry(1L, 1L, "db", 1).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Assert.assertTrue(databaseService.isNotExistByName(groupDb));
    }

    @Test
    public void updateGroupDBTest() throws SQLException {
        DataBaseInfo groupDb = DataBaseInfo.builder().group_name("name").build();
        Assert.assertTrue(databaseService.updateDBInfo(groupDb));
    }

    @Test
    public void findDbPageListTest() throws SQLException {
        ListResult<DataBaseView> listResult = new ListResult<>();
        List<DataBaseView> list = Lists.newArrayList(DataBaseView.builder().id(1).dbname("tom").build());
        listResult.setList(list);
        Paging<DataBaseInfo> paging = new Paging<>();
        paging.setData(new DataBaseInfo());
        Assert.assertTrue(databaseService.findDbPageList(paging).getList().size() == 1);

    }

    @Test
    public void encryptAndOptUserTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        List<DataBaseInfo> dBList = Lists.newArrayList(DataBaseInfo.builder().id(1L).dbname("tom").build());
        Assert.assertTrue(databaseService.encryptAndOptUser(user, dBList).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void getDBCatalogsTest() throws Exception {
        Assert.assertTrue(databaseService.getDBCatalogs(1L).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteCheckTest() throws SQLException {
        Assert.assertTrue(databaseService.deleteCheck(1L).getCode() == ServiceResult.SUCCESS);
    }
}
