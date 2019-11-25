package com.ppdai.das.console.cloud.dto.view;

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

    @Column(name="db_name")
    private String db_name;

    @Column(name="db_address")
    private String db_address;

    @Column(name="db_port")
    private String db_port;

    @Column(name="db_catalog")
    private String db_catalog;

    @Column(name="db_type")
    private String db_type;

    @Column(name="group_name")
    private String group_name;

    @Column(name="insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insert_time;


    public String getDb_name() {
        if (StringUtils.isNotBlank(db_name)) {
            return db_name.trim();
        }
        return db_name;
    }

    public String getDb_catalog() {
        if (StringUtils.isNotBlank(db_catalog)) {
            return db_catalog.trim();
        }
        return db_catalog;
    }
}

