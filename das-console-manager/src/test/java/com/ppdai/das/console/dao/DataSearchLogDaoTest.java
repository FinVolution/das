package com.ppdai.das.console.dao;


import com.ppdai.das.console.dto.entry.das.DataSearchLog;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DataSearchLogView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DataSearchLogDao.class})
public class DataSearchLogDaoTest {
    @Autowired
    DataSearchLogDao dataSearchLogDao;

    DataSearchLog dataSearchLog;

    DataSearchLogView dataSearchLogModel;

    Paging<DataSearchLogView> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        dataSearchLog = DataSearchLog.builder().ip("127.0.0.1").request("asasas").request_type(1).result("success").success(true).build();
        dataSearchLogModel = DataSearchLogView.builder().request_type(1).build();
        paging.setData(dataSearchLogModel);
    }

    @Test
    public void insertDataSearchLog() throws Exception {
        Long id = dataSearchLogDao.insertDataSearchLog(dataSearchLog);
        System.out.println("insertDataSearchLog :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void getTotalCount() throws Exception {
        Long count = dataSearchLogDao.getTotalCount(paging);
        System.out.println("getTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findLogPageList() throws Exception {
        List<DataSearchLogView> list = dataSearchLogDao.findLogPageList(paging);
        System.out.println("findLogPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void findDataSearchLogList() throws Exception {
        List<DataSearchLog> list = dataSearchLogDao.findDataSearchLogList(10);
        System.out.println("findDataSearchLogList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
