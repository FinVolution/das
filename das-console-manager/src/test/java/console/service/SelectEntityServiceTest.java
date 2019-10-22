package com.ppdai.platform.das.console.service;


import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.PermissionDao;
import com.ppdai.platform.das.console.dao.SelectEntityDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.TaskSql;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.view.TaskSqlView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, SelectEntityDao.class, Message.class, PermissionService.class, PermissionDao.class})
public class SelectEntityServiceTest {

    @Mock
    private SelectEntityService selectEntityService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        TaskSql taskSql = TaskSql.builder().id(1L).class_name("name").build();
        Errors errors = new BeanPropertyBindingResult(taskSql, "taskSql", true, 256);
        when(selectEntityService.validatePermision(user, taskSql, errors)).thenReturn(chain);

        ListResult<TaskSqlView> listResult = new ListResult<>();
        List<TaskSqlView> list = Lists.newArrayList(TaskSqlView.builder().id(1L).class_name("list").build());
        listResult.setList(list);
        Paging<TaskSql> paging = new Paging<>();
        paging.setData(new TaskSql());
        when(selectEntityService.findSelectEntityPageList(paging)).thenReturn(listResult);

        when(selectEntityService.insertTask(taskSql, user)).thenReturn(1L);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        TaskSql taskSql = TaskSql.builder().id(1L).class_name("name").build();
        Errors errors = new BeanPropertyBindingResult(taskSql, "taskSql", true, 256);
        Assert.assertTrue(selectEntityService.validatePermision(user, taskSql, errors).validate().isValid());
    }

    @Test
    public void findSelectEntityPageListTest() throws SQLException {
        Paging<TaskSql> paging = new Paging<>();
        paging.setData(new TaskSql());
        Assert.assertTrue(selectEntityService.findSelectEntityPageList(paging).getList().size() > 0);
    }

    @Test
    public void insertTaskTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        TaskSql taskSql = TaskSql.builder().id(1L).class_name("name").build();
        Assert.assertTrue(selectEntityService.insertTask(taskSql, user) == 1);
    }
}
