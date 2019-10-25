package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.TaskAuto;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DaoBySqlBuilder extends BaseDao {

    public List<TaskAuto> getTasksByProjectId(Long projectId) throws SQLException {
        String sql = "SELECT id, project_id,db_name, table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints " +
                "FROM task_auto WHERE project_id=? order by id ";
        return this.queryBySql(sql, TaskAuto.class, Parameter.integerOf(StringUtils.EMPTY, projectId));
    }

    public List<TaskAuto> updateAndGetAllTasks(Long projectId) throws SQLException {
        List<TaskAuto> result = new ArrayList<>();
        List<TaskAuto> list = getTasksByProjectId(projectId);
        if (list == null || list.isEmpty()) {
            return result;
        }

        for (TaskAuto entity : list) {
            entity.setGenerated(true);
            result.add(entity);
        }

        return result;
    }

    public List<TaskAuto> updateAndGetTasks(Long projectId) throws SQLException {
        String sql = "SELECT  id, project_id, db_name,table_name,class_name,method_name,sql_style,crud_type,fields,where_condition,sql_content,`generated`,version,update_user_no,update_time,comment,scalarType,pagination,orderby,approved,approveMsg,hints " +
                "FROM task_auto WHERE project_id=? AND `generated`=FALSE";
        List<TaskAuto> list = this.queryBySql(sql, TaskAuto.class, Parameter.integerOf(StringUtils.EMPTY, projectId));
        List<TaskAuto> result = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return result;
        }
        //processList(list);
        for (TaskAuto entity : list) {
            entity.setGenerated(true);
            result.add(entity);
        }
        return result;
    }

    public Long insertTask(TaskAuto task) throws SQLException {
        if (null == task) {
            return 0L;
        }
        this.getDasClient().insert(task, Hints.hints().setIdBack());
        return task.getId();
    }

}
