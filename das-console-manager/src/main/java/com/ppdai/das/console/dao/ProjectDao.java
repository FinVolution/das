package com.ppdai.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.das.console.common.utils.StringUtil;
import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.dto.entry.das.Project;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.ProjectModel;
import com.ppdai.das.console.dto.view.ProjectView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ProjectDao extends BaseDao {

    public Long insertProject(Project project) throws SQLException {
        if (null == project) {
            return 0L;
        }
        this.getDasClient().insert(project, Hints.hints().setIdBack());
        return project.getId();
    }

    public int deleteProject(Project project) throws SQLException {
        if (null == project || project.getId() == null) {
            return 0;
        }
        return getDasClient().deleteByPk(project);
    }

    public int updateProject(Project project) throws SQLException {
        if (null == project || project.getId() == null) {
            return 0;
        }
        return getDasClient().update(project);
    }

    public int updateTokenByAppId(String app_id, String token) throws SQLException {
        Project project = getProjectByAppId(app_id);
        project.setToken(token);
        return updateProject(project);
    }

    public int updateProjectGroupById(int groupId, int id) throws SQLException {
        String sql = "UPDATE project SET dal_group_id = ? WHERE id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, groupId), Parameter.integerOf(StringUtils.EMPTY, id));
    }

    public int updateProjectAppGroupIdById(Long appGroupId, Set<Long> projectIds) throws SQLException {
        String sql = "update project set app_group_id = ? where id in (" + StringUtil.joinCollectByComma(projectIds) + ") ";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, appGroupId));
    }

    public int deleteProjectAppGroupIdById(Long appGroupId) throws SQLException {
        String sql = "update project set app_group_id = 0 where app_group_id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, appGroupId));
    }

    public int deleteProjectAppGroupIdByIdS(Set<Long> projectIds) throws SQLException {
        String sql = "update project set app_group_id = 0 where id in (" + StringUtil.joinCollectByComma(projectIds) + ") ";
        return this.updataBysql(sql);
    }

    public Project getProjectByID(Long id) throws SQLException {
        Project project = new Project();
        project.setId(id);
        return getDasClient().queryByPk(project);
    }

    public Project getProjectByAppId(String appId) throws SQLException {
        String sql = "SELECT id, name, namespace,dal_group_id,dal_config_name,update_user_no,app_id,token,update_time FROM project WHERE app_id = '" + appId + "' limit 1";
        List<Project> list = this.queryBySql(sql, Project.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public ProjectView getProjectViewById(Long id) throws SQLException {
        String sql = "select t1.app_id,t1.name,t2.group_name, group_concat(distinct t4.name) dbset_namees from project t1\n" +
                "inner join dal_group t2 on t1.dal_group_id=t2.id\n" +
                "left join project_dbset_relation t3 on t3.project_id = t1.id\n" +
                "left join databaseset t4 on t4.id = t3.dbset_id\n" +
                "where t1.id = " + id + " group by t1.id";
        List<ProjectView> list = this.queryBySql(sql, ProjectView.class);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<Project> getProjectByDbId(Integer dbId) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.namespace, t1.dal_group_id, t1.dal_config_name, t1.update_user_no, t1.app_id, t1.update_time FROM project t1\n" +
                "inner join project_dbset_relation t2 on t2.project_id = t1.id\n" +
                "inner join databasesetentry t3 on t3.dbset_id = t2.dbset_id\n" +
                "where t3.db_Id  = ? ";
        return this.queryBySql(sql, Project.class, Parameter.integerOf(StringUtils.EMPTY, dbId));
    }

    public List<Project> getProjectBydbsetId(Long dbsetId) throws SQLException {
        String sql = "SELECT t1.id, t1.name, t1.namespace, t1.dal_group_id, t1.dal_config_name, t1.update_user_no, t1.app_id, t1.update_time FROM project t1\n" +
                "inner join project_dbset_relation t2 on t2.project_id = t1.id " +
                "where t2.dbset_id  = ? ";
        return this.queryBySql(sql, Project.class, Parameter.integerOf(StringUtils.EMPTY, dbsetId));
    }

    public List<Project> getProjectByGroupId(Long groupId) throws SQLException {
        String sql = "SELECT id, name, namespace, dal_group_id, dal_config_name, app_scene, update_user_no, update_time FROM project WHERE dal_group_id = ? order by name";
        return this.queryBySql(sql, Project.class, Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    public List<Project> getProjectByAppGroupId(Long appGroupId) throws SQLException {
        String sql = "SELECT id, name, namespace,dal_group_id,dal_config_name,app_id,update_user_no,update_time FROM project WHERE app_group_id = ?";
        return this.queryBySql(sql, Project.class, Parameter.integerOf(StringUtils.EMPTY, appGroupId));
    }

    /**
     * 全局不存在
     */
    public Long getCountByName(String name) throws SQLException {
        return this.getCount("SELECT count(1) FROM project WHERE name='" + name + "'");
    }

    public Long getCountByAppId(String app_id) throws SQLException {
        String sql = "SELECT count(1) FROM project WHERE app_id='" + app_id + "'";
        return this.getCount(sql);
    }

    /**
     * 其他行不存在此dbname
     */
    public Long getCountByIdAndName(Long id, String name) throws SQLException {
        return getCount("SELECT count(1) FROM project WHERE name='" + name + "' and id = " + id);
    }

    public Long getCountByIdAndAppId(Long id, String app_id) throws SQLException {
        return getCount("SELECT count(1) FROM project WHERE app_id='" + app_id + "' and id = " + id);
    }

    public List<Project> getProjectsListByLikeName(String name) throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT id, app_id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project");
        if (StringUtils.isNotBlank(name)) {
            sql.append(" WHERE name like '" + name + "%' limit 10");
        } else {
            sql.append(" limit 10");
        }
        return this.queryBySql(sql.toString(), Project.class);
    }

    public List<Project> getProjectsNoGroup(Long appGroupId) throws SQLException {
        String cond = StringUtils.EMPTY;
        if (null != appGroupId && appGroupId > 0) {
            cond = " or app_group_id = " + appGroupId;
        }
        String sql = "SELECT id, app_id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project where app_group_id = 0" + cond;
        return this.queryBySql(sql, Project.class);
    }

    public List<Project> getProjectsByAppGroupId(Long appGroupId) throws SQLException {
        String sql = "SELECT id, app_id, name, namespace,dal_group_id,dal_config_name,update_user_no,update_time FROM project where app_group_id = ? ";
        return this.queryBySql(sql, Project.class, Parameter.integerOf(StringUtils.EMPTY, appGroupId));
    }

    private String getfindProjectSql() {
        String selected = "SELECT t1.id, t1.name, t1.namespace, t1.app_id, t1.dal_group_id, t1.dal_config_name, t1.update_user_no, t1.insert_time, t1.update_time, t1.comment, " +
                "t1.pre_release_time,t1.first_release_time, t1.app_scene,t1.token,group_concat(distinct t3.name) as dbset_namees, group_concat(distinct t3.id) as dbset_ids, " +
                "group_concat(distinct t4.user_real_name) user_real_name, group_concat(distinct t5.user_id) user_ids, group_concat(distinct t6.user_real_name) project_users, " +
                "group_concat(distinct t7.group_name) group_name";
        String sql = " FROM project t1 " +
                "left join project_dbset_relation t2 on t2.project_id = t1.id " +
                "left join databaseset t3 on t2.dbset_id = t3.id " +
                "inner join login_users t4 on t1.update_user_no = t4.user_no " +
                "left join user_project t5 on t5.project_id = t1.id " +
                "left join login_users t6 on t6.id = t5.user_id " +
                "inner join dal_group t7 on t7.id = t1.dal_group_id ";
        return selected + sql;
    }

    public Long getProjectTotalCount(Paging<ProjectModel> paging) throws SQLException {
        String sql = "SELECT count(1) from (" + getfindProjectSql() + appenWhere(paging) + " group by  t1.id) t";
        //log.info("getProjectTotalCount----->" + sql);
        return this.getCount(sql);
    }

    public List<ProjectView> findProjectPageList(Paging<ProjectModel> paging) throws SQLException {
        String sql = getfindProjectSql() + appenCondition(paging);
        //log.info("findProjectPageList---> " + sql);
        return this.queryBySql(sql, ProjectView.class);
    }

    private String appenWhere(Paging<ProjectModel> paging) {
        ProjectModel projectModel = paging.getData();
        if (null == projectModel) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal(projectModel.getDal_group_id() != null && projectModel.getDal_group_id() != 0, "dal_group_id", projectModel.getDal_group_id())
                .likeLeft("name", projectModel.getName())
                .rangeData("first_release_time", projectModel.getFirst_release_times())
                .rangeData("pre_release_time", projectModel.getPre_release_times())
                .rangeData("insert_time", projectModel.getInsert_times())
                .likeLeft("app_id", projectModel.getApp_id())
                .likeLeft("app_scene", projectModel.getApp_scene())
                .likeLeft("comment", projectModel.getComment())
                .setTab("t3.")
                .likeLeft("name", projectModel.getDbsetNamees())
                .setTab("t7.")
                .likeLeft("group_name", projectModel.getGroupName())
                .builer();
    }

    private String appenCondition(Paging<ProjectModel> paging) {
        return appenWhere(paging) + SelectCoditonBuilder.getInstance()
                .groupBy("t1.id")
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
