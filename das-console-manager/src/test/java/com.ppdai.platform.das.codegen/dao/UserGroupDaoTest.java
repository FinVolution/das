package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.UserGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.MemberView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserGroupDao.class})
public class UserGroupDaoTest {

    @Autowired
    UserGroupDao userGroupDao;

    UserGroup userProject;

    UserGroup userGroupModel;

    Paging<UserGroup> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        userProject = UserGroup.builder().user_id(1L).group_id(1L).role(1).opt_user(1).build();
        userGroupModel = UserGroup.builder().userNo("011158").build();
        paging.setData(userGroupModel);
    }

    @Test
    public void insertUserGroup() throws Exception {
        Long id = userGroupDao.insertUserGroup(userProject);
        System.out.println("insertUserGroup " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertUserGroupTest() throws Exception {
        Long id = userGroupDao.insertUserGroup(1L, 1L, 1, 2);
        System.out.println("insertUserGroup " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void deleteUserFromGroup() throws Exception {
        int count = userGroupDao.deleteUserFromGroup(1L, 1L);
        System.out.println("deleteUserFromGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateUserPersimion() throws Exception {
        UserGroup userGroup = UserGroup.builder().user_id(1L).opt_user(4).role(1).group_id(1L).build();
        int count = userGroupDao.updateUserPersimion(userGroup);
        System.out.println("updateUserPersimion :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateUserPersimionTest() throws Exception {
        int count = userGroupDao.updateUserPersimion(1L, 1L, 3, 3);
        System.out.println("updateUserPersimion :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getUserListByLikeUserName() throws Exception {
        List<MemberView> lsit = userGroupDao.getUserListByLikeUserName("yu");
        System.out.println("getUserListByLikeUserName :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }

    @Test
    public void getUserGroupByUserId() throws Exception {
        List<UserGroup> lsit = userGroupDao.getUserGroupByUserId(1L);
        System.out.println("getUserGroupByUserId :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }

    @Test
    public void getUserGroupByGroupId() throws Exception {
        List<UserGroup> lsit = userGroupDao.getUserGroupByGroupId(1L);
        System.out.println("getUserGroupByGroupId :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }

    @Test
    public void getUserGroupByGroupIdAndUserId() throws Exception {
        List<UserGroup> lsit = userGroupDao.getUserGroupByGroupIdAndUserId(1L, 1L);
        System.out.println("getUserGroupByGroupIdAndUserId :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }

    @Test
    public void getMemberTotalCount() throws Exception {
        Long count = userGroupDao.getMemberTotalCount(paging);
        System.out.println("getMemberTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findMemberPageList() throws Exception {
        List<MemberView> lsit = userGroupDao.findMemberPageList(paging);
        System.out.println("findMemberPageList :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }
}
