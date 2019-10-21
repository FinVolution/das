package com.ppdai.platform.das.codegen.common.codeGen.resource;

import com.ppdai.platform.das.codegen.common.codeGen.entity.DefaultUserInfo;
import com.ppdai.platform.das.codegen.common.codeGen.utils.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class CustomizedResource {
    private static final Object LOCK = new Object();
    private ClassLoader classLoader = null;
    private final String CONF_PROPERTIES = "conf.properties";
    private final String USER_INFO_CLASS_NAME = "userinfo_class";
    private DefaultUserInfo userInfo = null;

    private final String CONFIG_CLASS_NAME = "config_class";

    private CustomizedResource() throws Exception {
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = Configuration.class.getClassLoader();
            }
            userInfo = getUserInfo();
        } catch (Throwable e) {
            throw e;
        }
    }

    private static CustomizedResource INSTANCE = null;

    public static CustomizedResource getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new CustomizedResource();
                }
            }
        }

        return INSTANCE;
    }

    public String getEmployee(String userNo) throws SQLException {
        if (userNo == null || userNo.isEmpty()){
            return userInfo.getEmployee(userNo);
        }
        return DefaultUserInfo.getInstance().getEmployee(userNo);
    }

    public String getName(String userNo) throws SQLException {
        if (userNo == null || userNo.isEmpty()){
            return userInfo.getName(userNo);
        }
        return DefaultUserInfo.getInstance().getName(userNo);
    }

    public String getMail(String userNo) throws SQLException {
        if (userNo == null || userNo.isEmpty()){
            return userInfo.getMail(userNo);
        }
        return DefaultUserInfo.getInstance().getMail(userNo);
    }

    private DefaultUserInfo getUserInfo() throws Exception {
        String className = getUserInfoClassName();
        if (className == null || className.isEmpty()){
            return DefaultUserInfo.getInstance(); // set to default
        }
        try {
            Class<?> clazz = Class.forName(className);
            return (DefaultUserInfo) clazz.newInstance();
        } catch (Throwable e) {
            throw e;
        }
    }

    private String getUserInfoClassName() throws IOException {
        return getClassNameFromConf(USER_INFO_CLASS_NAME);
    }

    public String getConfigClassName() throws IOException {
        return getClassNameFromConf(CONFIG_CLASS_NAME);
    }

    private String getClassNameFromConf(String className) throws IOException {
        if (className == null || className.isEmpty()){
            return null;
        }

        try {
            Properties properties = new Properties();
            InputStream inStream = classLoader.getResourceAsStream(CONF_PROPERTIES);
            properties.load(inStream);
            return properties.getProperty(className);
        } catch (Throwable e) {
            throw e;
        }
    }

}
