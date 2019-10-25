package com.ppdai.das.console.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Consts {

    public final Long SUPER_GROUP_ID = 1L;

    @Value("${test.permissions.validate.canManagerCRUDManager}")
    public boolean canManagerCRUDManager;

    @Value("${spring.evn.active}")
    public String springActive;

    @Value("${code.gen.file.path}")
    public String codeGenFilePath;

    @Value("${dbatool.file.path}")
    public String dbaToolFilePath;

    @Value("${application.isdev}")
    public boolean applicationIsdev;

    @Value("${openapi.help.userManualUrl}")
    public String userManualUrl;

    @Value("${openapi.help.adminMailAddress}")
    public String adminMailAddress;

    @Value("${openapi.sso.login.url}")
    public String ssologinUrl;

    @Value("${openapi.help.das.sync.target}")
    public String dasSyncTarget;
}
