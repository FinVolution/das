package com.ppdai.platform.das.console.common.codeGen.generator.processor.prepareData;

import com.ppdai.platform.das.console.common.codeGen.CodeGenConsts;
import com.ppdai.platform.das.console.common.codeGen.entity.ExecuteResult;
import com.ppdai.platform.das.console.common.codeGen.generator.java.context.JavaCodeGenContext;
import com.ppdai.platform.das.console.common.codeGen.generator.processor.AbstractDataPreparer;
import com.ppdai.platform.das.console.common.codeGen.host.AbstractParameterHost;
import com.ppdai.platform.das.console.common.codeGen.host.java.FreeSqlHost;
import com.ppdai.platform.das.console.common.codeGen.host.java.JavaGivenSqlResultSetExtractor;
import com.ppdai.platform.das.console.common.codeGen.host.java.JavaMethodHost;
import com.ppdai.platform.das.console.common.codeGen.host.java.JavaParameterHost;
import com.ppdai.platform.das.console.common.codeGen.utils.DbUtils;
import com.ppdai.platform.das.console.common.codeGen.utils.SqlBuilder;
import com.ppdai.platform.das.console.dao.TaskSqlDao;
import com.ppdai.platform.das.console.dto.entry.codeGen.Progress;
import com.ppdai.platform.das.console.dto.view.TaskSqlView;
import com.ppdai.platform.das.console.enums.DataFieldTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class FreeSqlDataProcessor extends AbstractDataPreparer {

    public void process(JavaCodeGenContext ctx) throws Exception {
        Long projectId = ctx.getProjectId();
        final Progress progress = ctx.getProgress();
        final String namespace = ctx.getNamespace();
        final Map<String, JavaMethodHost> freeSqlPojoHosts = ctx.getFreeSqlPojoHosts();
        final Queue<FreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
        TaskSqlDao taskSqlDao = new TaskSqlDao();
        List<TaskSqlView> freeSqlTasks;
        if (ctx.isRegenerate()) {
            freeSqlTasks = taskSqlDao.updateAndGetAllTasks(projectId);
            prepareDbFromFreeSql(ctx, freeSqlTasks);
        } else {
            freeSqlTasks = taskSqlDao.updateAndGetTasks(projectId);
            prepareDbFromFreeSql(ctx, taskSqlDao.getTasksByProjectId(projectId));
        }

        if (!ctx.isIgnoreApproveStatus() && freeSqlTasks != null && freeSqlTasks.size() > 0) {
            Iterator<TaskSqlView> ite = freeSqlTasks.iterator();
            while (ite.hasNext()) {
                int approved = ite.next().getApproved();
                if (approved != 2 && approved != 0) {
                    ite.remove();
                }
            }
        }

        final Map<String, List<TaskSqlView>> groupBy = freeSqlGroupBy(freeSqlTasks);
        for (final Map.Entry<String, List<TaskSqlView>> entry : groupBy.entrySet()) {
            ExecuteResult result = new ExecuteResult("Build  Free SQL[" + entry.getKey() + "] Host");
            progress.setOtherMessage(result.getTaskName());
            List<TaskSqlView> currentTasks = entry.getValue();
            if (currentTasks.size() < 1) {
                continue;
            }
            FreeSqlHost host = new FreeSqlHost();
            host.setDbSetName(currentTasks.get(0).getDbsetName());
            host.setClassName(currentTasks.get(0).getClass_name());
            host.setPackageName(namespace);
            host.setDatabaseCategory(getDatabaseCategory(currentTasks.get(0).getAlldbs_id()));
            //host.setLength(currentTasks.get(0).getLength());

            List<JavaMethodHost> methods = new ArrayList<>();
            for (TaskSqlView task : currentTasks) {
                try {
                    processMethodHost(task, namespace, methods, freeSqlPojoHosts);
                } catch (Throwable e) {
                    progress.setOtherMessage(e.getMessage());
                    throw new Exception(String.format("Task Id[%s]:%s\r\n", task.getId(), e.getMessage()), e);
                }
            }
            host.setMethods(methods);
            freeSqlHosts.add(host);
            result.setSuccessal(true);
        }
    }

    private void prepareDbFromFreeSql(JavaCodeGenContext codeGenCtx, List<TaskSqlView> freeSqls) throws SQLException {
        for (TaskSqlView task : freeSqls) {
            addDatabaseSet(codeGenCtx, task.getDbset_id());
        }
    }

    private Map<String, List<TaskSqlView>> freeSqlGroupBy(List<TaskSqlView> tasks) {
        Map<String, List<TaskSqlView>> groupBy = new HashMap<>();
        for (TaskSqlView task : tasks) {
            String key = String.format("%s_%s", task.getAlldbs_id(), task.getClass_name().toLowerCase());
            if (groupBy.containsKey(key)) {
                groupBy.get(key).add(task);
            } else {
                groupBy.put(key, new ArrayList<TaskSqlView>());
                groupBy.get(key).add(task);
            }
        }
        return groupBy;
    }

    private void processMethodHost(TaskSqlView task, String namespace, List<JavaMethodHost> methods,
                                   Map<String, JavaMethodHost> freeSqlPojoHosts) throws Exception {
        JavaMethodHost method = new JavaMethodHost();
        method.setSql(task.getSql_content());
        method.setName(task.getMethod_name());
        method.setPackageName(namespace);
        method.setScalarType(task.getScalarType());
        method.setPojoType(task.getPojoType());
        method.setPaging(task.getPagination());
        method.setCrud_type(task.getCrud_type());
        method.setComments(task.getComment());
        method.setField_type(task.getField_type());
        // method.setLength(task.getLength());

        if (task.getPojo_name() != null && !task.getPojo_name().isEmpty()) {
            method.setPojoClassName(WordUtils.capitalize(task.getPojo_name() /*+ "Pojo"*/));
        }
        List<JavaParameterHost> params = new ArrayList<>();
        for (String param : StringUtils.split(task.getParameters(), ";")) {
            String[] splitedParam = StringUtils.split(param, ",");
            JavaParameterHost p = new JavaParameterHost();
            p.setName(splitedParam[0]);
            p.setSqlType(Integer.valueOf(splitedParam[1]));
            p.setJavaClass(CodeGenConsts.jdbcSqlTypeToJavaClass.get(p.getSqlType()));
            p.setValidationValue(DbUtils.mockATest(p.getSqlType()));
            boolean sensitive = splitedParam.length >= 3 ? Boolean.parseBoolean(splitedParam[2]) : false;
            p.setSensitive(sensitive);
            params.add(p);
        }

        SqlBuilder.rebuildJavaInClauseSQL(task.getSql_content(), params);
        method.setParameters(params);
        method.setHints(task.getHints());
        methods.add(method);

        if (method.getPojoClassName() != null && !method.getPojoClassName().isEmpty()
                && !freeSqlPojoHosts.containsKey(method.getPojoClassName())
                && !"update".equalsIgnoreCase(method.getCrud_type())) {
            List<JavaParameterHost> paramHosts = new ArrayList<>();
            List<AbstractParameterHost> hosts = DbUtils.testAQuerySql(task.getAlldbs_id(), task.getSql_content(), task.getParameters(), new JavaGivenSqlResultSetExtractor());
            for (AbstractParameterHost _ahost : hosts) {
                JavaParameterHost host = (JavaParameterHost) _ahost;
                host.setField_type(task.getField_type());
                paramHosts.add(host);
            }
            List<JavaParameterHost> list = paramHosts.stream().filter(field -> DataFieldTypeEnum.SQL_DATE.getDetail().equals(field.getJavaClass().getName())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                paramHosts.stream().forEach(p -> p.setSqlDateExist(true));
            }
            method.setFields(paramHosts);
            freeSqlPojoHosts.put(method.getPojoClassName(), method);
        } else if ("update".equalsIgnoreCase(method.getCrud_type())) {
            DbUtils.testUpdateSql(task.getAlldbs_id(), task.getSql_content(), task.getParameters());
        }
    }

}
