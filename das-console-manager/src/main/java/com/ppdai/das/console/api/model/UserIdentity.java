package com.ppdai.das.console.api.model;

public interface UserIdentity {

    /**
     * 工号
     *
     * @return
     */
    String getWorkNumber();

    void setWorkNumber(String workNumber);

    /**
     * 域账号（一般统一登录的唯一账号，例如：zhangsan007）
     *
     * @return
     */
    String getUserName();

    void setUserName(String userName);

    /**
     * 中文真实姓名
     *
     * @return
     */
    String getUserRealName();

    void setUserRealName(String userRealName);


    String getUserEmail();

    void setUserEmail(String userEmail);

    /**
     * 是否在职
     *
     * @return
     */
    Boolean getActive();

    void setActive(Boolean active);

}
