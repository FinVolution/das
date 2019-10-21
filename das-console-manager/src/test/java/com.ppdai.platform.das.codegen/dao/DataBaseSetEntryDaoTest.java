package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetEntryView;
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
@SpringBootTest(classes = {DataBaseSetEntryDao.class})
public class DataBaseSetEntryDaoTest {

    @Autowired
    DataBaseSetEntryDao dataBaseSetEntryDao;

    DatabaseSetEntry databasesetentry;

    DatabaseSetEntry databaseSetEntryModel;

    Paging<DatabaseSetEntry> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        databasesetentry = DatabaseSetEntry.builder().name("dbsetname").databaseType(1).dbset_id(1L).sharding("1").db_Id(1L).build();
        databaseSetEntryModel = DatabaseSetEntry.builder().dbset_id(90L).build();
        paging.setData(databaseSetEntryModel);
    }

    @Test
    public void insertDatabaseSet() throws Exception {
        Long id = dataBaseSetEntryDao.insertDatabaseSetEntry(databasesetentry);
        System.out.println("insertDatabaseSetEntry :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertDatabaseSetEntrylist() throws Exception {
        List<DatabaseSetEntry> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(DatabaseSetEntry.builder().name("dbsetname").databaseType(1 + i).dbset_id(1L).sharding("1").db_Id(1L).build());
        }
        int[] ints = dataBaseSetEntryDao.insertDatabaseSetEntrylist(list);
        System.out.println("insertDatabaseSetEntrylist :-------> " + ints);
        Assert.assertTrue(ints.length > 0);
    }

    @Test
    public void updateDatabaseSetEntry() throws Exception {
        databasesetentry.setId(611L);
        databasesetentry.setName("newdbset");
        int count = dataBaseSetEntryDao.updateDatabaseSetEntry(databasesetentry);
        System.out.println("updateDatabaseSetEntry :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteDatabaseSetEntryById() throws Exception {
        int count = dataBaseSetEntryDao.deleteDatabaseSetEntryById(611L);
        System.out.println("deleteDatabaseSetEntryById :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = dataBaseSetEntryDao.getCountByName("dbsetname");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = dataBaseSetEntryDao.getCountByIdAndName(610L, "dbsetname");
        System.out.println("getCountByIdAndName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getAllDbSetEntryByDbSetId() throws Exception {
        List<DatabaseSetEntry> list = dataBaseSetEntryDao.getAllDbSetEntryByDbSetId(234L);
        System.out.println("getAllDbSetEntryByDbSetId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getDataBaseSetEntryById() throws Exception {
        DatabaseSetEntry databaseSetEntryView = dataBaseSetEntryDao.getDataBaseSetEntryById(601L);
        System.out.println("getDataBaseSetEntryById :-------> " + databaseSetEntryView);
        Assert.assertTrue(databaseSetEntryView != null);
    }

    @Test
    public void getDataBaseSetEntryByDbId() throws Exception {
        DatabaseSetEntryView databaseSetEntryView = dataBaseSetEntryDao.getDataBaseSetEntryByDbId(601L);
        System.out.println("getDataBaseSetEntryById :-------> " + databaseSetEntryView);
        Assert.assertTrue(databaseSetEntryView != null);
    }

    @Test
    public void getAllDbSetEntryByDbSetIds() throws Exception {
        List<Integer> dbset_ids = new ArrayList<>();
        dbset_ids.add(608);
        dbset_ids.add(609);
        dbset_ids.add(610);
        List<DatabaseSetEntry> list = dataBaseSetEntryDao.getAllDbSetEntryByDbSetIds(dbset_ids);
        System.out.println("getAllDbSetEntryByDbSetIds :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getDatabaseSetEntrysByDbNames() throws Exception {
        List<String> names = new ArrayList<>();
        names.add("dbsetname");
        names.add("test31");
        List<DatabaseSetEntry> list = dataBaseSetEntryDao.getDatabaseSetEntrysByDbNames(names);
        System.out.println("getDatabaseSetEntrysByDbNames :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getDbSetEntryTotalCount() throws Exception {
        Long count = dataBaseSetEntryDao.getDbSetEntryTotalCount(paging);
        System.out.println("getDbSetEntryTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findDbSetEntryPageList() throws Exception {
        List<DatabaseSetEntryView> list = dataBaseSetEntryDao.findDbSetEntryPageList(paging);
        System.out.println("findDbSetEntryPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

}
