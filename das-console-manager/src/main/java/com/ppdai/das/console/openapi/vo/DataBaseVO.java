package com.ppdai.das.console.openapi.vo;

import com.ppdai.das.console.enums.DataBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseVO {

    private DataBaseEnum dataBaseEnum;  // 数据库类型 1、MySql 或 2.SqlServer
    private String dbName;
    private String userName;
    private String password;
    private String port;
    private String host;
}
