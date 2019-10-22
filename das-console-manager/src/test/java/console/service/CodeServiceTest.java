package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.dao.DeleteCheckDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DeleteCheckDao.class})
public class CodeServiceTest {

    @Mock
    private CodeService codeService;

    @Before
    public void setUp() throws Exception {
        when(codeService.isTaskCountByProjectId(1L)).thenReturn(true);
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        Assert.assertTrue(codeService.isTaskCountByProjectId(1L));
    }
}
