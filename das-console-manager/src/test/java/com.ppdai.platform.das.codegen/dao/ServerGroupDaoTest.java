package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.Server;
import com.ppdai.platform.das.codegen.dto.entry.das.ServerGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.ServerGroupView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServerGroupDao.class})
public class ServerGroupDaoTest {

    @Autowired
    ServerGroupDao serverGroupDao;

    ServerGroup serverGroup;

    Paging<ServerGroup> paging;

    private String serverGroupName = "server_group_name_test";

    @Before
    public void setUp() {
        paging = new Paging<>();
        serverGroup = ServerGroup.builder().name(serverGroupName).comment("测试").build();
        paging.setData(serverGroup);
    }

    @Test
    public void insertServerGroup() throws Exception {
        Long id = serverGroupDao.insertServerGroup(serverGroup);
        System.out.println("insertServerGroup :-------> " + id);
        Assert.assertTrue(id != null);
    }

    @Test
    public void getServerGroupById() throws Exception {
        ServerGroup serverGroup = serverGroupDao.getServerGroupById(1L);
        System.out.println("getServerGroupById :-------> " + serverGroup);
        Assert.assertTrue(serverGroup != null);
    }

    @Test
    public void getAllServerGroups() throws Exception {
        List<ServerGroup> list = serverGroupDao.getAllServerGroups();
        System.out.println("getAllServerGroups :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = serverGroupDao.getCountByName(serverGroupName);
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = serverGroupDao.getCountByName("pres_test");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateServerGroup() throws Exception {
        serverGroup.setId(1L);
        int id = serverGroupDao.updateServerGroup(serverGroup);
        System.out.println("updateServerGroup :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void deleteServerGroup() throws Exception {
        serverGroup.setId(3L);
        int count = serverGroupDao.deleteServerGroup(serverGroup);
        System.out.println("deleteServerGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteServerGroupAndServerAndServerConfigByServerGroupId() throws Exception {
        serverGroup.setId(3L);
        int count = serverGroupDao.deleteServerGroupAndServerAndServerConfigByServerGroupId(3L);
        System.out.println("deleteServerGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void serversNoGroup() throws Exception {
        List<Server> list = serverGroupDao.serversNoGroup(3);
        System.out.println("serversNoGroup :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getServerGroupTotalCount() throws Exception {
        Long count = serverGroupDao.getServerGroupTotalCount(paging);
        System.out.println("getServerGroupTotalCount :-------> " + count);
        Assert.assertTrue(count> 0);
    }

    @Test
    public void findServerGroupPageList() throws Exception {
        List<ServerGroupView> list = serverGroupDao.findServerGroupPageList(paging);
        System.out.println("findServerGroupPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

}
