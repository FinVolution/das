package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.ProjectDbsetRelation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProjectDbsetRelationDao.class})
public class ProjectDbsetRelationDaoTest {

    @Autowired
    ProjectDbsetRelationDao projectDbsetRelationDao;

    ProjectDbsetRelation projectDbsetRelation;

    @Before
    public void setUp() {
        projectDbsetRelation = ProjectDbsetRelation.builder().dbsetId(1L).projectId(1L).updateUserNo("007").build();
    }

    @Test
    public void insertProjectDbsetRelation() throws Exception {
        Long id = projectDbsetRelationDao.insertProjectDbsetRelation(projectDbsetRelation);
        System.out.println("insertProjectDbsetRelation :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertRelationList() throws Exception {
        List<ProjectDbsetRelation> list = new ArrayList<>();
        for (long i = 1; i < 4; i++) {
            ProjectDbsetRelation projectDbsetRelation = ProjectDbsetRelation.builder().dbsetId(i + 1L).projectId(i).updateUserNo("007").build();
            list.add(projectDbsetRelation);
        }
        int[] ids = projectDbsetRelationDao.insertRelationList(list);
        System.out.println("insertProjectDbsetRelation :-------> " + ids);
        Assert.assertTrue(ids.length > 0);
    }

    @Test
    public void updateProjectDbsetRelation() throws Exception {
        projectDbsetRelation.setId(312L);
        projectDbsetRelation.setDbsetId(111L);
        int count = projectDbsetRelationDao.updateProjectDbsetRelation(projectDbsetRelation);
        System.out.println("updateProjectDbsetRelation :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteProjectDbsetRelation() throws Exception {
        projectDbsetRelation.setId(312L);
        int count = projectDbsetRelationDao.deleteProjectDbsetRelation(projectDbsetRelation);
        System.out.println("deleteProjectDbsetRelation :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteByProjectAndDbSetIdS() throws Exception {
        Set<Long> dbsetIds = new HashSet<>();
        dbsetIds.add(1L);
        dbsetIds.add(2L);
        int count = projectDbsetRelationDao.deleteByProjectAndDbSetIdS(1L, dbsetIds);
        System.out.println("deleteProjectDbsetRelation :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteByProjectId() throws Exception {
        int count = projectDbsetRelationDao.deleteByProjectId(999L);
        System.out.println("deleteByProjectId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getProjectDbsetRelationById() throws Exception {
        ProjectDbsetRelation projectDbsetRelation = projectDbsetRelationDao.getProjectDbsetRelationById(304L);
        System.out.println("getProjectDbsetRelationById :-------> " + projectDbsetRelation);
        Assert.assertTrue(projectDbsetRelation != null);
    }

    @Test
    public void getAllProjectDbsetRelation() throws Exception {
        List<ProjectDbsetRelation> list = projectDbsetRelationDao.getAllProjectDbsetRelation(88L);
        System.out.println("getAllProjectDbsetRelation :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getCountByRelation() throws Exception {
        Long count = projectDbsetRelationDao.getCountByRelation(234L,88L);
        System.out.println("getCountByRelation :-------> " + count);
        Assert.assertTrue(count > 0);
    }
}
