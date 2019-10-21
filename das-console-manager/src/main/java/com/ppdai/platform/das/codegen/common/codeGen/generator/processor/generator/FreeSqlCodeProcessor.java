package com.ppdai.platform.das.codegen.common.codeGen.generator.processor.generator;

import com.ppdai.platform.das.codegen.common.codeGen.entity.ExecuteResult;
import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.JavaCodeGenContext;
import com.ppdai.platform.das.codegen.common.codeGen.host.java.JavaMethodHost;
import com.ppdai.platform.das.codegen.common.codeGen.utils.GenUtils;
import com.ppdai.platform.das.codegen.dto.entry.codeGen.Progress;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Map;

@Slf4j
public class FreeSqlCodeProcessor {

    public void process(JavaCodeGenContext ctx) throws Exception {
        try {
            Long projectId = ctx.getProjectId();
            File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));
            generateFreeSqlDao(ctx, dir);
        } catch (Throwable e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private void generateFreeSqlDao(JavaCodeGenContext ctx, final File mavenLikeDir) throws Exception {
        final Progress progress = ctx.getProgress();
        Map<String, JavaMethodHost> freeSqlPojoHosts = ctx.getFreeSqlPojoHosts();
        for (final JavaMethodHost host : freeSqlPojoHosts.values()) {
            if (host.isSampleType()) {
                continue;
            }
            ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getPojoClassName() + "] Pojo");
            progress.setOtherMessage(result.getTaskName());
            VelocityContext context = GenUtils.buildDefaultVelocityContext();
            context.put("host", host);
            GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
                    mavenLikeDir.getAbsolutePath(), host.getPojoClassName()),
                    "templates/java/FreeEntity.Pojo.java.tpl");
            result.setSuccessal(true);
        }
    }
}
