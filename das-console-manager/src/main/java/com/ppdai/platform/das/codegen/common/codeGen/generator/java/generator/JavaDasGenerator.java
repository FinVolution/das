package com.ppdai.platform.das.codegen.common.codeGen.generator.java.generator;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.JavaCodeGenContext;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.generator.FreeSqlCodeProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.generator.TableCodeProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareData.FreeSqlDataProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareData.TableDataProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareDirectory.DirectoryPreparerProcessor;
import com.ppdai.platform.das.codegen.common.codeGen.host.DalConfigHost;
import com.ppdai.platform.das.codegen.dao.ProjectDao;
import com.ppdai.platform.das.codegen.dto.entry.codeGen.Progress;
import com.ppdai.platform.das.codegen.dto.entry.das.Project;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class JavaDasGenerator {

    public JavaCodeGenContext createContext(Long projectId, boolean regenerate, Progress progress) throws Exception {
        JavaCodeGenContext ctx;
        try {
            ctx = new JavaCodeGenContext(projectId, regenerate, progress);
            Project project = new ProjectDao().getProjectByID(projectId);
            DalConfigHost dalConfigHost;
            if (StringUtils.isNotBlank(project.getDal_config_name())) {
                dalConfigHost = new DalConfigHost(project.getDal_config_name());
            } else if (StringUtils.isNotBlank(project.getNamespace())) {
                dalConfigHost = new DalConfigHost(project.getNamespace());
            } else {
                dalConfigHost = new DalConfigHost("");
            }
            ctx.setDalConfigHost(dalConfigHost);
            ctx.setNamespace(project.getNamespace());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return ctx;
    }

    public void prepareDirectory(JavaCodeGenContext ctx) throws Exception {
        DirectoryPreparerProcessor.process(ctx);
    }

    public void prepareData(JavaCodeGenContext ctx) throws Exception {
        new TableDataProcessor().process(ctx);
        new FreeSqlDataProcessor().process(ctx);
    }

    public void generateCode(JavaCodeGenContext ctx) throws Exception {
        new TableCodeProcessor().process(ctx);
        new FreeSqlCodeProcessor().process(ctx);
    }

}
