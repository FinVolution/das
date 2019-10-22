package com.ppdai.platform.das.console.dao;

import com.ppdai.platform.das.console.dto.entry.das.UserProject;
import com.ppdai.platform.das.console.dto.model.Paging;
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
@SpringBootTest(classes = {UserProjectDao.class})
public class UserProjectDaoTest {

    @Autowired
    UserProjectDao userProjectDao;

    UserProject userProject;

    Paging<UserProject> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        userProject = UserProject.builder().projectId(1L).userId(2L).build();
        paging.setData(userProject);
    }

    @Test
    public void insertUserProject() throws Exception {
        Long id = userProjectDao.insertUserProject(userProject);
        System.out.println("insertUserProject :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void insertUserProjectList() throws Exception {
        List<UserProject> userProjects = new ArrayList<>();
        for (Long i = 1L; i < 4; i++) {
            userProjects.add(UserProject.builder().projectId(1L).userId(i).build());
        }
        int[] ids = userProjectDao.insertUserProjectList(userProjects);
        System.out.println("insertUserProjectList :-------> " + ids);
        Assert.assertTrue(ids.length > 0);
    }

    @Test
    public void updateUserProject() throws Exception {
        userProject.setId(176L);
        userProject.setUserId(4L);
        int count = userProjectDao.updateUserProject(userProject);
        System.out.println("updateUserProject :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteUserProjectByProjectId() throws Exception {
        int count = userProjectDao.deleteUserProjectByProjectId(1L);
        System.out.println("updateUserProject :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteByProjectAndUserIdS() throws Exception {
        Set<Long> userIds = new HashSet<>();
        userIds.add(1L);
        userIds.add(2L);
        int count = userProjectDao.deleteByProjectAndUserIdS(1L, userIds);
        System.out.println("deleteByProjectAndUserIdS :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getUsersByProjectId() throws Exception {
        List<UserProject> lsit = userProjectDao.getUsersByProjectId(1L);
        System.out.println("getUsersByProjectId :-------> " + lsit);
        Assert.assertTrue(lsit.size() > 0);
    }

    @Test
    public void getMinUserProjectByProjectId() throws Exception {
        UserProject userProject = userProjectDao.getMinUserProjectByProjectId(39L);
        System.out.println("getMinUserProjectByProjectId :-------> " + userProject);
        Assert.assertTrue(userProject.getId() > 0);
    }
}
