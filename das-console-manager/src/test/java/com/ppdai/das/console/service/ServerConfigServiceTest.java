package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dao.PermissionDao;
import com.ppdai.das.console.dao.ServerConfigDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.ServerConfig;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.ServerConfigService;
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
@SpringBootTest(classes = {LoginUserDao.class, ServerConfigDao.class, Message.class, PermissionService.class, PermissionDao.class})
public class ServerConfigServiceTest {

    @Mock
    private ServerConfigService serverConfigService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        ServerConfig serverConfig = ServerConfig.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(serverConfig, "serverConfig", true, 256);
        when(serverConfigService.validatePermision(user, errors)).thenReturn(chain);

        ListResult<ServerConfig> listResult = new ListResult<>();
        List<ServerConfig> list = Lists.newArrayList(ServerConfig.builder().id(1L).build());
        listResult.setList(list);
        Paging<ServerConfig> paging = new Paging<>();
        paging.setData(new ServerConfig());
        when(serverConfigService.findServerAppConfigPageList(paging)).thenReturn(listResult);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        ServerConfig serverConfig = ServerConfig.builder().id(1L).build();
        Errors errors = new BeanPropertyBindingResult(serverConfig, "serverConfig", true, 256);
        Assert.assertTrue(serverConfigService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void findServerAppConfigPageListTest() throws SQLException {
        Paging<ServerConfig> paging = new Paging<>();
        paging.setData(new ServerConfig());
        Assert.assertTrue(serverConfigService.findServerAppConfigPageList(paging).getList().size() > 0);
    }
}
