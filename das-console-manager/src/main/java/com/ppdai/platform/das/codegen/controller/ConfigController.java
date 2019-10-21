package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.api.DefaultConfiguration;
import com.ppdai.platform.das.codegen.api.SyncConfiguration;
import com.ppdai.platform.das.codegen.common.utils.DasEnv;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.DasEnvModel;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.service.ConfigService;
import com.ppdai.platform.das.codegen.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(value = "/config")
public class ConfigController {

    @Autowired
    private Consts consts;

    @Autowired
    private ConfigService configService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    @Autowired
    private SyncConfiguration syncConfiguration;

    @RequestMapping(value = "/datasourceValid", method = RequestMethod.GET)
    public ServiceResult<String> datasourceValid() {
        return configService.ckeckLoader();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ServiceResult<String> add(@RequestBody DataBaseInfo dataBaseInfo) {
        return configService.addConfig(dataBaseInfo);
    }

    @RequestMapping(value = "/env")
    public ServiceResult<String> env(HttpServletRequest request, @CurrentUser LoginUser user) {
        try {
            DasEnvModel dasEnvModel = DasEnvModel.builder()
                    .configName(defaultConfiguration.getConfigCenterName())
                    .securityName(defaultConfiguration.getSecurityCenterName())
                    .dasSyncTarget(syncConfiguration.getSyncUrl())
                    .user(user)
                    .isDasLogin(DasEnv.isNeedDasLogin(request, "isConfigNeedDasLogin"))
                    .isAdmin(permissionService.isManagerById(user.getId()))
                    .isLocal(DasEnv.isLocal(request))
                    .isDev(consts.applicationIsdev)
                    .build();
            return ServiceResult.success(configService.removeSensitiveInfo(dasEnvModel));
        } catch (Exception e) {
            return ServiceResult.fail();
        }
    }
}
