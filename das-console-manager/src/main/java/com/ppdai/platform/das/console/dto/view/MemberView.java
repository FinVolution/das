package com.ppdai.platform.das.console.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberView {

    @Column(name="id")
    private Integer id;

    @Column(name = "user_id")
    private Long user_id;

    @Column(name="group_id")
    private Long group_id;

    @Column(name="user_no")
    private String userNo;

    @Column(name="user_name")
    private String userName;

    @Column(name="user_email")
    private String userEmail;

    @Column(name="role")
    private Integer role;

    @Column(name="opt_user")
    private Integer opt_user;

    @Column(name="user_real_name")
    private String userRealName;

    @Column(name="update_user_name")
    private String update_user_name;

    @Column(name="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

}


