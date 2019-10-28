package com.ppdai.das.console.common.utils;

import com.google.common.base.Splitter;
import com.ppdai.das.console.api.AdditionalConfiguration;
import com.ppdai.das.console.api.DefaultConfiguration;
import com.ppdai.das.console.api.EncdecConfiguration;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by liang.wang on 18/9/19.
 */
public abstract class DasEnv {

    @Setter
    public static DefaultConfiguration defaultConfiguration;

    @Setter
    public static AdditionalConfiguration additionalConfiguration;

    @Setter
    public static EncdecConfiguration encdecConfiguration;

    public static boolean isLocal(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        return requestURL.contains("localhost") || requestURL.contains("127.0.0.1");
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        List<String> list = Splitter.on("/").omitEmptyStrings().trimResults().splitToList(url);
        return list.get(1);
    }

    public static boolean isNeedDasLogin(HttpServletRequest request, String isNeedDasLogin) {
        Object _isNeedDasLogin = request.getSession().getAttribute(isNeedDasLogin);
        return Boolean.parseBoolean(_isNeedDasLogin != null ? _isNeedDasLogin.toString() : "false");
    }

    public static String getConfigCenterName() {
        if (defaultConfiguration != null) {
            return defaultConfiguration.getConfigCenterName();
        }
        return "配置中心";
    }
}
