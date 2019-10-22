package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.dao.DeleteCheckDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Slf4j
@Service
public class CodeService {

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    public boolean isTaskCountByProjectId(Long projectId) throws SQLException {
        return deleteCheckDao.isProjectIdInTaskTable(projectId) || deleteCheckDao.isProjectIdInTaskSQL(projectId);
    }


}