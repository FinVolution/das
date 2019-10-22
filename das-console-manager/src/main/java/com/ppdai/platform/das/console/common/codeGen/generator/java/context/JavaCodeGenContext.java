package com.ppdai.platform.das.console.common.codeGen.generator.java.context;

import com.ppdai.platform.das.console.common.codeGen.host.DalConfigHost;
import com.ppdai.platform.das.console.common.codeGen.host.java.*;
import com.ppdai.platform.das.console.common.codeGen.utils.Configuration;
import com.ppdai.platform.das.console.dto.entry.codeGen.Progress;
import com.ppdai.platform.das.console.dto.entry.das.TaskAuto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JavaCodeGenContext {
    protected Long projectId;
    protected boolean regenerate;
    private boolean ignoreApproveStatus;
    protected Progress progress;
    protected String namespace;
    public String generatePath;

    protected Queue<TaskAuto> sqlBuilders = new ConcurrentLinkedQueue<>();
    protected DalConfigHost dalConfigHost;

    protected Queue<JavaTableHost> tableHosts = new ConcurrentLinkedQueue<>();
    protected Queue<ViewHost> viewHosts = new ConcurrentLinkedQueue<>();
    // <SpDbHost dbName, SpDbHost>
    protected Map<Integer, SpDbHost> spHostMaps = new ConcurrentHashMap<>();
    protected Queue<SpHost> spHosts = new ConcurrentLinkedQueue<>();
    protected ContextHost contextHost = new ContextHost();
    protected Queue<FreeSqlHost> freeSqlHosts = new ConcurrentLinkedQueue<>();
    // <JavaMethodHost pojoClassName, JavaMethodHost>
    protected Map<String, JavaMethodHost> freeSqlPojoHosts = new ConcurrentHashMap<>();

    public JavaCodeGenContext(Long projectId, boolean regenerate, Progress progress) {
        this.projectId = projectId;
        this.regenerate = regenerate;
        this.progress = progress;
        this.generatePath = Configuration.get("gen_code_path");
    }
}
