package com.ppdai.das.console.common.codeGen.entity;

import com.ppdai.das.console.common.codeGen.utils.BeanGetter;
import com.ppdai.das.console.dto.entry.das.LoginUser;

import java.sql.SQLException;

public class DefaultUserInfo {
    private DefaultUserInfo() {
    }

    private static final DefaultUserInfo INSTANCE = new DefaultUserInfo();

    public static final DefaultUserInfo getInstance() {
        return INSTANCE;
    }

    private LoginUser getLoginUser(String userNo) throws SQLException {
        return BeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
    }

    public String getEmployee(String userNo) throws SQLException {
        String number = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            number = user.getUserNo();
        }
        return number;
    }

    public String getName(String userNo) throws SQLException {
        String name = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            name = user.getUserName();
        }
        return name;
    }

    public String getMail(String userNo) throws SQLException {
        String email = null;
        LoginUser user = getLoginUser(userNo);
        if (user != null) {
            email = user.getUserEmail();
        }
        return email;
    }
}
