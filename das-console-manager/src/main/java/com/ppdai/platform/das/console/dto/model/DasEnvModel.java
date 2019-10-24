package com.ppdai.platform.das.console.dto.model;

import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DasEnvModel {

    private String configName;
    private String securityName;
    private String dasSyncTarget;
    private LoginUser user;
    private boolean isAdmin;
    private boolean isLocal;
    private boolean isDasLogin;
    private boolean isDev;
    private ConsModel cons;

}
