package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.*;
import com.ppdai.das.console.service.GroupService;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.DalGroupView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, GroupDao.class, Consts.class, UserGroupDao.class, ProjectDao.class, PermissionService.class, PermissionDao.class, Message.class})
public class GroupServiceTest {

    @Mock
    private GroupService groupService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup group = DasGroup.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(group, "group", true, 256);
        when(groupService.validatePermision(user, errors)).thenReturn(chain);

        when(groupService.addDalGroup(user, group)).thenReturn(true);

        when(groupService.updateDalGroup(user, group)).thenReturn(true);

        when(groupService.isInSuperGroup("1")).thenReturn(true);

        when(groupService.isInCurrentGroup(1L, 1)).thenReturn(true);

        ListResult<DalGroupView> listResult = new ListResult<>();
        List<DalGroupView> list = Lists.newArrayList(DalGroupView.builder().id(1L).group_name("tom").build());
        listResult.setList(list);
        Paging<DasGroup> paging = new Paging<>();
        paging.setData(new DasGroup());
        when(groupService.findGroupPageList(paging)).thenReturn(listResult);

        when(groupService.isNotExistByName(group)).thenReturn(true);

        when(groupService.isNotExistInProjectAndGroup("name")).thenReturn(true);
    }

    @Test
    public void validatePermisionTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup group = DasGroup.builder().group_name("name").build();
        Errors errors = new BeanPropertyBindingResult(group, "group", true, 256);
        Assert.assertTrue(groupService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void addDalGroupTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup group = DasGroup.builder().group_name("name").build();
        Assert.assertTrue(groupService.addDalGroup(user, group));
    }

    @Test
    public void updateDalGroupTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        DasGroup group = DasGroup.builder().group_name("name").build();
        Assert.assertTrue(groupService.updateDalGroup(user, group));
    }

    @Test
    public void isInSuperGroupTest() throws Exception {
        Assert.assertTrue(groupService.isInCurrentGroup(1L, 1));
    }

    @Test
    public void isInCurrentGroupTest() throws Exception {
        Assert.assertTrue(groupService.isInSuperGroup("1"));
    }

    @Test
    public void findGroupPageListTest() throws Exception {
        Paging<DasGroup> paging = new Paging<>();
        paging.setData(new DasGroup());
        Assert.assertTrue(groupService.findGroupPageList(paging).getList().size() > 0);
    }

    @Test
    public void isNotExistByNameTest() throws Exception {
        DasGroup group = DasGroup.builder().group_name("name").build();
        Assert.assertTrue(groupService.isNotExistByName(group));
    }

    @Test
    public void isNotExistInProjectAndGroupTest() throws Exception {
        Assert.assertTrue(groupService.isNotExistInProjectAndGroup("name"));
    }
}
