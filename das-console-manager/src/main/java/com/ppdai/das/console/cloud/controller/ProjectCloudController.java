package com.ppdai.das.console.cloud.controller;

import com.ppdai.das.console.cloud.dto.view.ProjectView;
import com.ppdai.das.console.cloud.service.ProjectCloudService;
import com.ppdai.das.console.dto.model.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/das/project")
public class ProjectCloudController {

    @Autowired
    private ProjectCloudService projectCloudService;

    @RequestMapping(value = "/getAppidList")
    public ServiceResult<List<ProjectView>> getAppidListByWorkName(@RequestParam(value = "name", defaultValue = "") String name) throws SQLException {
        return ServiceResult.success(projectCloudService.getAppidListByWorkName(name));
    }

}
