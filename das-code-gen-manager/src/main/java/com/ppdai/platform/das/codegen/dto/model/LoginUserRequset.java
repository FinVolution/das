package com.ppdai.platform.das.codegen.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequset {

    private Integer id;

    private String userNo;

    private String userName;

    private String userEmail;

    private String password;

}
