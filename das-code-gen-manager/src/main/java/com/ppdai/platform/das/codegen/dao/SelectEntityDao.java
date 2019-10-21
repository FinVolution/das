package com.ppdai.platform.das.codegen.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.platform.das.codegen.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.codegen.dao.base.BaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.TaskSql;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.TaskSqlView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class SelectEntityDao extends BaseDao {

    public Long insertTask(TaskSql task) throws SQLException {
        if (null == task) {
            return 0L;
        }
        this.getDasClient().insert(task, Hints.hints().setIdBack());
        return task.getId();
    }

    public int updateTask(TaskSql task) throws SQLException {
        if (null == task || task.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(task);
    }

    public int deleteTask(TaskSql task) throws SQLException {
        if (null == task || task.getId() == null) {
            return 0;
        }
        return this.getDasClient().deleteByPk(task);
    }

    public int deleteByProjectId(Long projectId) throws SQLException {
        String sql = "DELETE FROM task_sql WHERE project_id=?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, projectId));
    }


    public TaskSql getTasksByTaskId(Long id) throws SQLException {
        TaskSql task = new TaskSql();
        task.setId(id);
        return this.getDasClient().queryByPk(task);
    }

    public Long getSelectEntityTotalCount(Paging<TaskSql> paging) throws SQLException {
        return this.getCount("SELECT count(1) FROM task_sql");
    }

    public List<TaskSqlView> findSelectEntityPageList(Paging<TaskSql> paging) throws SQLException {
        String sql = "select t1.id, t1.project_id, t1.class_name, t1.pojo_name, t1.method_name,t1.sql_content,t1.update_user_no,t1.update_time,t1.dbset_id,t2.user_real_name, t1.comment, t1.field_type, t3.name as dbset_name from task_sql t1 " +
                " join login_users t2 on t1.update_user_no = t2.user_no " +
                " join databaseset t3 on t1.dbset_id = t3.id" + appenCondition(paging);
        return this.queryBySql(sql, TaskSqlView.class);
    }

    private String appenWhere(Paging<TaskSql> paging) {
        TaskSql taskSql = paging.getData();
        if (null == taskSql) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("project_id", taskSql.getProject_id())
                .likeLeft("class_name", taskSql.getClass_name())
                .likeLeft("comment", taskSql.getComment())
                .setTab("t3.")
                .likeLeft("name", taskSql.getDbsetName())
                .builer();
    }

    private String appenCondition(Paging<TaskSql> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
