package com.ppdai.platform.das.codegen.openapi.vo;

import com.ppdai.platform.das.codegen.enums.DataBaseEnum;
import com.ppdai.platform.das.codegen.enums.StrategyTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSetVO {

    /**
     * 逻辑库名
     */
    private String databaseSetName;

    /**
     * 数据库类型 1、MySql 或 2.SqlServer
     */
    private DataBaseEnum dataBaseType;

    /**
     * 策略类型
     */
    private StrategyTypeEnum strategyType;

    /**
     * 无策略时，策略名为空
     */
    private String StrategyClassName;

    /**
     * sharing 策略信息
     */
    private Map<String, String> shardingStrategy;
}
