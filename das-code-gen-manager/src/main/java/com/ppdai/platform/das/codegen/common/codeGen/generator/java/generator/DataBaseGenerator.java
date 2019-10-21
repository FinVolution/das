package com.ppdai.platform.das.codegen.common.codeGen.generator.java.generator;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.DataBaseGenContext;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.generator.DataBaseCodeProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareData.DataBaseDataProcessor;
import com.ppdai.platform.das.codegen.dao.ProjectDao;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataBaseGenerator {

    public DataBaseGenContext createContext(String appId, Long projectId, boolean regenerate) throws Exception {
        DataBaseGenContext ctx = new DataBaseGenContext(appId, projectId, regenerate);
        try {
            Project project = new ProjectDao().getProjectByID(projectId);
            ctx.setNamespace(project.getNamespace());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return ctx;
    }

    public void prepareData(DataBaseGenContext ctx) throws Exception {
        new DataBaseDataProcessor().process(ctx);
    }

    public void generateCode(DataBaseGenContext ctx) throws Exception {
        new DataBaseCodeProcessor().process(ctx);
    }

}
