package com.ppdai.das.console.service;


import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dao.PermissionDao;
import com.ppdai.das.console.service.PermissionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, PermissionDao.class})
public class PermissionServiceTest {

    @Mock
    private PermissionService permissionService;

    @Before
    public void setUp() throws Exception {

        when(permissionService.isSuperManagerById(1L)).thenReturn(true);

        when(permissionService.isSuperManagerByUserNo("1")).thenReturn(true);

        when(permissionService.isNormalSuperManagerById(1L)).thenReturn(true);

        when(permissionService.isManagerById(1L)).thenReturn(true);

        when(permissionService.isGroupManagerById(1L, 1L)).thenReturn(true);

        when(permissionService.isNormalManagerById(1L, 1L)).thenReturn(true);

        when(permissionService.isProjectPermission(1L, 1L)).thenReturn(true);
    }

    @Test
    public void isSuperManagerByIdTest() {
        Assert.assertTrue(permissionService.isSuperManagerById(1L));
    }

    @Test
    public void isSuperManagerByUserNoTest() throws Exception {
        Assert.assertTrue(permissionService.isSuperManagerByUserNo("1"));
    }

    @Test
    public void isNormalSuperManagerByIdTest() throws Exception {
        Assert.assertTrue(permissionService.isNormalSuperManagerById(1L));
    }

    @Test
    public void isManagerByIdTest() throws Exception {
        Assert.assertTrue(permissionService.isManagerById(1L));
    }

    @Test
    public void isGroupManagerByIdTest() throws Exception {
        Assert.assertTrue(permissionService.isGroupManagerById(1L, 1L));
    }

    @Test
    public void isNormalManagerByIdTest() throws Exception {
        Assert.assertTrue(permissionService.isNormalManagerById(1L, 1L));
    }

    @Test
    public void isProjectPermissionTest() throws Exception {
        Assert.assertTrue(permissionService.isProjectPermission(1L, 1L));
    }
}
