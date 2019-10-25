package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dao.PermissionDao;
import com.ppdai.das.console.dao.UserGroupDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.UserGroup;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.MemberView;
import com.ppdai.das.console.service.GroupMemberService;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.UserService;
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
@SpringBootTest(classes = {LoginUserDao.class, PermissionDao.class, Consts.class, UserService.class, UserGroupDao.class, PermissionService.class, Message.class})
public class GroupMemberServiceTest {

    @Mock
    private GroupMemberService groupMemberService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        UserGroup userGroup = UserGroup.builder().update_user_no("name").build();
        Errors errors = new BeanPropertyBindingResult(userGroup, "userGroup", true, 256);
        when(groupMemberService.validatePermisionAdd(user, userGroup, errors)).thenReturn(chain);

        when(groupMemberService.validatePermisionUpdateAndDelete(user, userGroup, errors)).thenReturn(chain);

        when(groupMemberService.canOpterateGroupMember(1L, 1L)).thenReturn(true);

        when(groupMemberService.isUserInGroup(1L, 1L)).thenReturn(true);

        ListResult<MemberView> listResult = new ListResult<>();
        List<MemberView> list = Lists.newArrayList(MemberView.builder().group_id(1L).group_id(1L).build());
        listResult.setList(list);
        Paging<UserGroup> paging = new Paging<>();
        paging.setData(new UserGroup());
        when(groupMemberService.findGroupMemberPageList(paging)).thenReturn(listResult);
    }

    @Test
    public void validatePermisionAddTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        UserGroup userGroup = UserGroup.builder().update_user_no("name").build();
        Errors errors = new BeanPropertyBindingResult(userGroup, "userGroup", true, 256);
        Assert.assertTrue(groupMemberService.validatePermisionAdd(user, userGroup, errors).validate().isValid());
    }

    @Test
    public void validatePermisionUpdateAndDeleteTest() throws Exception {
        LoginUser user = LoginUser.builder().id(1L).build();
        UserGroup userGroup = UserGroup.builder().update_user_no("name").build();
        Errors errors = new BeanPropertyBindingResult(userGroup, "userGroup", true, 256);
        Assert.assertTrue(groupMemberService.validatePermisionUpdateAndDelete(user, userGroup, errors).validate().isValid());
    }

    @Test
    public void canOpterateGroupMemberTest() throws Exception {
        Assert.assertTrue(groupMemberService.canOpterateGroupMember(1L, 1L));
    }

    @Test
    public void isUserInGroupTest() throws Exception {
        Assert.assertTrue(groupMemberService.isUserInGroup(1L, 1L));
    }

    @Test
    public void findGroupMemberPageListTest() throws Exception {
        Paging<UserGroup> paging = new Paging<>();
        paging.setData(new UserGroup());
        Assert.assertTrue(groupMemberService.findGroupMemberPageList(paging).getList().size() > 0);
    }
}
