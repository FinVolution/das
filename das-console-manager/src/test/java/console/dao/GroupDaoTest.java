package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.DasGroup;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.DalGroupView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GroupDao.class})
public class GroupDaoTest {

    @Autowired
    GroupDao groupDao;

    DasGroup group;

    DasGroup dasGroupModel;

    Paging<DasGroup> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        group = DasGroup.builder().group_comment("dbsetname").group_name("groupName001").update_user_no("007").build();
        dasGroupModel = DasGroup.builder().group_name("das").build();
        paging.setData(dasGroupModel);
    }

    @Test
    public void insertDalGroup() throws Exception {
        Long id = groupDao.insertDasGroup(group);
        System.out.println("insertDasGroup :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void updateDalGroup() throws Exception {
        group.setId(62L);
        group.setGroup_name("dbsetnamenew");
        int count = groupDao.updateDalGroup(group);
        System.out.println("updateDalGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteDalGroup() throws Exception {
        int count = groupDao.deleteDalGroup(62L);
        System.out.println("deleteDalGroup :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = groupDao.getCountByName("tx");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = groupDao.getCountByIdAndName(46L, "tx");
        System.out.println("getCountByIdAndName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getGroupsByUserId() throws Exception {
        List<DasGroup> list = groupDao.getGroupsByUserId(46L);
        System.out.println("getGroupsByUserId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getGroupsByDbSetId() throws Exception {
        DasGroup dasGroup = groupDao.getGroupsByDbSetId(52L);
        System.out.println("getGroupsByDbSetId :-------> " + dasGroup);
        Assert.assertTrue(dasGroup != null);
    }

    @Test
    public void getDalGroupById() throws Exception {
        DasGroup dasGroup = groupDao.getDalGroupById(52L);
        System.out.println("getDalGroupById :-------> " + dasGroup);
        Assert.assertTrue(dasGroup != null);
    }

    @Test
    public void getGroupByName() throws Exception {
        DasGroup dasGroup = groupDao.getGroupByName("das_test");
        System.out.println("getGroupByName :-------> " + dasGroup);
        Assert.assertTrue(dasGroup != null);
    }

    @Test
    public void getAllGroups() throws Exception {
        List<DasGroup> list = groupDao.getAllGroups();
        System.out.println("getAllGroups :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllGroupsByAppoid() throws Exception {
        List<DasGroup> list = groupDao.getAllGroupsByAppoid("1000002118");
        System.out.println("getAllGroupsByAppoid :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllGroupsWithNotAdmin() throws Exception {
        List<DasGroup> list = groupDao.getAllGroupsWithNotAdmin();
        System.out.println("getAllGroupsWithNotAdmin :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getTotalCount() throws Exception {
        Long count = groupDao.getTotalCount(paging);
        System.out.println("getTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findGroupPageList() throws Exception {
        List<DalGroupView> list = groupDao.findGroupPageList(paging);
        System.out.println("findGroupPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
