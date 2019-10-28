package com.ppdai.das.console.common.codeGen.generator.processor.generator;

import com.ppdai.das.console.common.codeGen.generator.java.context.DbSetGenContext;
import com.ppdai.das.console.common.codeGen.utils.GenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;

import java.io.File;

@Slf4j
public class DbSetCodeProcessor {

    public void process(DbSetGenContext ctx) throws Exception {
        try {
            Long projectId = ctx.getProjectId();
            File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));

            VelocityContext vltCcontext = GenUtils.buildDefaultVelocityContext();
            vltCcontext.put("host", ctx);
            GenUtils.mergeVelocityContext(vltCcontext, String.format("%s/db/das.xml", dir.getAbsolutePath()), "templates/java/Das.config.java.tpl");
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
