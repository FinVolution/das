package com.ppdai.das.console.common.codeGen.generator.java.generator;

import com.ppdai.das.console.common.codeGen.generator.java.context.DbSetGenContext;
import com.ppdai.das.console.common.codeGen.generator.processor.generator.DbSetCodeProcessor;
import com.ppdai.das.console.common.codeGen.generator.processor.prepareData.DbSetDataProcessor;
import com.ppdai.das.console.common.codeGen.generator.processor.prepareDirectory.DirectoryPreparerProcessor;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.das.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DbSetGenerator {

    public DbSetGenContext createContext(String appId, Long projectId, boolean regenerate) throws Exception {
        DbSetGenContext ctx = new DbSetGenContext(appId, projectId, regenerate);
        try {
            Project project = new ProjectDao().getProjectByID(projectId);
            ctx.setNamespace(project.getNamespace());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return ctx;
    }

    public void prepareDirectory(DbSetGenContext ctx) throws Exception {
        DirectoryPreparerProcessor.process(ctx);
    }

    public void prepareData(DbSetGenContext ctx) throws Exception {
        new DbSetDataProcessor().process(ctx);
    }

    public void generateCode(DbSetGenContext ctx) throws Exception {
        new DbSetCodeProcessor().process(ctx);
    }

}
