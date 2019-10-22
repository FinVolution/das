package com.ppdai.platform.das.console.common.codeGen.generator.processor.generator;

import com.ppdai.platform.das.console.common.codeGen.entity.ExecuteResult;
import com.ppdai.platform.das.console.common.codeGen.generator.java.context.JavaCodeGenContext;
import com.ppdai.platform.das.console.common.codeGen.host.java.JavaParameterHost;
import com.ppdai.platform.das.console.common.codeGen.host.java.JavaTableHost;
import com.ppdai.platform.das.console.common.codeGen.utils.GenUtils;
import com.ppdai.platform.das.console.common.codeGen.utils.StringUtil;
import com.ppdai.platform.das.console.dto.entry.codeGen.Progress;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TableCodeProcessor {

    private final int aliCodeCeckMix = 80;

    public void process(JavaCodeGenContext ctx) throws Exception {
        Long projectId = ctx.getProjectId();
        File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));
        generateTableDao(ctx, dir);
    }

    private void generateTableDao(JavaCodeGenContext ctx, final File mavenLikeDir) throws Exception {
        final Progress progress = ctx.getProgress();
        Queue<JavaTableHost> tableHosts = ctx.getTableHosts();

        for (final JavaTableHost host : tableHosts) {
            List<String> columnDefinitions = initColumnDefinitions(host);
            host.setColumnDefinitions(columnDefinitions);

            ExecuteResult result = new ExecuteResult(
                    "Generate Table[" + host.getDbSetName() + "." + host.getTableName() + "] Dao, Pojo, Test");
            progress.setOtherMessage(result.getTaskName());
            VelocityContext context = GenUtils.buildDefaultVelocityContext();
            context.put("host", transforHost(host));

            GenUtils.mergeVelocityContext(context, String.format("%s/Table/%s.java",
                    mavenLikeDir.getAbsolutePath(), host.getPojoClassName()),
                    "templates/java/Pojo.java.tpl");
            result.setSuccessal(true);
        }
    }


    private JavaTableHost transforHost(JavaTableHost host) {
        if (StringUtils.isNotBlank(host.getPojoViewName())) {
            host.setPojoClassName(upperCase(StringUtil.camelCaseName(host.getPojoViewName())));
        }
        return host;
    }

    private String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    private List<String> initColumnDefinitions(JavaTableHost javaTableHost) {
        List<String> list = new ArrayList<>();
        List<JavaParameterHost> fields = javaTableHost.getFields();
        StringBuffer columns = new StringBuffer();
        StringBuffer _columns = new StringBuffer();
        String column;
        int size = fields.size();
        for (int i = 0; i < size; i++) {
            JavaParameterHost host = fields.get(i);
            column = host.getCamelCaseUncapitalizedName();
            String comma = ", ";
            if (i == size - 1) {
                comma = StringUtils.EMPTY;
            }
            column = column + comma;
            _columns.append(column);
            boolean isMax = _columns.toString().length() > aliCodeCeckMix;
            if (isMax) {
                list.add(columns.toString());
                columns = new StringBuffer();
                _columns = new StringBuffer();
                columns.append(column);
                _columns.append(column);
            } else {
                columns.append(column);
            }
            if (i == size - 1 && !list.contains(columns.toString())) {
                list.add(columns.toString());
            }
        }
        return list;
    }
}
