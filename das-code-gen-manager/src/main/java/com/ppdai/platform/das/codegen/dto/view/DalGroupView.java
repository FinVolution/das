package com.ppdai.platform.das.codegen.dto.view;

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
public class DalGroupView  {

    @Column(name = "id")
    private Long id;

    @Column(name = "group_name")
    private String group_name;

    @Column(name = "group_comment")
    private String group_comment;

    @Column(name = "update_user_no")
    private String update_user_no;

    @Column(name = "user_real_name")
    private String userRealName;

    @Column(name = "insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insert_time;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;
}
