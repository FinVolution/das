package com.ppdai.platform.das.codegen.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitDbUserRequset{

    private String dbaddress;
    private String dbport;
    private String dbuser;
    private String dbpassword;
    private String dbcatalog;
    private String groupName;
    private Long groupAppid;
    private String groupComment;
    private String adminNo;
    private String adminName;
    private String adminEmail;
    private String adminPass;

}
