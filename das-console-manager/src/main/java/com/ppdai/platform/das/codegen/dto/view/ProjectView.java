package com.ppdai.platform.das.codegen.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectView {

    @Column(name = "id")
    private Long id;

    @Column(name = "app_group_id")
    private Integer app_group_id;

    @Column(name = "name")
    private String name;

    @Column(name = "namespace")
    private String namespace;

    @Column(name = "dal_group_id")
    private Integer dal_group_id;

    @Column(name = "dal_config_name")
    private String dal_config_name;

    @Column(name = "update_user_no")
    private String update_user_no;

    @Column(name = "user_real_name")
    private String userRealName;

    @Column(name = "project_users")
    private String projectUsers;

    @Column(name = "app_id")
    private String app_id;

    @Column(name = "app_scene")
    private String app_scene;

    @Column(name = "pre_release_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date pre_release_time;

    @Column(name = "first_release_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date first_release_time;

    @Column(name = "insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date insert_time;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date update_time;

    @Column(name = "dbset_ids")
    private String dbsetIds;

    @Column(name = "dbset_namees")
    private String dbsetNamees;

    @Column(name = "user_ids")
    private String userIds;

    @Column(name = "comment")
    private String comment;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "token")
    private String token;

    private List<String> pre_release_times;

    private List<String> first_release_times;

    private List<String> insert_times;

    public String getApp_scene() {
        if (StringUtils.isBlank(app_scene)) {
            return StringUtils.EMPTY;
        }
        return app_scene;
    }

    public List<String> getDbsetIds() {
        return StringUtil.toList(dbsetIds);
    }

    public List<String> getUserIds() {
        return StringUtil.toList(userIds);
    }

}
