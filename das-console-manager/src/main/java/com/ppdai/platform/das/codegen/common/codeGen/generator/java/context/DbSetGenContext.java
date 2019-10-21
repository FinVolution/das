package com.ppdai.platform.das.codegen.common.codeGen.generator.java.context;

import com.ppdai.platform.das.codegen.dto.entry.codeGen.Progress;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbSetGenContext {

    private String name;
    private String appId;
    protected Long projectId;
    protected boolean regenerate;
    private boolean ignoreApproveStatus;
    protected Progress progress;
    protected String namespace;
    public String generatePath;
    List<DatabaseSetView> databaseSets;

    public DbSetGenContext(String appId, Long projectId, boolean regenerate) {
        this.appId = appId;
        this.projectId = projectId;
        this.regenerate = regenerate;
    }
}
