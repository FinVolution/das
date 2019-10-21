package com.ppdai.platform.das.codegen.service;

import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dao.GroupDao;
import com.ppdai.platform.das.codegen.dao.LoginUserDao;
import com.ppdai.platform.das.codegen.dao.SetupDatabaseDao;
import com.ppdai.platform.das.codegen.dao.UserGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DasGroup;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.ConnectionRequest;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
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
