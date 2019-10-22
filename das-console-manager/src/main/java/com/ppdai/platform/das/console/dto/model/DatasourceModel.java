package com.ppdai.platform.das.console.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasourceModel {

    private String name;
    private String userName;
    private String password;
    private String connectionUrl;
    private String driverClassName;
}
