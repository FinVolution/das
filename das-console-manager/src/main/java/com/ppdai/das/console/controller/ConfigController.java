package com.ppdai.das.console.controller;

import com.ppdai.das.console.api.DataBaseConfiguration;
import com.ppdai.das.console.api.DefaultConfiguration;
import com.ppdai.das.console.api.SyncConfiguration;
import com.ppdai.das.console.common.utils.DasEnv;
import com.ppdai.das.console.config.annotation.CurrentUser;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.ConsModel;
import com.ppdai.das.console.dto.model.DasEnvModel;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.service.ConfigService;
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
    private DataBaseConfiguration dataBaseConfiguration;

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
                    .cons(ConsModel.builder().dataBaseNameMaxLength(dataBaseConfiguration.getDataBaseNameMaxLength()).build())
                    .build();
            return ServiceResult.success(configService.removeSensitiveInfo(dasEnvModel));
        } catch (Exception e) {
            return ServiceResult.fail();
        }
    }
}
