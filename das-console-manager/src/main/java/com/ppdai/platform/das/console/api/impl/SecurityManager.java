package com.ppdai.platform.das.console.api.impl;

import com.ppdai.platform.das.console.api.SecurityConfiguration;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;
import com.ppdai.platform.das.console.dto.model.Item;

public class SecurityManager implements SecurityConfiguration {

    @Override
    public Object getInstanceDetail(LoginUser user, Project project) {
        return new Item(1234L, "tom");
    }

    @Override
    public String getSecurityToken(LoginUser user, Project project) {
        return "token";
    }
}
