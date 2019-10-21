package com.ppdai.platform.das.codegen.dao;


import com.ppdai.platform.das.codegen.dto.view.TaskSqlView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TaskSqlDao.class})
public class TaskSqlDaoTest {

    @Autowired
    TaskSqlDao taskSqlDao;

    @Test
    public void updateAndGetAllTasks() throws Exception {
        List<TaskSqlView> list = taskSqlDao.updateAndGetAllTasks(186L);
        System.out.println("updateAndGetAllTasks :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }


}
