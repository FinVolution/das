package com.ppdai.das.console.api.impl;

import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.Item;
import com.ppdai.das.console.api.SecurityConfiguration;

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
