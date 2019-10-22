package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServerDao.class})
public class ServerDaoTest {

    @Autowired
    ServerDao serverDao;

    Server server;

    @Before
    public void setUp() {
        server = Server.builder().ip("129.12.3.1").port(3123).serverGroupId(1L).comment("dao server").update_user_no("00001").build();
    }

    @Test
    public void insertServer() throws Exception {
        Long id = serverDao.insertServer(server);
        System.out.println("insertServer :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void getServerById() throws Exception {
        Long id = 5L;
        Server server = serverDao.getServerById(id);
        System.out.println("getServerById :-------> " + server.toString());
        Assert.assertTrue(server.getId() == id);
    }

    @Test
    public void updateServer() throws Exception {
        Long id = 4L;
        server.setId(id);
        server.setComment("sasasas");
        int _id = serverDao.updateServer(server);
        System.out.println("updateServer :-------> " + _id);
        Assert.assertTrue(_id == 1);
    }

    @Test
    public void deleteServer() throws Exception {
        Long id = 6L;
        server.setId(id);
        int _id = serverDao.deleteServer(server);
        System.out.println("deleteServer :-------> " + _id);
        Assert.assertTrue(_id > 0);
    }

    @Test
    public void deleteServerAndServerConfigByServerId() throws Exception {
        Long id = 1L;
        int _id = serverDao.deleteServerAndServerConfigByServerId(id);
        System.out.println("deleteServerAndServerConfigByServerId :-------> " + _id);
        Assert.assertTrue(_id > 0);
    }

    @Test
    public void isNotExistByIpAndPort() throws Exception {
        boolean boolen = serverDao.isNotExistByIpAndPort(server);
        System.out.println("isNotExistByIpAndPort :-------> " + boolen);
        Assert.assertTrue(boolen == false);
    }

    @Test
    public void deleteServerByServerGroupId() throws Exception {
        Long id = 1L;
        int _id = serverDao.deleteServerByServerGroupId(id);
        System.out.println("deleteServerByServerGroupId :-------> " + _id);
        Assert.assertTrue(_id > 0);
    }

    @Test
    public void changeServerGroup() throws Exception {
        Long serverId = 7L;
        Long serverGroupId = 11L;
        int _id = serverDao.changeServerGroup(serverId, serverGroupId);
        System.out.println("changeServerGroup :-------> " + _id);
        Assert.assertTrue(_id > 0);
    }

    @Test
    public void getServersByServerGroupId() throws Exception {
        Long serverGroupId = 11L;
        List<Server> list = serverDao.getServersByServerGroupId(serverGroupId);
        System.out.println("getServersByServerGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }


}
