package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.PermissionDao;
import com.ppdai.platform.das.console.dao.UserGroupDao;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
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

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, PermissionDao.class, DataBaseDao.class, UserGroupDao.class, Message.class, PermissionService.class})
public class GroupDatabaseServiceTest {

    @Mock
    private GroupDatabaseService groupDatabaseService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        DataBaseInfo dalGroupDB = DataBaseInfo.builder().dbname("name").build();
        Errors errors = new BeanPropertyBindingResult(dalGroupDB, "dalGroupDB", true, 256);
        when(groupDatabaseService.validatePermision(user, errors)).thenReturn(chain);

        when(groupDatabaseService.isGroupHadDB(1L, 1L)).thenReturn(true);

        when(groupDatabaseService.isGroupHadDB(dalGroupDB)).thenReturn(true);

        when(groupDatabaseService.validateTransferPermision(1L, 1L)).thenReturn(true);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        DataBaseInfo dalGroupDB = DataBaseInfo.builder().dbname("name").build();
        Errors errors = new BeanPropertyBindingResult(dalGroupDB, "dalGroupDB", true, 256);
        Assert.assertTrue(groupDatabaseService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void isGroupHadDBTest() throws SQLException {
        Assert.assertTrue(groupDatabaseService.isGroupHadDB(1L, 1L));
    }

    @Test
    public void isGroupHadDBTest2() throws SQLException {
        DataBaseInfo dalGroupDB = DataBaseInfo.builder().dbname("name").build();
        Assert.assertTrue(groupDatabaseService.isGroupHadDB(dalGroupDB));
    }

    @Test
    public void validateTransferPermisionTest() throws SQLException {
        Assert.assertTrue(groupDatabaseService.validateTransferPermision(1L, 1L));
    }
}
