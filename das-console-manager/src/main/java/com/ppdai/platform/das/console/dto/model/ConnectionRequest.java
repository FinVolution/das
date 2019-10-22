package com.ppdai.platform.das.console.dto.model;

import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequest {

    //connectionTest
    private Integer db_type; //1.mysql 2.sqlserver
    private String db_address;
    private String db_port;
    private String db_user;
    private String db_password;

    //connectionTest, tableConsistentCheck
    private String db_catalog;

    public ConnectionRequest(DataBaseInfo groupDb) {
        this.db_type = groupDb.getDb_type();
        this.db_address = groupDb.getDb_address();
        this.db_port = groupDb.getDb_port();
        this.db_user = groupDb.getDb_user();
        this.db_password = groupDb.getDb_password();
    }

}
