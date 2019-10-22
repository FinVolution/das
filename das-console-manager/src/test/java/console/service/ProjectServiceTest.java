package com.ppdai.platform.das.console.service;


import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.*;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.ProjectModel;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.view.ProjectView;
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
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, ProjectDao.class, PermissionDao.class, UserProjectDao.class, ProjectDbsetRelationDao.class, Message.class, SelectEntityDao.class, PermissionService.class, DeleteCheckDao.class})
public class ProjectServiceTest {

    @Mock
    private ProjectService projectService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        Project project = Project.builder().id(1L).name("name").build();
        Errors errors = new BeanPropertyBindingResult(project, "project", true, 256);
        when(projectService.validatePermision(user, project, errors)).thenReturn(chain);

        when(projectService.insertProject(project)).thenReturn(ServiceResult.success());

        when(projectService.updateProject(project)).thenReturn(ServiceResult.success());

        when(projectService.deleteCheck(1L)).thenReturn(ServiceResult.success());

        when(projectService.deleteProject(project)).thenReturn(ServiceResult.success());

        ListResult<ProjectView> listResult = new ListResult<>();
        List<ProjectView> list = Lists.newArrayList(ProjectView.builder().id(1L).groupName("tom").build());
        listResult.setList(list);
        Paging<ProjectModel> paging = new Paging<>();
        paging.setData(new ProjectModel());
        when(projectService.findProjectPageList(paging)).thenReturn(listResult);

        when(projectService.isNotExistByAppId(project)).thenReturn(true);

        when(projectService.isNotExistByName(project)).thenReturn(true);
    }

    @Test
    public void validatePermision() throws SQLException {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        Project project = Project.builder().id(1L).name("name").build();
        Errors errors = new BeanPropertyBindingResult(project, "project", true, 256);
        Assert.assertTrue(projectService.validatePermision(user, project, errors).validate().isValid());
    }

    @Test
    public void insertProjectTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.insertProject(project).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void updateProjectTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.updateProject(project).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteCheckTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.deleteCheck(1L).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void deleteProjectTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.deleteProject(project).getCode() == ServiceResult.SUCCESS);
    }

    @Test
    public void findProjectPageListTest() throws SQLException {
        Paging<ProjectModel> paging = new Paging<>();
        paging.setData(new ProjectModel());
        Assert.assertTrue(projectService.findProjectPageList(paging).getList().size() > 0);
    }

    @Test
    public void isNotExistByAppIdTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.isNotExistByAppId(project));
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        Project project = Project.builder().id(1L).name("name").build();
        Assert.assertTrue(projectService.isNotExistByName(project));
    }
}
