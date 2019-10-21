package com.ppdai.platform.das.codegen.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectModel {

    private Integer id;

    private Integer app_group_id;

    private String name;

    private String namespace;

    private Integer dal_group_id;

    private String dal_config_name;

    private String update_user_no;

    private String userRealName;

    private String projectUsers;

    private String app_id;

    private String app_scene;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date pre_release_time;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date first_release_time;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date insert_time;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date update_time;

    private String dbsetIds;

    private String dbsetNamees;

    private String userIds;

    private String comment;

    private String groupName;

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
