package com.ppdai.das.console.cloud.controller;

import com.ppdai.das.console.cloud.dao.DataBaseCloudDao;
import com.ppdai.das.console.cloud.dto.view.DataBaseView;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.DatabaseService;
import com.ppdai.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/das/db")
public class DatabaseCloudController {

    @Resource
    private Message message;

    @Autowired
    private DataBaseCloudDao dataBaseCloudDao;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/getdbList")
    public ServiceResult<List<DataBaseView>> getdbnames(@RequestParam("appid") String appid) throws Exception {
        List<DataBaseView> list = dataBaseCloudDao.getAllDbByAppId(appid);
        return ServiceResult.success(list);
    }
}
