package com.ppdai.platform.das.console.dto.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseView {

    @Column(name="id")
    private Integer id;

    @Column(name="db_name")
    private String dbname;

    @Column(name="dal_group_id")
    private Integer dal_group_id;

    @Column(name="db_address")
    private String db_address;

    @Column(name="db_port")
    private String db_port;

    @Column(name="db_user")
    private String db_user;

    @Column(name="db_password")
    private String db_password;

    @Column(name="db_catalog")
    private String db_catalog;

    @Column(name="db_type")
    private Integer db_type;

    @Column(name="comment")
    private String comment;

    @Column(name="group_name")
    private String group_name;

    @Column(name="insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insert_time;

    @Column(name="user_real_name")
    private String userRealName;

    @Column(name="update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;


    public String getDbname() {
        if (StringUtils.isNotBlank(dbname)) {
            return dbname.trim();
        }
        return dbname;
    }

    public String getDb_catalog() {
        if (StringUtils.isNotBlank(db_catalog)) {
            return db_catalog.trim();
        }
        return db_catalog;
    }
}

