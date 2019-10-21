package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.ServerConfig;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServerConfigDao.class})
public class ServerConfigDaoTest {

    @Autowired
    ServerConfigDao serverConfigDao;

    ServerConfig serverConfig;

    Paging<ServerConfig> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        serverConfig = ServerConfig.builder().serverId(1).keya("key01").value("122").comment("serve configr").update_user_no("00001").build();
        paging.setData(serverConfig);
    }

    @Test
    public void insertServerAppConfig() throws Exception {
        Long id = serverConfigDao.insertServerAppConfig(serverConfig);
        System.out.println("insertServerAppConfig :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void deleteServerAppConfig() throws Exception {
        serverConfig.setId(5L);
        int count = serverConfigDao.deleteServerAppConfig(serverConfig);
        System.out.println("deleteServerAppConfig :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateServerAppConfig() throws Exception {
        serverConfig.setId(6L);
        serverConfig.setComment("test");
        int count = serverConfigDao.updateServerAppConfig(serverConfig);
        System.out.println("updateServerAppConfig :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteServerConfigByServerGroupId() throws Exception {
        int count = serverConfigDao.deleteServerConfigByServerGroupId(1L);
        System.out.println("deleteServerConfigByServerGroupId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getServerAppConfigByServerId() throws Exception {
        List<ServerConfig> list = serverConfigDao.getServerAppConfigByServerId(1L);
        System.out.println("deleteServerConfigByServerGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getServerAppConfigTotalCount() throws Exception {
        Long count = serverConfigDao.getServerAppConfigTotalCount(paging);
        System.out.println("getServerAppConfigTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findServerAppConfigPageList() throws Exception {
        List<ServerConfig> list = serverConfigDao.findServerAppConfigPageList(paging);
        System.out.println("findServerAppConfigPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
