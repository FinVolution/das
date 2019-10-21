package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.TaskSql;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.TaskSqlView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SelectEntityDao.class})
public class SelectEntityDaoTest {

    @Autowired
    SelectEntityDao selectEntityDao;

    TaskSql task;

    TaskSql taskSqlModel;

    Paging<TaskSql> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        task = TaskSql.builder().class_name("SelectEntityDaoTest").pojo_name("SelectEntityDaoTest").comment("project comment").approveMsg("ass").dbset_id(111).sql_content("select * from tab").project_id(999L).update_user_no("007").build();
        taskSqlModel = TaskSql.builder().project_id(97L).build();
        paging.setData(taskSqlModel);
    }

    @Test
    public void insertTask() throws Exception {
        Long id = selectEntityDao.insertTask(task);
        System.out.println("insertTask :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void updateTask() throws Exception {
        task.setId(26L);
        task.setClass_name("SelectTab");
        int count = selectEntityDao.updateTask(task);
        System.out.println("updateTask :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteTask() throws Exception {
        task.setId(26L);
        int count = selectEntityDao.deleteTask(task);
        System.out.println("deleteTask :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteByProjectId() throws Exception {
        int count = selectEntityDao.deleteByProjectId(999L);
        System.out.println("deleteByProjectId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getTasksByTaskId() throws Exception {
        TaskSql taskSql = selectEntityDao.getTasksByTaskId(24L);
        System.out.println("getTasksByTaskId :-------> " + taskSql);
        Assert.assertTrue(taskSql != null);
    }

    @Test
    public void getSelectEntityTotalCount() throws Exception {
        Long count = selectEntityDao.getSelectEntityTotalCount(paging);
        System.out.println("getSelectEntityTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findSelectEntityPageList() throws Exception {
        List<TaskSqlView> list = selectEntityDao.findSelectEntityPageList(paging);
        System.out.println("findSelectEntityPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
