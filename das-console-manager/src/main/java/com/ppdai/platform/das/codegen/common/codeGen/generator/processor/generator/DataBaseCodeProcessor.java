package com.ppdai.platform.das.codegen.common.codeGen.generator.processor.generator;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.DataBaseGenContext;
import com.ppdai.platform.das.codegen.common.codeGen.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;

import java.io.File;

@Slf4j
public class DataBaseCodeProcessor {

    public void process(DataBaseGenContext ctx) throws Exception {
        try {
            Long projectId = ctx.getProjectId();
            File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));

            VelocityContext vltCcontext = GenUtils.buildDefaultVelocityContext();
            vltCcontext.put("host", ctx);
            GenUtils.mergeVelocityContext(vltCcontext, String.format("%s/db/datasource.xml", dir.getAbsolutePath()),
                    "templates/java/DataSource.java.tpl");
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
