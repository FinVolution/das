package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.TaskTable;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.TaskTableView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class TableEntityDao extends BaseDao {

    public Long insertTask(TaskTable task) throws SQLException {
        if (null == task) {
            return 0L;
        }
        this.getDasClient().insert(task, Hints.hints().setIdBack());
        return task.getId();
    }

    public int[] insertTaskTablelist(List<TaskTable> list) throws SQLException {
        if (CollectionUtils.isEmpty(list)) {
            return new int[0];
        }
        return this.getDasClient().batchInsert(list);
    }

    public int updateTask(TaskTable taskTable) throws SQLException {
        if (null == taskTable || taskTable.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(taskTable);
    }

    public int deleteTask(TaskTable taskTable) throws SQLException {
        if (null == taskTable || taskTable.getId() == null) {
            return 0;
        }
        return this.getDasClient().deleteByPk(taskTable);
    }

    public int deleteByProjectId(int id) throws SQLException {
        return this.updataBysql("DELETE FROM task_table WHERE project_id=?", Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public TaskTable getTasksByTaskId(Long id) throws SQLException {
        TaskTable taskTable = new TaskTable();
        taskTable.setId(id);
        return this.getDasClient().queryByPk(taskTable);
    }

    public List<TaskTable> getTaskTableByDbNames(Long project_id, List<String> names) throws SQLException {
        TaskTable.TaskTableDefinition t = TaskTable.TASKTABLE;
        SqlBuilder builder = SqlBuilder.selectAllFrom(t).where().allOf(t.customTableName.in(names), t.projectId.eq(project_id)).into(TaskTable.class);
        return this.getDasClient().query(builder);
    }

    /**
     * 根据项目主键查询所有任务
     *
     * @param projectId
     * @return
     */
    public List<TaskTableView> getTasksByProjectId(Long projectId) throws SQLException {
        String sql = "select t1.id, t1.project_id,t1.dbset_id, t1.table_names,t1.custom_table_name,t1.view_names,t1.prefix,suffix, t1.cud_by_sp,t1.pagination,t1.generated,t1.version,t1.update_user_no,t1.update_time,t1.comment,t1.sql_style,t1.api_list,t1.approved,t1.approveMsg,t1.field_type " +
                ",t2.name as dbset_name,t3.db_name, t3.id as alldbs_id " +
                "from (select t1.id task_id, t1.project_id,t1.dbset_id, min(t2.id) as dbsetentry_id " +
                "from task_table t1 " +
                "inner join  databasesetentry t2 on t1.dbset_id = t2.dbset_id " +
                "where t1.project_id = " + projectId + " and t2.database_type = 1 " +
                "group by t1.id ) t0 " +
                "inner join task_table t1 on t0.task_id = t1.id " +
                "inner join  databasesetentry t2 on t0.dbsetentry_id = t2.id " +
                "inner join alldbs t3 on t2.db_Id = t3.id ";
        log.info("getTasksByProjectId --->" + sql);
        return this.queryBySql(sql, TaskTableView.class);
    }

    public Long getTableEntityTotalCount(Paging<TaskTable> paging) throws SQLException {
        String sql = " select count(1) from task_table t1 " +
                " left join databaseset t2 on t1.dbset_id = t2.id " +
                " left join login_users t3 on t1.update_user_no = t3.user_no " + appenCondition(paging);
        return this.getCount(sql);
    }

    public List<TaskTableView> findTableEntityPageList(Paging<TaskTable> paging) throws SQLException {
        String sql = " select t1.id, t1.project_id, t1.table_names,t1.view_names,t1.custom_table_name,t1.update_user_no,t1.dbset_id, t1.update_user_no, t1.comment, t1.update_time, t1.field_type, t2.name as dbset_name, t3.user_real_name from task_table t1 " +
                " left join databaseset t2 on t1.dbset_id = t2.id " +
                " left join login_users t3 on t1.update_user_no = t3.user_no " + appenCondition(paging);
        return this.queryBySql(sql, TaskTableView.class);
    }

    private String appenWhere(Paging<TaskTable> paging) {
        TaskTable taskTable = paging.getData();
        if (null == taskTable) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("project_id", taskTable.getProject_id())
                .likeLeft("view_names", taskTable.getView_names())
                .likeLeft("table_names", taskTable.getTable_names())
                .likeLeft("comment", taskTable.getComment())
                .setTab("t2.")
                .likeLeft("name", taskTable.getDbsetName())
                .builer();
    }

    private String appenCondition(Paging<TaskTable> paging) {
        TaskTable taskTable = paging.getData();
        if (null == taskTable) {
            return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                    .orderBy(paging)
                    .limit(paging)
                    .builer();
        }
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }

}
