package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.model.DataBaseModel;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.DataBaseView;
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
@SpringBootTest(classes = {DataBaseDao.class})
public class DataBaseDaoTest {

    @Autowired
    DataBaseDao dataBaseDao;

    DataBaseInfo dataBaseInfo;

    DataBaseModel dataBaseModel;

    Paging<DataBaseInfo> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        dataBaseInfo = DataBaseInfo.builder().db_type(1).dbname("DataBaseInfo11").db_catalog("table12").db_user("tom")
                .dal_group_id(1L).db_address("128.0.0.12").db_port("3309").db_password("121231").build();
        dataBaseInfo.setDb_catalog("table");
        dataBaseModel = DataBaseModel.builder().build();
        paging.setData(dataBaseInfo);
    }

    @Test
    public void insertDataBaseInfo() throws Exception {
        Long id = dataBaseDao.insertDataBaseInfo(dataBaseInfo);
        System.out.println("insertDataBaseInfo :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertDatabaselist() throws Exception {
        List<DataBaseInfo> dataBaseInfoList = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            DataBaseInfo alldbs = new DataBaseInfo();
            alldbs.setComment("aa" + i);
            alldbs.setDal_group_id(1L);
            alldbs.setDb_user("omas" + i);
            alldbs.setDb_address("192.0.0.12" + i);
            alldbs.setDb_catalog("table" + i);
            alldbs.setDbname("name11" + i);
            alldbs.setDb_password("21212" + i);
            alldbs.setDb_port("1203" + i);
            alldbs.setDb_type(1);
            alldbs.setUpdateUserNo("0076");
            dataBaseInfoList.add(alldbs);
        }

        int[] ins = dataBaseDao.insertDatabaselist(dataBaseInfoList);
        System.out.println("insertDatabaselist :-------> " + ins);
        Assert.assertTrue(ins.length > 0);
    }

    @Test
    public void updateDataBaseInfo() throws Exception {
        dataBaseInfo.setDb_catalog("aaaadddd");
        dataBaseInfo.setDbname("aaaadddd");
        dataBaseInfo.setId(443L);
        int cout = dataBaseDao.updateDataBaseInfo(dataBaseInfo);
        System.out.println("updateDataBaseInfo :-------> " + cout);
        Assert.assertTrue(cout > 0);
    }

    @Test
    public void updateDataBaseInfoTest() throws Exception {
        int cout = dataBaseDao.updateDataBaseInfo(443L, 22L, "22222");
        System.out.println("updateDataBaseInfo :-------> " + cout);
        Assert.assertTrue(cout > 0);
    }

    @Test
    public void updateDataBaseInfoT2() throws Exception {
        int cout = dataBaseDao.updateDataBaseInfo(443L, "33333");
        System.out.println("updateDataBaseInfo :-------> " + cout);
        Assert.assertTrue(cout > 0);
    }

    @Test
    public void updateDataBaseInfoT3() throws Exception {
        int cout = dataBaseDao.updateDataBaseInfo(443L, 44L);
        System.out.println("updateDataBaseInfo :-------> " + cout);
        Assert.assertTrue(cout > 0);
    }

    @Test
    public void deleteDataBaseInfo() throws Exception {
        int cout = dataBaseDao.deleteDataBaseInfo(451L);
        System.out.println("deleteDataBaseInfo :-------> " + cout);
        Assert.assertTrue(cout > 0);
    }

    @Test
    public void getDataBaseInfoByDbId() throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(90L);
        System.out.println("getDataBaseInfoByDbId :-------> " + dataBaseInfo);
        Assert.assertTrue(dataBaseInfo != null);
    }

    @Test
    public void getDatabaseByName() throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDatabaseByName("name11");
        System.out.println("getDatabaseByName :-------> " + dataBaseInfo);
        Assert.assertTrue(dataBaseInfo != null);
    }

    @Test
    public void getAllDbByProjectId() throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getAllDbByProjectId(38L);
        System.out.println("getAllDbByProjectId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDbByAppId() throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getAllDbByAppId("1000002455");
        System.out.println("getAllDbByAppId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDbAllinOneNames() throws Exception {
        List<String> list = dataBaseDao.getAllDbAllinOneNames();
        System.out.println("getAllDbAllinOneNames :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getGroupDBsByGroup() throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getGroupDBsByGroup(1L);
        System.out.println("getGroupDBsByGroup :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = dataBaseDao.getCountByName("aaaadddd");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = dataBaseDao.getCountByIdAndName(443L, "aaaadddd");
        System.out.println("getCountByIdAndName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getAllDbsByDbNames() throws Exception {
        List<String> dbNames = new ArrayList<>();
        dbNames.add("name11");
        dbNames.add("name12");
        dbNames.add("name13");
        List<DataBaseInfo> list = dataBaseDao.getAllDbsByDbNames(dbNames);
        System.out.println("getAllDbsByDbNames :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getAllDbsByIdss() throws Exception {
        List<Long> ids = new ArrayList<>();
        ids.add(448L);
        ids.add(449L);
        ids.add(450L);
        List<DataBaseInfo> list = dataBaseDao.getAllDbsByIdss(ids);
        System.out.println("getAllDbsByIdss :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getMasterCologByDatabaseSetId() throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseDao.getMasterCologByDatabaseSetId(54L);
        System.out.println("getMasterCologByDatabaseSetId :-------> " + dataBaseInfo);
        Assert.assertTrue(dataBaseInfo != null);
    }

    @Test
    public void getTotalCount() throws Exception {
        Long cont = dataBaseDao.getTotalCount(paging);
        System.out.println("getTotalCount :-------> " + cont);
        Assert.assertTrue(cont > 0);
    }

    @Test
    public void findDbPageList() throws Exception {
        List<DataBaseView> list = dataBaseDao.findDbPageList(paging);
        System.out.println("findDbPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getTotalCountByUserId() throws Exception {
        Long count = dataBaseDao.getTotalCountByUserId(paging, 54L);
        System.out.println("getTotalCountByUserId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findDbPageListByUserId() throws Exception {
        List<DataBaseView> list = dataBaseDao.findDbPageListByUserId(paging, 54L);
        System.out.println("findDbPageListByUserId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getDatabaseListByLikeName() throws Exception {
        List<DataBaseInfo> list = dataBaseDao.getDatabaseListByLikeName("pay");
        System.out.println("findDbPageListByUserId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
