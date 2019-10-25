package com.ppdai.das.console.controller;

import com.ppdai.das.console.api.SecurityConfiguration;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.dto.model.saec.CassecRequest;
import com.ppdai.das.console.dto.model.saec.DataIntancesRequest;
import com.ppdai.das.console.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@Slf4j
@RestController
@RequestMapping(value = "/saec")
public class SaecController {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    /**
     * 1、加密连接串
     */
    @RequestMapping(value = "/enconn", method = RequestMethod.POST)
    public ServiceResult<String> enconn(@RequestBody CassecRequest cassecRequest, @CurrentUser LoginUser user) throws Exception {
        Project project = projectDao.getProjectByAppId(cassecRequest.getAppId());
        return securityService.getSecurityToken(user, project);
    }

    /**
     * 1、加密连接串
     */
    @RequestMapping(value = "/getInstanceDetail", method = RequestMethod.POST)
    public ServiceResult<String> getInstanceDetail(@RequestBody DataIntancesRequest dataIntancesRequest, @CurrentUser LoginUser user) throws SQLException {
        Project project = projectDao.getProjectByAppId(dataIntancesRequest.getAppId());
        return securityService.getInstanceDetail(user, project);
    }
}
