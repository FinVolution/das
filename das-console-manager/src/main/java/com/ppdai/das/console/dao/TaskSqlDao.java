package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.common.codeGen.utils.DatabaseSetUtils;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.TaskSql;
import com.ppdai.das.console.dto.view.TaskSqlView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TaskSqlDao extends BaseDao {

    private void processList(List<TaskSqlView> list) throws SQLException {
        if (list == null || list.size() == 0) {
            return;
        }

        for (TaskSqlView entity : list) {
            processGenTaskByFreeSql(entity);
        }
    }

    private void processGenTaskByFreeSql(TaskSqlView entity) throws SQLException {
        if (entity.getApproved() != null) {
            if (entity.getApproved() == 1) {
                entity.setStr_approved("未审批");
            } else if (entity.getApproved() == 2) {
                entity.setStr_approved("通过");
            } else if (entity.getApproved() == 3) {
                entity.setStr_approved("未通过");
            } else {
                entity.setStr_approved("通过");
            }
        }

        if (entity.getUpdate_time() != null) {
            Date date = new Date(entity.getUpdate_time().getTime());
            entity.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        }

        entity.setAlldbs_id(DatabaseSetUtils.getAlldbsIdByDbSetId(entity.getDbset_id()));
    }

    public List<TaskSqlView> getTasksByProjectId(Long projectId) throws SQLException {
        String sql = "SELECT t1.id,t1.project_id, t1.dbset_id, t1.class_name, t1.pojo_name, t1.method_name, t1.crud_type, t1.sql_content, t1.parameters, t1.generated, t1.version, t1.update_user_no, t1.update_time, t1.comment, t1.scalarType, t1.pojoType, t1.pagination, t1.sql_style, t1.approved, t1.approveMsg, t1.hints,t1.field_type,t2.name as dbset_name " +
                "FROM task_sql t1 " +
                "left join databaseset t2 on t1.dbset_id = t2.id WHERE t1.project_id=" + projectId + " order by t1.id";
        List<TaskSqlView> list = this.queryBySql(sql, TaskSqlView.class);
        processList(list);
        return list;
    }

    public List<TaskSqlView> updateAndGetAllTasks(Long projectId) throws SQLException {
        List<TaskSqlView> result = new ArrayList<>();
        List<TaskSqlView> list = getTasksByProjectId(projectId);
        if (list == null || list.size() == 0) {
            return result;
        }

        for (TaskSqlView entity : list) {
            entity.setGenerated(true);
            if (updateTask(entity) > 0) {
                result.add(entity);
            }
        }

        return result;
    }

    public List<TaskSqlView> updateAndGetTasks(Long projectId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT id, project_id,dbset_id, db_name, class_name,pojo_name,method_name,crud_type,sql_content,parameters,`generated`,version,update_user_no,update_time,comment,scalarType,pojoType,pagination,sql_style,approved,approveMsg,hints ");
        sb.append("FROM task_sql WHERE project_id=? AND `generated`=FALSE");
        List<TaskSqlView> list = this.queryBySql(sb.toString(), TaskSqlView.class, Parameter.integerOf(StringUtils.EMPTY, projectId));

        List<TaskSqlView> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }
        processList(list);
        for (TaskSqlView entity : list) {
            entity.setGenerated(true);
            if (updateTask(entity) > 0) {
                result.add(entity);
            }
        }
        return result;
    }

    public Long insertTask(TaskSql task) throws SQLException {
        if (null == task) {
            return 0L;
        }
        this.getDasClient().insert(task, Hints.hints().setIdBack());
        return task.getId();
    }

    public int updateTask(TaskSqlView task) throws SQLException {
        {
            String sql = "SELECT * FROM task_sql WHERE id != ? AND project_id=? AND class_name=? AND method_name=? LIMIT 1";
            List<TaskSqlView> list = this.queryBySql(sql, TaskSqlView.class,
                    Parameter.integerOf(StringUtils.EMPTY, task.getId()),
                    Parameter.integerOf(StringUtils.EMPTY, task.getProject_id()),
                    Parameter.varcharOf(StringUtils.EMPTY, task.getClass_name()),
                    Parameter.varcharOf(StringUtils.EMPTY, task.getMethod_name())
            );
            if (CollectionUtils.isNotEmpty(list)) {
                return 0;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE task_sql SET project_id=?, class_name=?,pojo_name=?,");
        sb.append("method_name=?,crud_type=?,sql_content=?,parameters=?,`generated`=?,");
        sb.append("version=version+1,update_user_no=?,comment=?,");
        sb.append("scalarType=?,pojoType=?,pagination=?,sql_style=?,approved=?,approveMsg=?,hints=? ");
        sb.append(" WHERE id=? AND version=?");

        return this.updataBysql(sb.toString(),
                Parameter.integerOf(StringUtils.EMPTY, task.getProject_id()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getClass_name()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getPojo_name()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getMethod_name()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getCrud_type()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getSql_content()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getParameters()),
                Parameter.booleanOf(StringUtils.EMPTY, task.getGenerated()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getUpdate_user_no()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getComment()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getScalarType()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getPojoType()),
                Parameter.booleanOf(StringUtils.EMPTY, task.getPagination()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getSql_style()),
                Parameter.integerOf(StringUtils.EMPTY, task.getApproved()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getApproveMsg()),
                Parameter.varcharOf(StringUtils.EMPTY, task.getHints()),
                Parameter.integerOf(StringUtils.EMPTY, task.getId()),
                Parameter.integerOf(StringUtils.EMPTY, task.getVersion())
        );
    }

}
