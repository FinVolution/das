package com.ppdai.das.console.dto.entry.configCheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCheckResponse {

    //das数据
    private ConfigDataResponse das;

    //配置中心数据
    private ConfigDataResponse config;

}
