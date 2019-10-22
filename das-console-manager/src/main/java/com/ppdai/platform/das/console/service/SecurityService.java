package com.ppdai.platform.das.console.service;


import com.ppdai.platform.das.console.api.SecurityConfiguration;
import com.ppdai.platform.das.console.common.utils.JsonUtil;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.Project;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private SecurityConfiguration securityConfiguration;

    public ServiceResult getInstanceDetail(LoginUser user, Project project) {
        try {
            Object obj = securityConfiguration.getInstanceDetail(user, project);
            String str = JsonUtil.toJSONString(obj);
            return ServiceResult.success(JsonUtil.toFormat(str, true, true));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }

    public ServiceResult<String> getSecurityToken(LoginUser user, Project project) {
        try {
            return ServiceResult.success(securityConfiguration.getSecurityToken(user, project));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }
}
