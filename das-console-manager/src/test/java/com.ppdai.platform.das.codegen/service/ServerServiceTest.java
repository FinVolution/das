package com.ppdai.platform.das.codegen.service;


import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.LoginUserDao;
import com.ppdai.platform.das.codegen.dao.PermissionDao;
import com.ppdai.platform.das.codegen.dao.ServerDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.Server;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.ServerView;
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
@SpringBootTest(classes = {LoginUserDao.class, ServerDao.class, Message.class, PermissionService.class, PermissionDao.class})
public class ServerServiceTest {

    @Mock
    private ServerService serverService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        Server server = Server.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(server, "server", true, 256);
        when(serverService.validatePermision(user, errors)).thenReturn(chain);

        ListResult<ServerView> listResult = new ListResult<>();
        List<ServerView> list = Lists.newArrayList(ServerView.builder().id(1L).build());
        listResult.setList(list);
        Paging<Server> paging = new Paging<>();
        paging.setData(new Server());
        when(serverService.findServerPageList(paging)).thenReturn(listResult);

        when(serverService.insertServer(server)).thenReturn(ServiceResult.success());

        when(serverService.deleteServer(server)).thenReturn(ServiceResult.success());
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        Server server = Server.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(server, "server", true, 256);
        Assert.assertTrue(serverService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void findServerPageListTest() throws SQLException {
        Paging<Server> paging = new Paging<>();
        paging.setData(new Server());
        Assert.assertTrue(serverService.findServerPageList(paging).getList().size() > 0);
    }

    @Test
    public void insertServerTest() throws SQLException {
        Server server = Server.builder().id(1L).build();
        Assert.assertTrue(serverService.insertServer(server).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteServerTest() throws SQLException {
        Server server = Server.builder().id(1L).build();
        Assert.assertTrue(serverService.deleteServer(server).getCode() == ServiceResult.SUCCESS);
    }
}
