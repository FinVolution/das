package com.ppdai.das.console.api;

import com.ppdai.das.console.api.model.UserIdentity;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.MenuItemModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接入统一登录
 */
public interface UserConfiguration {

    /**
     * 是否接入统一登录系统，如果接入需要实现当前接口的其他所有方法
     */
    boolean isUseSSO();

    /**
     * 系统管理 --> 用户管理 添加域账号到DAS，根据域账号获取用户信息
     * 如果使用统一登录无需注册新用户，用户信息可以从
     *
     * @param name
     * @return
     */
    UserIdentity getUserIdentityByWorkName(LoginUser user, String name) throws Exception;

    /**
     * 获取当前统一登录用户信息
     *
     * @param request
     * @return
     */
    UserIdentity getUserIdentity(HttpServletRequest request, HttpServletResponse response);

    /**
     * 退出登录
     *
     * @param request
     * @param response
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 未登录提醒页面，当前用户未添加到das时的提醒
     *
     * @param user
     * @return
     */
    MenuItemModel getLinkItem(LoginUser user);

    /**
     * 统一登录的登录地址
     *
     * @param request
     * @param response
     * @return
     */
    String fetchLoginUrl(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取身份识别token，存储到cookie或session
     *
     * @param request
     * @param response
     * @return
     */
    Boolean fetchToken(HttpServletRequest request, HttpServletResponse response);

}
