package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.DatabaseSetView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseSetDao.class})
public class DatabaseSetDaoTest {

    @Autowired
    DatabaseSetDao databaseSetDao;

    DatabaseSet databaseset;

    DatabaseSetEntry databasesetentry;

    DatabaseSet databaseSetModel;

    Paging<DatabaseSet> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        databaseset = DatabaseSet.builder().name("dbsetname").className("class01").dbType(1).dynamicStrategyId(1L).groupId(1L).updateUserNo("00001").build();
        databaseSetModel = DatabaseSet.builder().className("bpoext").build();
        paging.setData(databaseSetModel);
    }

    @Test
    public void insertDatabaseSet() throws Exception {
        Long id = databaseSetDao.insertDatabaseSet(databaseset);
        System.out.println("insertDatabaseSet :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void updateDatabaseSet() throws Exception {
        databaseset.setId(235L);
        databaseset.setClassName("DatabaseSetDaoTest");
        int count = databaseSetDao.updateDatabaseSet(databaseset);
        System.out.println("updateDatabaseSet :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteDatabaseSetById() throws Exception {
        int count = databaseSetDao.deleteDatabaseSetById(235L);
        System.out.println("deleteDatabaseSetById :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteDatabaseSetEntryByDbsetId() throws Exception {
        int count = databaseSetDao.deleteDatabaseSetEntryByDbsetId(999L);
        System.out.println("deleteDatabaseSetEntryByDbsetId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getDatabaseSetById() throws Exception {
        DatabaseSet databaseset = databaseSetDao.getDatabaseSetById(234L);
        System.out.println("getDatabaseSetById :-------> " + databaseset);
        Assert.assertTrue(databaseset != null);
    }

    @Test
    public void getDatabaseSetByName() throws Exception {
        DatabaseSet databaseset = databaseSetDao.getDatabaseSetByName("test_das");
        System.out.println("getDatabaseSetByName :-------> " + databaseset);
        Assert.assertTrue(databaseset != null);
    }

    @Test
    public void getAllDatabaseSetByName() throws Exception {
        List<DatabaseSet> list = databaseSetDao.getAllDatabaseSetByName("test_das");
        System.out.println("getAllDatabaseSetByName :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDatabaseSetByNames() throws Exception {
        List<String> names = new ArrayList<>();
        names.add("test_das");
        names.add("sy_test");
        names.add("test");
        List<DatabaseSet> list = databaseSetDao.getAllDatabaseSetByNames(names);
        System.out.println("getAllDatabaseSetByName :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDatabaseSetById() throws Exception {
        List<DatabaseSet> list = databaseSetDao.getAllDatabaseSetById(234L);
        System.out.println("getAllDatabaseSetByName :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDatabaseSetByProjectId() throws Exception {
        List<DatabaseSetView> list = databaseSetDao.getAllDatabaseSetByProjectId(50L);
        System.out.println("getAllDatabaseSetByProjectId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDatabaseSetByGroupId() throws Exception {
        List<DatabaseSet> list = databaseSetDao.getAllDatabaseSetByGroupId(36L);
        System.out.println("getAllDatabaseSetByGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getMasterDatabaseSetEntryByDatabaseSetId() throws Exception {
        DatabaseSetEntry databasesetentry = databaseSetDao.getMasterDatabaseSetEntryByDatabaseSetId(180L);
        System.out.println("getMasterDatabaseSetEntryByDatabaseSetId :-------> " + databasesetentry);
        Assert.assertTrue(databasesetentry != null);
    }

    @Test
    public void getDbSetTotalCount() throws Exception {
        Long count = databaseSetDao.getDbSetTotalCount(paging);
        System.out.println("getDbSetTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findDbSetPageList() throws Exception {
        List<DatabaseSetView> list = databaseSetDao.findDbSetPageList(paging);
        System.out.println("findDbSetPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getDbSetTotalCountByAppid() throws Exception {
        databaseSetModel.setApp_id("1000002397");
        Long count = databaseSetDao.getDbSetTotalCountByAppid(paging);
        System.out.println("getDbSetTotalCountByAppid :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findDbSetPageListByAppid() throws Exception {
        databaseSetModel.setApp_id("1000002397");
        List<DatabaseSetView> list = databaseSetDao.findDbSetPageListByAppid(paging);
        System.out.println("findDbSetPageListByAppid :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

}