package com.ppdai.platform.das.codegen.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUsersView {

    @Column(name="id")
    private Integer id;

    @Column(name="user_no")
    private String userNo;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_real_name")
    private String userRealName;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="password")
    private String password;

    @Column(name="role")
    private Integer role;

    @Column(name="opt_user")
    private Integer optUser;

}


