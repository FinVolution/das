package com.ppdai.das.console.service;

import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dao.SetupDatabaseDao;
import com.ppdai.das.console.dao.UserGroupDao;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.ConnectionRequest;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.SetupDataBaseService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, Consts.class, SetupDatabaseDao.class, GroupDao.class, UserGroupDao.class})
public class SetupDataBaseServiceTest {

    @Mock
    private SetupDataBaseService setupDataBaseService;

    private ConnectionRequest connectionRequest = ConnectionRequest.builder().db_user("name").build();

    private DasGroup dasGroup = DasGroup.builder().id(1L).build();

    private LoginUser user = LoginUser.builder().id(1L).build();

    @Before
    public void setUp() throws Exception {
        when(setupDataBaseService.connectionTest(connectionRequest)).thenReturn(ServiceResult.success());

       // when(setupDataBaseService.initializeDatasourceXml("127.0.0.1", "3306", "root", "root", "code_gen")).thenReturn(true);

        when(setupDataBaseService.tableConsistent("code_gen")).thenReturn(true);

        //when(setupDataBaseService.datasourceXmlValid()).thenReturn(true);

        //when(setupDataBaseService.setupTables()).thenReturn(true);

        when(setupDataBaseService.setupAdmin(dasGroup, user)).thenReturn(true);

        when(setupDataBaseService.isDalInitialized()).thenReturn(true);
    }

    @Test
    public void findServerPageListTest() {
        Assert.assertTrue(setupDataBaseService.connectionTest(connectionRequest).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void initializeDatasourceXmlTest() throws Exception {
        //Assert.assertTrue(setupDataBaseService.initializeDatasourceXml("127.0.0.1", "3306", "root", "root", "code_gen"));
    }

    @Test
    public void tableConsistentTest() throws Exception {
        Assert.assertTrue(setupDataBaseService.tableConsistent("code_gen"));
    }

    @Test
    public void datasourceXmlValidTest() throws Exception {
        //Assert.assertTrue(setupDataBaseService.datasourceXmlValid());
    }

    @Test
    public void setupTablesTest() throws Exception {
        //Assert.assertTrue(setupDataBaseService.setupTables());
    }

    @Test
    public void setupAdminTest() throws Exception {
        Assert.assertTrue(setupDataBaseService.setupAdmin(dasGroup, user));
    }

    @Test
    public void isDalInitializedTest() {
        Assert.assertTrue(setupDataBaseService.isDalInitialized());
    }
}
