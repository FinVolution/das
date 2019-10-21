package com.ppdai.platform.das.codegen.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PermissionDao.class})
public class PermissionDaoTest {

    @Autowired
    PermissionDao permissionDao;

    @Test
    public void isNormalSuperManagerById() throws Exception {
        Boolean b = permissionDao.isNormalSuperManagerById(1L);
        System.out.println("isNormalSuperManagerById :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isGroupManagerById() throws Exception {
        Boolean b = permissionDao.isGroupManagerById(1L, 1L);
        System.out.println("isGroupManagerById :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isNormalManagerById() throws Exception {
        Boolean b = permissionDao.isNormalManagerById(1L, 1L);
        System.out.println("isNormalManagerById :-------> " + b);
        Assert.assertTrue(b);
    }

}


