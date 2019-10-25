package com.ppdai.das.console.dto.model;

import com.ppdai.das.console.api.model.UserIdentity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdentityModel implements UserIdentity {

    private String workNumber;
    private String userName;
    private String userRealName;
    private String userEmail;
    private Boolean active;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getWorkNumber() {
        return workNumber;
    }

    @Override
    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String getUserRealName() {
        return userRealName;
    }

    @Override
    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }
}
