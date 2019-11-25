package com.ppdai.das.console.cloud.service;

import com.ppdai.das.console.cloud.dao.ProjectCloudDao;
import com.ppdai.das.console.cloud.dto.view.ProjectView;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectCloudService {

    @Autowired
    private ProjectCloudDao projectCloudDao;

    public List<String> getAppidListByWorkName(String workname) throws SQLException {
        if (StringUtils.isBlank(workname)) {
            return ListUtils.EMPTY_LIST;
        }
        List<ProjectView> list = projectCloudDao.getProjectsByWorkName(workname);
        return list.stream().map(i -> i.getApp_id()).collect(Collectors.toList());
    }
}
