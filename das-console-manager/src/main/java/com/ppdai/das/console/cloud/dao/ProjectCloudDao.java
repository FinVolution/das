package com.ppdai.das.console.cloud.dao;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.cloud.dto.view.ProjectView;
import com.ppdai.das.console.dao.base.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class ProjectCloudDao extends BaseDao {

    public List<ProjectView> getProjectsByWorkName(String workname) throws SQLException {
        String sql = "select t1.id, t1.name,t1.app_id,t1.dal_group_id,t1.dal_group_id, t1.dal_config_name,t1.update_user_no,t1.insert_time,t1.update_time,t1.pre_release_time,t1.app_scene,t1.comment,t1.first_release_time, t1.token from project t1\n" +
                "inner join dal_group t2 on t1.dal_group_id = t2.id\n" +
                "inner join user_group t3 on t3.group_id = t2.id\n" +
                "inner join login_users t4 on t4.id = t3.user_id\n" +
                "where t4.user_name = ? ";
        return this.queryBySql(sql, ProjectView.class, Parameter.varcharOf(StringUtils.EMPTY, workname));
    }

}
