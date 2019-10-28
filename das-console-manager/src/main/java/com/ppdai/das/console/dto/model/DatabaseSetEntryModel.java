package com.ppdai.das.console.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.console.dto.view.search.CheckTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSetEntryModel {

    private Integer id;

    private String name;

    private Integer databaseType;

    private String sharding;

    private Integer db_Id;

    private Integer dbset_id;

    private String update_user_no;

    private String userRealName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    private String providerName;

    private String userName;

    private String password;

    private String dbAddress;

    private String dbPort;

    private String db_catalog;

    private Integer groupId;

    private String dbName;

    private String dbsetName;

    private CheckTypes databaseTypes;

}
