package com.ppdai.platform.das.codegen.dao;

import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ProjectModel;
import com.ppdai.platform.das.codegen.dto.view.ProjectView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProjectDao.class})
public class ProjectDaoTest {

    @Autowired
    ProjectDao projectDao;

    Project project;

    ProjectModel projectModel;

    Paging<ProjectModel> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        project = Project.builder().comment("project comment").app_id("10000000008").app_scene("aaa").dal_config_name("sasas").update_user_no("007").build();
        projectModel = ProjectModel.builder().name("das").build();
        paging.setData(projectModel);
    }

    @Test
    public void insertProject() throws Exception {
        Long id = projectDao.insertProject(project);
        System.out.println("insertProject :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void deleteProject() throws Exception {
        project.setId(181L);
        int count = projectDao.deleteProject(project);
        System.out.println("deleteProject :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateProject() throws Exception {
        project.setId(182L);
        project.setName("projectName");
        int count = projectDao.updateProject(project);
        System.out.println("updateProject :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateTokenByAppId() throws Exception {
        int count = projectDao.updateTokenByAppId("10000000007", "aasasasasasas");
        System.out.println("updateTokenByAppId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateProjectGroupById() throws Exception {
        int count = projectDao.updateProjectGroupById(12, 182);
        System.out.println("updateProjectGroupById :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void updateProjectAppGroupIdById() throws Exception {
        Set<Long> projectIds = new HashSet<>();
        projectIds.add(182L);
        projectIds.add(185L);
        int count = projectDao.updateProjectAppGroupIdById(33L, projectIds);
        System.out.println("updateProjectAppGroupIdById :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteProjectAppGroupIdById() throws Exception {
        int count = projectDao.deleteProjectAppGroupIdById(33L);
        System.out.println("deleteProjectAppGroupIdById :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deleteProjectAppGroupIdByIdS() throws Exception {
        Set<Long> projectIds = new HashSet<>();
        projectIds.add(182L);
        projectIds.add(185L);
        int count = projectDao.deleteProjectAppGroupIdByIdS(projectIds);
        System.out.println("deleteProjectAppGroupIdByIdS :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getProjectByID() throws Exception {
        Project project = projectDao.getProjectByID(182L);
        System.out.println("getProjectByID :-------> " + project);
        Assert.assertTrue(project != null);
    }

    @Test
    public void getProjectByAppId() throws Exception {
        Project project = projectDao.getProjectByAppId("10000000008");
        System.out.println("getProjectByAppId :-------> " + project);
        Assert.assertTrue(project != null);
    }

    @Test
    public void getProjectByDbId() throws Exception {
        List<Project> list = projectDao.getProjectByDbId(430);
        System.out.println("getProjectByDbId :-------> " + list);
        Assert.assertTrue(project != null);
    }

    @Test
    public void getProjectBydbsetId() throws Exception {
        List<Project> list = projectDao.getProjectBydbsetId(430L);
        System.out.println("getProjectBydbsetId :-------> " + list);
        Assert.assertTrue(project != null);
    }

    @Test
    public void getProjectByGroupId() throws Exception {
        List<Project> list = projectDao.getProjectByGroupId(58L);
        System.out.println("getProjectByGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getProjectByAppGroupId() throws Exception {
        List<Project> list = projectDao.getProjectByAppGroupId(11L);
        System.out.println("getProjectByAppGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getCountByAppId() throws Exception {
        Long count = projectDao.getCountByAppId("10000000007");
        System.out.println("getCountByAppId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = projectDao.getCountByIdAndName(182L, "projectName");
        System.out.println("getCountByIdAndName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndAppId() throws Exception {
        Long count = projectDao.getCountByIdAndAppId(182L, "10000000007");
        System.out.println("getCountByIdAndAppId :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getProjectsListByLikeName() throws Exception {
        List<Project> list = projectDao.getProjectsListByLikeName("das");
        System.out.println("getProjectsListByLikeName :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getProjectsNoGroup() throws Exception {
        List<Project> list = projectDao.getProjectsNoGroup(11L);
        System.out.println("getProjectsNoGroup :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getProjectsByAppGroupId() throws Exception {
        List<Project> list = projectDao.getProjectsByAppGroupId(11L);
        System.out.println("getProjectsByAppGroupId :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getProjectTotalCount() throws Exception {
        Long count = projectDao.getProjectTotalCount(paging);
        System.out.println("getProjectTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findProjectPageList() throws Exception {
        List<ProjectView> list = projectDao.findProjectPageList(paging);
        System.out.println("findProjectPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
