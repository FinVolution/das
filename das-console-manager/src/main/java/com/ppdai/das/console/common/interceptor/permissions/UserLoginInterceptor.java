package com.ppdai.das.console.common.interceptor.permissions;

import com.ppdai.das.console.api.UserConfiguration;
import com.ppdai.das.console.common.user.UserContext;
import com.ppdai.das.console.common.utils.HttpServletUtil;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.ServiceResult;
import com.ppdai.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liang.wang on 2018/8/23.
 */
@Slf4j
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserConfiguration userConfiguration;

    @Autowired
    private LoginUserDao loginUserDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(PermissionService.getSUPERID().equals(UserContext.getUser(request).getId())){
            return true;
        }
        // 统一登录
        if (userConfiguration.isUseSSO()) {
            String userName = userConfiguration.getUserIdentity(request, response).getUserName();
            if (StringUtils.isNotBlank(userName)) {
                LoginUser loginUser = loginUserDao.getUserByUserName(userName);
                if (null != loginUser) {
                    return true;
                }
            }
            HttpServletUtil.returnErrorResponse(response, ServiceResult.fail("请先登录! " + userConfiguration.fetchLoginUrl(request, response)));
            return false;
        } else {
            LoginUser user = (LoginUser) request.getSession().getAttribute("currentUser");
            if (null == user || UserContext.UNKNOWN.equals(user.getUserNo())) {
                HttpServletUtil.returnErrorResponse(response, ServiceResult.fail("请先登录！"));
                return false;
            }
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0 || cookieName == null) {
            return StringUtils.EMPTY;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

}