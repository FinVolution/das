package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.PermissionDao;
import com.ppdai.platform.das.console.dao.ProjectDbsetRelationDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.ProjectDbsetRelation;
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

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, ProjectDbsetRelationDao.class, PermissionService.class, PermissionDao.class})
public class ProjectDbsetRelationServiceTest {

    @Mock
    private ProjectDbsetRelationService projectDbsetRelationService;

    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        ProjectDbsetRelation projectDbsetRelation = ProjectDbsetRelation.builder().dbsetId(1L).build();
        Errors errors = new BeanPropertyBindingResult(projectDbsetRelation, "projectDbsetRelation", true, 256);
        when(projectDbsetRelationService.validatePermision(user, projectDbsetRelation, errors)).thenReturn(chain);

        when(projectDbsetRelationService.isNotExistByName(projectDbsetRelation)).thenReturn(true);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        ProjectDbsetRelation projectDbsetRelation = ProjectDbsetRelation.builder().dbsetId(1L).build();
        Errors errors = new BeanPropertyBindingResult(projectDbsetRelation, "projectDbsetRelation", true, 256);
        Assert.assertTrue(projectDbsetRelationService.validatePermision(user, projectDbsetRelation, errors).validate().isValid());
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        ProjectDbsetRelation projectDbsetRelation = ProjectDbsetRelation.builder().dbsetId(1L).build();
        Assert.assertTrue(projectDbsetRelationService.isNotExistByName(projectDbsetRelation));
    }
}
