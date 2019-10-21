package com.ppdai.platform.das.codegen.service;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.*;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, PermissionDao.class, DatabaseSetDao.class, ProjectDao.class, Message.class, PermissionService.class, DeleteCheckDao.class})
public class DatabaseSetServiceTest {

    @Mock
    private DatabaseSetService databaseSetService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        DatabaseSet dbset = DatabaseSet.builder().name("name").build();
        Errors errors = new BeanPropertyBindingResult(dbset, "dbset", true, 256);
        when(databaseSetService.validatePermision(user, errors)).thenReturn(chain);

        when(databaseSetService.insertDatabaseSet(dbset)).thenReturn(true);

        when(databaseSetService.updateDatabaseSet(dbset)).thenReturn(1);

        when(databaseSetService.deleteCheck(1L)).thenReturn(ServiceResult.success());

        when(databaseSetService.deleteDatabaseSet(1L)).thenReturn(true);

        ListResult<DatabaseSetView> listResult = new ListResult<>();
        List<DatabaseSetView> list = Lists.newArrayList(DatabaseSetView.builder().id(1L).groupName("tom").build());
        listResult.setList(list);
        Paging<DatabaseSet> paging = new Paging<>();
        paging.setData(new DatabaseSet());
        when(databaseSetService.findDbSetPageList(paging)).thenReturn(listResult);

        List<DatabaseSet> list1 = Lists.newArrayList(DatabaseSet.builder().id(1L).groupName("tom").build());
        when(databaseSetService.getAllDatabaseSetByProjectId(1L)).thenReturn(list1);

        when(databaseSetService.getAllDatabaseSetByAppId("1")).thenReturn(list1);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        DatabaseSet dbset = DatabaseSet.builder().name("name").build();
        Errors errors = new BeanPropertyBindingResult(dbset, "dbset", true, 256);
        Assert.assertTrue(databaseSetService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void insertDatabaseSetTest() throws SQLException {
        DatabaseSet dbset = DatabaseSet.builder().name("name").build();
        Assert.assertTrue(databaseSetService.insertDatabaseSet(dbset));
    }

    @Test
    public void updateDatabaseSetTest() throws SQLException {
        DatabaseSet dbset = DatabaseSet.builder().name("name").build();
        Assert.assertTrue(databaseSetService.updateDatabaseSet(dbset) > 0);
    }

    @Test
    public void deleteCheckTest() throws SQLException {
        Assert.assertTrue(databaseSetService.deleteCheck(1L).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteDatabaseSetTest() throws SQLException {
        Assert.assertTrue(databaseSetService.deleteDatabaseSet(1L));
    }

    @Test
    public void findDbSetPageListTest() throws SQLException {
        Paging<DatabaseSet> paging = new Paging<>();
        paging.setData(new DatabaseSet());
        Assert.assertTrue(databaseSetService.findDbSetPageList(paging).getList().size() > 0);
    }

    @Test
    public void getAllDatabaseSetByProjectIdTest() throws SQLException {
        Assert.assertTrue(databaseSetService.getAllDatabaseSetByProjectId(1L).size() > 0);
    }

    @Test
    public void getAllDatabaseSetByAppIdTest() throws SQLException {
        Assert.assertTrue(databaseSetService.getAllDatabaseSetByAppId("1").size() > 0);
    }
}
