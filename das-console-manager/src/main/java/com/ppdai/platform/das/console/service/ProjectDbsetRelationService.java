package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.dao.ProjectDbsetRelationDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.ProjectDbsetRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;

@Service
public class ProjectDbsetRelationService {

    @Autowired
    private ProjectDbsetRelationDao projectDbsetRelationDao;

    public ValidatorChain validatePermision(LoginUser user, ProjectDbsetRelation projectDbsetRelation, Errors errors) throws SQLException {
        return validatePermision(user.getId(), projectDbsetRelation.getProjectId(), errors);
    }

    private ValidatorChain validatePermision(Long userId, Long groupId, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors);
    }

    public boolean isNotExistByName(ProjectDbsetRelation projectDbsetRelation) throws SQLException {
        Long n = projectDbsetRelationDao.getCountByRelation(projectDbsetRelation.getDbsetId(), projectDbsetRelation.getProjectId());
        if (n == 0) {
            return true;
        }
        return false;
    }

}
