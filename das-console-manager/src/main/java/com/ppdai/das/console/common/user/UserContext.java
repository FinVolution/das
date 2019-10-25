package com.ppdai.das.console.common.user;

import com.ppdai.das.console.dto.entry.das.LoginUser;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by liang.wang
 * 该类禁止在子线程内使用,子线程需要用户信息请将user传递进去
 */
public class UserContext {

    public static final String UNKNOWN = "unknown";
    public static final Long UNKNOWNID = 0L;
    public static final String CURRENTUSER = "currentUser";

    public static LoginUser getUser(HttpServletRequest request) {
        LoginUser user = (LoginUser) request.getSession().getAttribute(CURRENTUSER);
        if (user == null) {
            return LoginUser.builder()
                    .id(UNKNOWNID)
                    .userNo(UNKNOWN)
                    .userName(UNKNOWN)
                    .userEmail(UNKNOWN)
                    .build();
        }
        return user;
    }

    public static void setUser(HttpServletRequest request, LoginUser user) {
        request.setAttribute("isDasLogin", "false");
        request.getSession().setAttribute(CURRENTUSER, user);
    }

    public static void clear(HttpServletRequest request) {
        request.getSession().removeAttribute(CURRENTUSER);
    }

    public static void init(HttpServletRequest request){
        request.getSession().setAttribute("user", LoginUser.builder()
                .id(UNKNOWNID)
                .userNo(UNKNOWN)
                .userName(UNKNOWN)
                .userEmail(UNKNOWN)
                .build());
    }
}