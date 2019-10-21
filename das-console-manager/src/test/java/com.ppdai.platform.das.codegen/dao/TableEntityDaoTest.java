package com.ppdai.platform.das.codegen.dao;


import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.dto.entry.das.TaskTable;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.TaskTableView;
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
@SpringBootTest(classes = {TableEntityDao.class})
public class TableEntityDaoTest {

    @Autowired
    TableEntityDao tableEntityDao;

    TaskTable taskTable;

    TaskTable taskTableModel;

    Paging<TaskTable> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        taskTable = TaskTable.builder().table_names("tableNames").custom_table_name("SelectEntityDaoTest").project_id(999L).view_names("SelectEntityDaoTest").comment("project comment").approveMsg("ass").dbset_id(111).update_user_no("007").build();
        taskTableModel = TaskTable.builder().project_id(39L).build();
        paging.setData(taskTableModel);
    }

    @Test
    public void insertTask() throws Exception {
        Long id = tableEntityDao.insertTask(taskTable);
        System.out.println("insertTask :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertTaskTablelist() throws Exception {
        List<TaskTable> list = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            list.add(TaskTable.builder().table_names("tableNames").custom_table_name("SelectEntityDaoTest").project_id(999L + i).view_names("SelectEntityDaoTest").comment("project comment").approveMsg("ass").dbset_id(111).update_user_no("007").build());
        }
        int[] ins = tableEntityDao.insertTaskTablelist(list);
        System.out.println("insertTaskTablelist :-------> " + ins);
        Assert.assertTrue(ins.length > 0);
    }

    @Test
    public void updateTask() throws Exception {
        taskTable.setId(1004L);
        taskTable.setComment("aaaaaa");
        int count = tableEntityDao.updateTask(taskTable);
        System.out.println("updateTask :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteTask() throws Exception {
        taskTable.setId(1004L);
        taskTable.setComment("aaaaaa");
        int count = tableEntityDao.deleteTask(taskTable);
        System.out.println("deleteTask :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteByProjectId() throws Exception {
        int count = tableEntityDao.deleteByProjectId(999);
        System.out.println("deleteByProjectId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getTasksByTaskId() throws Exception {
        TaskTable taskTable = tableEntityDao.getTasksByTaskId(1001L);
        System.out.println("getTasksByTaskId :-------> " + taskTable);
        Assert.assertTrue(taskTable != null);
    }

    @Test
    public void getTaskTableByDbNames() throws Exception {
        List<String> names = Lists.newArrayList("risk_scene_strategy", "risk_template");
        List<TaskTable> list = tableEntityDao.getTaskTableByDbNames(39L, names);
        System.out.println("getTaskTableByDbNames :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getTasksByProjectId() throws Exception {
        List<TaskTableView> list = tableEntityDao.getTasksByProjectId(39L);
        System.out.println("getTasksByProjectId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getTableEntityTotalCount() throws Exception {
        Long count = tableEntityDao.getTableEntityTotalCount(paging);
        System.out.println("getTableEntityTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findTableEntityPageList() throws Exception {
        List<TaskTableView> list = tableEntityDao.findTableEntityPageList(paging);
        System.out.println("findTableEntityPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
