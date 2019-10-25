package com.ppdai.das.console.dao;

import com.ppdai.das.console.dto.entry.das.AppGroup;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.AppGroupView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppGroupDao.class})
public class AppGroupDaoTest {

    @Autowired
    AppGroupDao appGroupDao;

    AppGroup appGroup;

    AppGroup appGroupModel;

    Paging<AppGroup> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        appGroupModel = AppGroup.builder().build();
        appGroup = AppGroup.builder().serverGroupId(1L).comment("abc@123.com").name("tom").serverEnabled(1).build();
        paging.setData(appGroupModel);
    }

    @Test
    public void insertAppGroup() throws Exception {
        Long id = appGroupDao.insertAppGroup(appGroup);
        System.out.println("insertAppGroup :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void getAppGroupById() throws Exception {
        AppGroup appGroup = appGroupDao.getAppGroupById(1L);
        System.out.println("getAppGroupById :-------> " + appGroup);
        Assert.assertTrue(appGroup != null);
    }

    @Test
    public void updateAppGroup() throws Exception {
        appGroup.setId(1L);
        appGroup.setComment("fix");
        int count = appGroupDao.updateAppGroup(appGroup);
        System.out.println("updateAppGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteAppGroup() throws Exception {
        appGroup.setId(2L);
        int count = appGroupDao.deleteAppGroup(appGroup);
        System.out.println("deleteAppGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = appGroupDao.getCountByName("张三");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = appGroupDao.getCountByName("张三");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void changeServerGroup() throws Exception {
        Set<Long> appGroupIds = new HashSet<>();
        appGroupIds.add(1L);
        appGroupIds.add(2L);
        int count = appGroupDao.changeServerGroup(2L, appGroupIds);
        System.out.println("changeServerGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getAppGroupsByServerGroupId() throws Exception {
        List<AppGroup> list = appGroupDao.getAppGroupsByServerGroupId(2L);
        System.out.println("getAppGroupsByServerGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAppGroupTotalCount() throws Exception {
        Long count = appGroupDao.getAppGroupTotalCount(paging);
        System.out.println("getAppGroupTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findAppGroupPageList() throws Exception {
        List<AppGroupView> list = appGroupDao.findAppGroupPageList(paging);
        System.out.println("findAppGroupPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

}
