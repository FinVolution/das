package com.ppdai.das.console.common.utils;

import com.ppdai.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.das.console.common.codeGen.resource.CustomizedResource;
import com.ppdai.das.console.dto.entry.das.LoginUser;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RequestUtil {
    public static HttpSession getSession(ServletRequest request) {
        if (request == null){
            return null;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return httpRequest.getSession();
    }

    public static LoginUser getUserInfo(HttpServletRequest request) throws Exception {
        LoginUser user = null;
        HttpSession session = getSession(request);
        if (session == null){
            return user;
        }
        Object userInfo = session.getAttribute(CodeGenConsts.USER_INFO);
        if (userInfo != null) {
            user = (LoginUser) userInfo;
        } else {
            user = new LoginUser();
            user.setUserNo(CustomizedResource.getInstance().getEmployee(null));
            user.setUserName(CustomizedResource.getInstance().getName(null));
            user.setUserEmail(CustomizedResource.getInstance().getMail(null));
        }

        return user;
    }

    public static String getUserNo(HttpServletRequest request) throws Exception {
        String userNo = null;
        LoginUser user = getUserInfo(request);
        if (user != null){
            userNo = user.getUserNo();
        }
        return userNo;
    }

    public static Boolean isDefaultUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null){
            return result;
        }
        Object defaultUser = session.getAttribute(CodeGenConsts.DEFAULT_USER);
        if (defaultUser != null){
            result = (Boolean) defaultUser;
        }
        return result;
    }

    public static Boolean isSuperUser(HttpServletRequest request) {
        Boolean result = null;
        HttpSession session = getSession(request);
        if (session == null){
            return result;
        }
        Object superUser = session.getAttribute(CodeGenConsts.SUPER_USER);
        if (superUser != null){
            result = (Boolean) superUser;
        }
        return result;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
