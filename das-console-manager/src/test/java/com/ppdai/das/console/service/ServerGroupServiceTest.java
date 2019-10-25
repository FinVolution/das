package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.*;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.ServerGroupService;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.ServerConfig;
import com.ppdai.das.console.dto.entry.das.ServerGroup;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.view.ServerGroupView;
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
@SpringBootTest(classes = {LoginUserDao.class, AppGroupDao.class, ServerGroupDao.class, Message.class, DeleteCheckDao.class, PermissionService.class, PermissionDao.class})
public class ServerGroupServiceTest {

    @Mock
    private ServerGroupService serverGroupService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        ServerConfig serverConfig = ServerConfig.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(serverConfig, "serverConfig", true, 256);
        when(serverGroupService.validatePermision(user, errors)).thenReturn(chain);

        ListResult<ServerGroupView> listResult = new ListResult<>();
        List<ServerGroupView> list = Lists.newArrayList(ServerGroupView.builder().id(1L).build());
        listResult.setList(list);
        Paging<ServerGroup> paging = new Paging<>();
        paging.setData(new ServerGroup());
        when(serverGroupService.findServerGroupPageList(paging)).thenReturn(listResult);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        ServerConfig serverConfig = ServerConfig.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(serverConfig, "serverConfig", true, 256);
        Assert.assertTrue(serverGroupService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void findServerGroupPageListTest() throws SQLException {
        Paging<ServerGroup> paging = new Paging<>();
        paging.setData(new ServerGroup());
        Assert.assertTrue(serverGroupService.findServerGroupPageList(paging).getList().size() > 0);
    }

}
