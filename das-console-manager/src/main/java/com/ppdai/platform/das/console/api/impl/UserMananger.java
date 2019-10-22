package com.ppdai.platform.das.console.api.impl;

import com.ppdai.platform.das.console.api.UserConfiguration;
import com.ppdai.platform.das.console.api.model.UserIdentity;
import com.ppdai.platform.das.console.constant.Consts;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.MenuItemModel;
import com.ppdai.platform.das.console.openapi.LoginProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserMananger implements UserConfiguration {

    @Autowired
    private Consts consts;

    @Autowired
    private LoginProvider loginProvider;

    @Override
    public boolean isUseSSO() {
        return StringUtils.isNotBlank(consts.ssologinUrl);
    }

    @Override
    public UserIdentity getUserIdentityByWorkName(LoginUser user, String name) throws Exception {
        return loginProvider.getUserIdentityByWorkName(name);
    }

    @Override
    public UserIdentity getUserIdentity(HttpServletRequest request, HttpServletResponse response) {
        return loginProvider.getUserIdentity(request, response);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
    }

    @Override
    public MenuItemModel getLinkItem(LoginUser user) {
        return null;
    }

    @Override
    public String fetchLoginUrl(HttpServletRequest request, HttpServletResponse response) {
        return consts.ssologinUrl;
    }

    @Override
    public Boolean fetchToken(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

}