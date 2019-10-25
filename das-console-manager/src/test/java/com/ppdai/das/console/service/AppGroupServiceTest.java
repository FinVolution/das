package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.*;
import com.ppdai.das.console.service.AppGroupService;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.dto.entry.das.AppGroup;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.AppGroupView;
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
@SpringBootTest(classes = {AppGroupService.class, Message.class, AppGroupDao.class, ProjectDao.class, PermissionService.class, DeleteCheckDao.class, LoginUserDao.class, PermissionDao.class})
public class AppGroupServiceTest {

    @Mock
    private AppGroupService appGroupService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup dasGroup = DasGroup.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(dasGroup, "dasGroup", true, 256);
        when(appGroupService.validatePermision(user, errors)).thenReturn(chain);


        ListResult<AppGroupView> listResult = new ListResult<>();
        List<AppGroupView> list = Lists.newArrayList(AppGroupView.builder().id(1).name("tom").build());
        listResult.setList(list);
        Paging<AppGroup> paging = new Paging<>();
        paging.setData(new AppGroup());
        when(appGroupService.findProjectGroupPageList(paging)).thenReturn(listResult);

        AppGroup appGroup = AppGroup.builder().id(1L).build();
        when(appGroupService.insertAppGroup(appGroup)).thenReturn(ServiceResult.success());

        when(appGroupService.deleteCheck(1L)).thenReturn(ServiceResult.success());

        when(appGroupService.deleteAppGroup(appGroup)).thenReturn(ServiceResult.success());

        when(appGroupService.updateAppGroup(appGroup)).thenReturn(ServiceResult.success());

        when(appGroupService.isNotExistByName(appGroup)).thenReturn(true);
    }

    @Test
    public void validatePermisionTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup dasGroup = DasGroup.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(dasGroup, "dasGroup", true, 256);
        ValidatorChain chain = appGroupService.validatePermision(user, errors);
        Assert.assertTrue(chain.validate().isValid());
    }

    @Test
    public void findProjectGroupPageListTest() throws SQLException {
        Paging<AppGroup> paging = new Paging<>();
        paging.setData(new AppGroup());
        ListResult<AppGroupView> listResult = appGroupService.findProjectGroupPageList(paging);
        Assert.assertTrue(listResult.getList().size() > 0);
    }

    @Test
    public void insertAppGroupTest() throws SQLException {
        AppGroup appGroup = AppGroup.builder().id(1L).build();
        ServiceResult<String> sr = appGroupService.insertAppGroup(appGroup);
        Assert.assertTrue(sr.getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteCheckTest() throws SQLException {
        ServiceResult sr = appGroupService.deleteCheck(1L);
        Assert.assertTrue(sr.getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteAppGroupTest() throws SQLException {
        AppGroup appGroup = AppGroup.builder().id(1L).build();
        ServiceResult<String> sr = appGroupService.deleteAppGroup(appGroup);
        Assert.assertTrue(sr.getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void updateAppGroupTest() throws SQLException {
        AppGroup appGroup = AppGroup.builder().id(1L).build();
        ServiceResult<String> sr = appGroupService.deleteAppGroup(appGroup);
        Assert.assertTrue(sr.getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        AppGroup appGroup = AppGroup.builder().id(1L).build();
        Assert.assertTrue(appGroupService.isNotExistByName(appGroup));
    }

}
