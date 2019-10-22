package com.ppdai.platform.das.console.openapi;

import com.ppdai.platform.das.console.api.model.UserIdentity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 如果接入统一登录需要实现的接口
 */
public interface LoginProvider {

    /**
     * 系统管理 --> 用户管理 添加域账号到DAS，根据域账号获取用户信息
     * 如果使用统一登录无需注册新用户，管理员从查询接口获取用户信息并添加到DAS
     *
     * @param name
     * @return
     */
    UserIdentity getUserIdentityByWorkName(String name) throws Exception;

    /**
     * 获取当前统一登录用户信息
     *
     * @param request
     * @return
     */
    UserIdentity getUserIdentity(HttpServletRequest request, HttpServletResponse response);

}
