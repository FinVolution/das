package com.ppdai.platform.das.codegen.common.codeGen.generator.java.context;

import com.ppdai.platform.das.codegen.dto.entry.codeGen.Progress;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataBaseGenContext {

    private String name;
    private String appId;
    protected Long projectId;
    protected boolean regenerate;
    private boolean ignoreApproveStatus;
    protected Progress progress;
    protected String namespace;
    public String generatePath;
    List<DataBaseInfo> dbs;

    public DataBaseGenContext(String appId, Long projectId, boolean regenerate) {
        this.appId = appId;
        this.projectId = projectId;
        this.regenerate = regenerate;
    }
}
