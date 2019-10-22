package com.ppdai.platform.das.console.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DeleteCheckDao.class})
public class DeleteCheckDaoTest {

    @Autowired
    DeleteCheckDao deleteCheckDao;

    @Test
    public void isDbsetIdInTaskSQL() throws Exception {
        boolean b = deleteCheckDao.isDbsetIdInTaskSQL(1L);
        System.out.println("isDbsetIdInTaskSQL :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInTaskSQLTest() throws Exception {
        Set<Long> dbsetIds = new HashSet<>();
        dbsetIds.add(1L);
        boolean b = deleteCheckDao.isDbsetIdInTaskSQL(dbsetIds);
        System.out.println("isDbsetIdInTaskSQL :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInTaskTable() throws Exception {
        boolean b = deleteCheckDao.isDbsetIdInTaskTable(1L);
        System.out.println("isDbsetIdInTaskTable :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInTaskTableTest() throws Exception {
        Set<Long> dbsetIds = new HashSet<>();
        dbsetIds.add(1L);
        boolean b = deleteCheckDao.isDbsetIdInTaskTable(dbsetIds);
        System.out.println("isDbsetIdInTaskTable :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInProjectDbsetRelation() throws Exception {
        boolean b = deleteCheckDao.isDbsetIdInProjectDbsetRelation(1L);
        System.out.println("isDbsetIdInProjectDbsetRelation :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInDatabasesetentry() throws Exception {
        boolean b = deleteCheckDao.isDbsetIdInDatabasesetentry(1L);
        System.out.println("isDbsetIdInDatabasesetentry :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isServerGroupIdInAppGroup() throws Exception {
        boolean b = deleteCheckDao.isServerGroupIdInAppGroup(1L);
        System.out.println("isServerGroupIdInAppGroup :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isServerGroupIdInServer() throws Exception {
        boolean b = deleteCheckDao.isServerGroupIdInServer(1L);
        System.out.println("isServerGroupIdInServer :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetEntryIdInProject() throws Exception {
        boolean b = deleteCheckDao.isDbsetEntryIdInProject(1L);
        System.out.println("isDbsetEntryIdInProject :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isDbsetIdInProject() throws Exception {
        boolean b = deleteCheckDao.isDbsetIdInProject(1L);
        System.out.println("isDbsetIdInProject :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isProjectIdInProjectDbsetRelatio() throws Exception {
        boolean b = deleteCheckDao.isProjectIdInProjectDbsetRelatio(1L);
        System.out.println("isProjectIdInProjectDbsetRelatio :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isProjectIdInTaskTable() throws Exception {
        boolean b = deleteCheckDao.isProjectIdInTaskTable(1L);
        System.out.println("isProjectIdInTaskTable :-------> " + b);
        Assert.assertTrue(b);
    }

    @Test
    public void isProjectIdInTaskSQL() throws Exception {
        boolean b = deleteCheckDao.isProjectIdInTaskSQL(1L);
        System.out.println("isProjectIdInTaskSQL :-------> " + b);
        Assert.assertTrue(b);
    }

}
