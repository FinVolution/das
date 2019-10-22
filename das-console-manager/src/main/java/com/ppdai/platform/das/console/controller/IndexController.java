package com.ppdai.platform.das.console.controller;

import com.ppdai.platform.das.console.api.DefaultConfiguration;
import com.ppdai.platform.das.console.api.SyncConfiguration;
import com.ppdai.platform.das.console.api.UserConfiguration;
import com.ppdai.platform.das.console.api.model.UserIdentity;
import com.ppdai.platform.das.console.common.exceptions.InitCheckException;
import com.ppdai.platform.das.console.common.user.UserContext;
import com.ppdai.platform.das.console.common.utils.DasEnv;
import com.ppdai.platform.das.console.common.utils.JsonUtil;
import com.ppdai.platform.das.console.common.utils.ResourceUtil;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.constant.Consts;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liang.wang.sh on 2018/9/20.
 */
@Slf4j
@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private Consts consts;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    @Autowired
    private SyncConfiguration syncConfiguration;

    @Autowired
    private UserConfiguration userConfiguration;

    @RequestMapping(value = {"", "/", "/index", "/das", "/das/", "/index/", "/index.html"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, @CurrentUser LoginUser loginUser, ModelAndView mv) {
        try {
            request.setAttribute("isDasLogin", "false");
            if (!islogin(request)) {
                boolean isNeedDasLogin = DasEnv.isNeedDasLogin(request, "isNeedDasLogin");
                if (!userConfiguration.isUseSSO()) {
                    if (!ResourceUtil.getSingleInstance().isDasExist() || !ResourceUtil.getSingleInstance().isDatasourceExist() || !ResourceUtil.getSingleInstance().datasourceXmlValid()) {
                        this.initUser(request);
                        return "index";
                    }
                    if (null == loginUser || loginUser.getId().equals(UserContext.UNKNOWNID)) {
                        request.setAttribute("isDasLogin", "true");
                        request.getSession().setAttribute("isConfigNeedDasLogin", "true");
                    }
                } else {
                    String loginUrl = userConfiguration.fetchLoginUrl(request, response);
                    UserIdentity userIdentity = userConfiguration.getUserIdentity(request, response);
                    if (isNeedDasLogin) {
                        request.getSession().removeAttribute("isNeedDasLogin");
                        request.setAttribute("isDasLogin", "true");
                    } else {
                        if (userIdentity == null || StringUtils.isBlank(userIdentity.getUserName()) || StringUtils.isBlank(userIdentity.getUserEmail())) {
                            request.getSession().setAttribute("isDasLogin", "false");
                            return "redirect:" + loginUrl;
                        }
                        if (StringUtils.isNotBlank(userIdentity.getUserName())) {
                            loginUser = loginUserDao.getUserByUserName(userIdentity.getUserName());
                            if (null == loginUser) {
                                return "err/404/index.html";
                            } else {
                                UserContext.setUser(request, loginUser);
                            }
                        }
                    }
                }
            }
            request.setAttribute("isAdmin", String.valueOf(permissionService.isManagerById(loginUser.getId())));
            request.setAttribute("configName", defaultConfiguration.getConfigCenterName());
            request.setAttribute("securityName", defaultConfiguration.getSecurityCenterName());
            request.setAttribute("dasSyncTarget", syncConfiguration.getSyncUrl());
            request.setAttribute("user", JsonUtil.toJSONString(loginUser));
            request.setAttribute("isLocal", DasEnv.isLocal(request));
            request.setAttribute("isDev", consts.applicationIsdev);
            return "index";
        } catch (Exception e) {
            e.printStackTrace();
            throw new InitCheckException("初始化数据不通过");
            //return "redirect:init";
        }
    }

    /**
     * 无论是否接入统一登录，先退出，再登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/login", "/login/"}, method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response) {
        UserContext.clear(request);
        if (userConfiguration.isUseSSO()) {
            userConfiguration.logout(request, response);
        }
        this.initUser(request);
        request.setAttribute("isDasLogin", "true");
        request.setAttribute("isAdmin", "false");
        request.getSession().setAttribute("isNeedDasLogin", "true");
        request.getSession().setAttribute("isConfigNeedDasLogin", "true");
        return "redirect:index";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        UserContext.clear(request);
        if (userConfiguration.isUseSSO()) {
            userConfiguration.logout(request, response);
            String loginUrl = userConfiguration.fetchLoginUrl(request, response);
            return "redirect:" + loginUrl;
        }
        this.initUser(request);
        request.setAttribute("isAdmin", "false");
        request.setAttribute("isDasLogin", "true");
        return "redirect:login";
    }

    @RequestMapping(value = "/fetchToken")
    public String fetchToken(HttpServletRequest request, HttpServletResponse response) {
        userConfiguration.fetchToken(request, response);
        return "redirect:index";
    }

    private boolean islogin(HttpServletRequest request) {
        try {
            LoginUser loginUser = UserContext.getUser(request);
            loginUser = loginUserDao.getUserById(loginUser.getId());
            if (loginUser != null && !UserContext.UNKNOWN.equals(loginUser.getUserName())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void initUser(HttpServletRequest request) {
        request.setAttribute("isAdmin", "true");
        request.setAttribute("dasSyncTarget", syncConfiguration.getSyncUrl());
        request.setAttribute("configName", defaultConfiguration.getConfigCenterName());
        request.setAttribute("securityName", defaultConfiguration.getSecurityCenterName());
        request.setAttribute("user", JsonUtil.toJSONString(LoginUser.builder().id(PermissionService.getSUPERID()).build()));
        request.setAttribute("isLocal", DasEnv.isLocal(request));
        request.setAttribute("isDasLogin", "false");
        request.setAttribute("isDev", consts.applicationIsdev);
    }

}
