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
public class TaskTableView {

    @Column(name = "id")
    private Long id;

    @Column(name = "alldbs_id")
    private Long alldbs_id;

    @Column(name = "project_id")
    private Long project_id;

    @Column(name = "dbset_id")
    private Long dbset_id;

    @Column(name = "db_name")
    private String dbname;

    @Column(name = "dbset_name")
    private String dbsetName;

    @Column(name = "table_names")
    private String table_names;

    @Column(name = "view_names")
    private String view_names;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "cud_by_sp")
    private Boolean cud_by_sp;

    @Column(name = "pagination")
    private Boolean pagination;

    @Column(name = "generated")
    private Boolean generated;

    @Column(name = "version")
    private Integer version;

    @Column(name = "custom_table_name")
    private String custom_table_name;

    @Column(name = "update_user_no")
    private String update_user_no;

    @Column(name = "user_real_name")
    private String userRealName;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    @Column(name = "comment")
    private String comment;

    @Column(name = "sql_style")
    private String sql_style;

    @Column(name = "api_list")
    private String api_list;

    @Column(name = "approved")
    private Integer approved;

    @Column(name = "field_type")
    private Integer field_type;

    private String sp_names;

    private String str_approved;
}
