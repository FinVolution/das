package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.common.utils.ResourceUtil;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.constant.CommMsg;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dto.model.ConnectionRequest;
import com.ppdai.platform.das.codegen.dto.model.InitDbUserRequset;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.service.SetupDataBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wang.liang on 2018/8/23.
 */

@Slf4j
@RestController
@RequestMapping(value = "/setupDb")
public class SetupDataBaseController {

    @Autowired
    private Consts consts;

    @Autowired
    private SetupDataBaseService setupDBService;

    @RequestMapping(value = "/setupDbCheck")
    public ServiceResult setupDbCheck() {
        try {
            boolean initialized = setupDBService.isInitialized();
            if (initialized) {
                return ServiceResult.success();
            }

            boolean valid = ResourceUtil.getSingleInstance().datasourceXmlValid();
            if (!valid) {
                return ServiceResult.fail("配置信息读取失败！");
            }

            if (valid && !initialized) {
                synchronized (SetupDataBaseService.LOCK) {
                    if (!initialized) {
                        setupDBService.setInitialized(true);
                    }
                }
                return ServiceResult.success("initialized");
            }
            return ServiceResult.success("initialized");
        } catch (Exception e) {
            log.error("SetupDBController.setupDbCheck : {}", StringUtil.getMessage(e));
            return ServiceResult.fail(CommMsg.loginFailMessage);
        }
    }

    @RequestMapping(value = "/connectionTest", method = RequestMethod.POST)
    public ServiceResult connectionTest(@RequestBody ConnectionRequest connectionRequest) {
        return setupDBService.connectionTest(connectionRequest);
    }

    @RequestMapping(value = "tableConsistentCheck", method = RequestMethod.POST)
    public ServiceResult tableConsistentCheck(@RequestBody ConnectionRequest connectionRequest) {
        try {
            //if (setupDBService.initializeDatasourceXml(connectionRequest.getDb_address(), connectionRequest.getDb_port(), connectionRequest.getDb_user(), connectionRequest.getDb_password(), connectionRequest.getDb_catalog())) {
            boolean result = setupDBService.tableConsistent(connectionRequest.getDb_catalog());
            if (result) {
                return ServiceResult.success();
            }
            //}
            return ServiceResult.fail();
        } catch (Exception e) {
            log.error("SetupDBController.tableConsistentCheck : {}", StringUtil.getMessage(e));
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }

    @RequestMapping(value = "initializeDb", method = RequestMethod.POST)
    public ServiceResult initializeDb(@RequestBody InitDbUserRequset initDbUserRequset) {
        try {
           /* if (!setupDBService.datasourceXmlValid() || !setupDBService.initializeDatasourceXml(initDbUserRequset.getDbaddress(), initDbUserRequset.getDbport(), initDbUserRequset.getDbuser(), initDbUserRequset.getDbpassword(), initDbUserRequset.getDbcatalog())) {
                return ServiceResult.fail("Error occured while initializing the jdbc.properties file.");
            }*/

            /*if (!setupDBService.setupTables()) {
                return ServiceResult.fail("Error occured while setting up the tables.");
            }

            DasGroup group = DasGroup.builder()
                    .group_name(initDbUserRequset.getGroupName())
                    .group_comment(initDbUserRequset.getGroupComment())
                    .build();

            LoginUser user = LoginUser.builder()
                    .userNo(initDbUserRequset.getAdminNo())
                    .userName(initDbUserRequset.getAdminName())
                    .userEmail(initDbUserRequset.getAdminEmail())
                    .password(MD5Util.parseStrToMd5L32(initDbUserRequset.getAdminPass()))
                    .build();*/

            /*if (!setupDBService.setupAdmin(group, user)) {
                return ServiceResult.fail("Error occured while setting up the admin.");
            }*/
            return ServiceResult.success();
        } catch (Exception e) {
            log.error("SetupDBController.initializeDb : {}", StringUtil.getMessage(e));
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }
}
