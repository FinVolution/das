package com.ppdai.das.console.dto.model;

import com.ppdai.das.console.dto.view.search.CheckTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseModel {

    private Long id;

    private String dbname;

    private String comment;

    private Long dal_group_id;

    private String db_address;

    private String db_port;

    private String db_user;

    private String db_password;

    private String db_catalog;

    private Integer db_type;

    private String updateUserNo;

    private Date create_time;

    private Date update_time;

    private boolean addToGroup;

    private boolean isGenDefault;

    private Integer target_dal_group_id;

    private String group_name;

    private CheckTypes db_types;

    private List<String> insert_times;
}
