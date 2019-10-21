package com.ppdai.platform.das.codegen.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.platform.das.codegen.common.configCenter.ConfigCenterCons;
import com.ppdai.platform.das.codegen.enums.DbMasterSlaveEnum;
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
public class DatabaseSetEntryView {

    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "database_type")
    private Integer databaseType;

    @Column(name = "sharding")
    private String sharding;

    @Column(name = "db_Id")
    private Long db_Id;

    @Column(name = "dbset_id")
    private Long dbset_id;

    @Column(name = "update_user_no")
    private String update_user_no;

    @Column(name = "user_real_name")
    private String userRealName;

    @Column(name = "insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time;

    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;

    @Column(name = "db_catalog")
    private String db_catalog;

    @Column(name = "groupId")
    private Integer groupId;

    @Column(name = "db_name")
    private String dbName;

    @Column(name = "dbset_name")
    private String dbsetName;

    public String getConnectionString() {
        return ConfigCenterCons.getNameSpaceByDataBase(databaseType, db_catalog);
    }

    public String getDatabaseTypeName(){
        return DbMasterSlaveEnum.getDbMasterSlaveEnumByType(databaseType).getName();
    }
}
