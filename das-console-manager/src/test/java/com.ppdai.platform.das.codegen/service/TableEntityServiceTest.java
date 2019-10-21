package com.ppdai.platform.das.codegen.service;


import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.LoginUserDao;
import com.ppdai.platform.das.codegen.dao.PermissionDao;
import com.ppdai.platform.das.codegen.dao.TableEntityDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.TaskTable;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.TaskTableView;
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
@SpringBootTest(classes = {LoginUserDao.class, TableEntityDao.class, Message.class, PermissionService.class, PermissionDao.class})
public class TableEntityServiceTest {

    @Mock
    private TableEntityService tableEntityService;

    private LoginUser user = LoginUser.builder().id(1L).build();
    private TaskTable taskTable = TaskTable.builder().id(1L).table_names("name").build();
    private Errors errors = new BeanPropertyBindingResult(taskTable, "taskTable", true, 256);

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();

        when(tableEntityService.validatePermision(user, taskTable, errors)).thenReturn(chain);

        ListResult<TaskTableView> listResult = new ListResult<>();
        List<TaskTableView> list = Lists.newArrayList(TaskTableView.builder().id(1L).build());
        listResult.setList(list);
        Paging<TaskTable> paging = new Paging<>();
        paging.setData(new TaskTable());
        when(tableEntityService.findTableEntityPageList(paging)).thenReturn(listResult);

        when(tableEntityService.insertTask(taskTable)).thenReturn(1L);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        Assert.assertTrue(tableEntityService.validatePermision(user, taskTable, errors).validate().isValid());
    }

    @Test
    public void findTableEntityPageListTest() throws SQLException {
        Paging<TaskTable> paging = new Paging<>();
        paging.setData(new TaskTable());
        Assert.assertTrue(tableEntityService.findTableEntityPageList(paging).getList().size() > 0);
    }

    @Test
    public void insertTasktest() throws SQLException {
        Assert.assertTrue(tableEntityService.insertTask(taskTable) == 1);
    }
}
