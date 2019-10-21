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
public class LoginUserView {

    @Column(name="id")
    private Integer id;

    @Column(name="user_no")
    private String userNo;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="user_real_name")
    private String userRealName;

    @Column(name="update_user_name")
    private String updateUserName;

    @Column(name="group_name")
    private String groupName;

}


