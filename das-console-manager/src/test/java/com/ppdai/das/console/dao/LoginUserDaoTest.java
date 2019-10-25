package com.ppdai.das.console.dao;

import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.LoginUserView;
import com.ppdai.das.console.dto.view.LoginUsersView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LoginUserDao.class})
public class LoginUserDaoTest {

    @Autowired
    LoginUserDao loginUserDao;

    LoginUser loginUser;

    Paging<LoginUser> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        loginUser = LoginUser.builder().password("123").userEmail("abc@123.com").userRealName("张三").userName("tom").userNo("007").update_user_no("张三").build();
        paging.setData(loginUser);
    }

    @Test
    public void insertUser() throws Exception {
        Long id = loginUserDao.insertUser(loginUser);
        System.out.println("insertServer :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void getAllUsers() throws Exception {
        List<LoginUser> list = loginUserDao.getAllUsers();
        System.out.println("getAllUsers :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getUserById() throws Exception {
        LoginUser loginUser1 = loginUserDao.getUserById(1L);
        System.out.println("getUserById :-------> " + loginUser1);
        Assert.assertTrue(loginUser1 != null);
    }

    @Test
    public void getUserByNo() throws Exception {
        LoginUser loginUser1 = loginUserDao.getUserByNo("011158");
        System.out.println("getUserByNo :-------> " + loginUser1);
        Assert.assertTrue(loginUser1 != null);
    }


    @Test
    public void getUserByUserName() throws Exception {
        LoginUser loginUser1 = loginUserDao.getUserByUserName("zhaodong");
        System.out.println("getUserByNo :-------> " + loginUser1);
        Assert.assertTrue(loginUser1 != null);
    }

    @Test
    public void getUserByGroupId() throws Exception {
        List<LoginUsersView> list = loginUserDao.getUserByGroupId(1L);
        System.out.println("getUserByGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void updateUser() throws Exception {
        loginUser.setId(21L);
        int count = loginUserDao.updateUser(loginUser);
        System.out.println("updateUser :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateUserPassword() throws Exception {
        loginUser.setId(21L);
        int count = loginUserDao.updateUserPassword(loginUser);
        System.out.println("updateUserPassword :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteUser() throws Exception {
        int count = loginUserDao.deleteUser(118L);
        System.out.println("deleteUser :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getTotalCount() throws Exception {
        Long count = loginUserDao.getTotalCount(paging);
        System.out.println("getTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findUserPageList() throws Exception {
        List<LoginUserView> list = loginUserDao.findUserPageList(paging);
        System.out.println("findUserPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
