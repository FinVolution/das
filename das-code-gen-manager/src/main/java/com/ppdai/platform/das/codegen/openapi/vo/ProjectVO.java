package com.ppdai.platform.das.codegen.openapi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO {

    private String appId;

    private String projectName;

    private String teamName;

    private List<String> databaseSetNames;
}
