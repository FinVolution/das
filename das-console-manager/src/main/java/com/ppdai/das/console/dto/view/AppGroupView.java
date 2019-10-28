package com.ppdai.das.console.dto.view;

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
public class AppGroupView {

    @Column(name="id")
    private Integer id;

    @Column(name="server_enabled")
    private Integer serverEnabled;

    @Column(name="server_group_id")
    private Integer serverGroupId;

    @Column(name="name")
    private String name;

    @Column(name="comment")
    private String comment;

    @Column(name="insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertTime;

    @Column(name="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Column(name="server_group_name")
    private String serverGroupName;

    @Column(name="project_names")
    private String projectNames;

    @Column(name="project_ids")
    private String projectIds;

    @Column(name="user_real_name")
    private String userRealName;

}

