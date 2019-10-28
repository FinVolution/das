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
public class TaskSqlView {

    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long project_id;

    @Column(name = "dbset_id")
    private Long dbset_id;

    //databaseSetName
    @Column(name = "dbset_name")
    private String dbsetName;

    @Column(name = "class_name")
    private String class_name;

    @Column(name = "sql_content")
    private String sql_content;

    @Column(name = "pojo_name")
    private String pojo_name;

    @Column(name = "method_name")
    private String method_name;

    @Column(name = "crud_type")
    private String crud_type;

    @Column(name = "parameters")
    private String parameters;

    @Column(name = "generated")
    private Boolean generated;

    @Column(name = "version")
    private Integer version;

    @Column(name = "update_user_no")
    private String update_user_no;

    @Column(name = "user_real_name")
    private String userRealName;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    @Column(name = "comment")
    private String comment;

    @Column(name = "scalarType")
    private String scalarType;

    @Column(name = "pojoType")
    private String pojoType;

    @Column(name = "pagination")
    private Boolean pagination;

    @Column(name = "sql_style")
    private String sql_style;

    @Column(name = "approved")
    private Integer approved;

    @Column(name = "approveMsg")
    private String approveMsg;

    @Column(name = "hints")
    private String hints;

    @Column(name = "field_type")
    private Integer field_type;

    private Long alldbs_id;

    private String str_approved;

    private String str_update_time;

}
