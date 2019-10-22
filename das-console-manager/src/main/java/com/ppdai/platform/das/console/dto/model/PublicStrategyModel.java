package com.ppdai.platform.das.console.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.platform.das.console.dto.view.search.CheckTypes;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublicStrategyModel {

    private Integer id;

    // 策略类型：1、静态加载的策略 2、动态加载策略
    private Integer strategyLoadingType;

    private String name;

    private String className;

    private String strategySource;

    private String strategyParams;

    private String comment;

    private String update_user_no;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private CheckTypes strategyLoadingTypes;
}
