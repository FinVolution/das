package com.ppdai.platform.das.console.dto.model;

import com.ppdai.platform.das.console.dto.view.search.CheckTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSetModel {

    private Integer id;

    private String name;

    private Integer dbType;

    private String className;

    private Integer groupId;

    private Integer strategyType;

    private String strategySource;

    private Integer dynamicStrategyId;

    private String updateUserNo;

    private Date create_time;

    private Date update_time;

    private String groupName;

    private CheckTypes dbTypes;

    private CheckTypes strategyTypes;

    private Long app_id;
}
