package com.ppdai.platform.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.PermissionDao;
import com.ppdai.platform.das.console.dao.TableEntityDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.view.LoginUserView;
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
@SpringBootTest(classes = {LoginUserDao.class, TableEntityDao.class, Message.class, PermissionService.class, PermissionDao.class})
public class UserServiceTest {

    @Mock
    private UserService userService;

    private LoginUser user = LoginUser.builder().id(1L).build();
    private Errors errors = new BeanPropertyBindingResult(user, "user", true, 256);
    private LoginUser currentUser = LoginUser.builder().id(2L).build();
    private LoginUser newUser = LoginUser.builder().id(3L).build();

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        when(userService.validatePermision(user, errors)).thenReturn(chain);

        when(userService.canUpdateOrDeleteUser(currentUser, newUser)).thenReturn(true);

        when(userService.canUpdateOrDeleteUser(1L, 2, 3L, 4)).thenReturn(true);

        when(userService.addUser(user)).thenReturn(true);

        when(userService.update(user)).thenReturn(true);

        ListResult<LoginUserView> listResult = new ListResult<>();
        List<LoginUserView> list = Lists.newArrayList(LoginUserView.builder().id(1).build());
        listResult.setList(list);
        Paging<LoginUser> paging = new Paging<>();
        paging.setData(new LoginUser());
        when(userService.findUserPageList(paging)).thenReturn(listResult);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        Assert.assertTrue(userService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void canUpdateOrDeleteUserTest() throws SQLException {
        Assert.assertTrue(userService.canUpdateOrDeleteUser(currentUser, newUser));
    }

    @Test
    public void canUpdateOrDeleteUserTest2() throws SQLException {
        Assert.assertTrue(userService.canUpdateOrDeleteUser(1L, 2, 3L, 4));
    }

    @Test
    public void addUserTest() throws SQLException {
        Assert.assertTrue(userService.addUser(user));
    }

    @Test
    public void updateTest() throws SQLException {
        Assert.assertTrue(userService.update(user));
    }

    @Test
    public void findUserPageListTest() throws SQLException {
        Paging<LoginUser> paging = new Paging<>();
        paging.setData(new LoginUser());
        Assert.assertTrue(userService.findUserPageList(paging).getList().size() > 0);
    }
}
