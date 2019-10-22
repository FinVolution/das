package com.ppdai.platform.das.console.openapi.impl;

import com.ppdai.platform.das.console.api.model.UserIdentity;
import com.ppdai.platform.das.console.openapi.LoginProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginProviderDefaultImpl implements LoginProvider {

    @Override
    public UserIdentity getUserIdentityByWorkName(String name) throws Exception {
        return null;
    }

    @Override
    public UserIdentity getUserIdentity(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
