package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.ProjectDbsetRelation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Component
public class ProjectDbsetRelationDao extends BaseDao {

    public Long insertProjectDbsetRelation(ProjectDbsetRelation projectDbsetRelation) throws SQLException {
        if (null == projectDbsetRelation) {
            return 0L;
        }
        this.getDasClient().insert(projectDbsetRelation, Hints.hints().setIdBack());
        return projectDbsetRelation.getId();
    }

    public int[] insertRelationList(List<ProjectDbsetRelation> projectDbsetRelations) throws SQLException {
        if (CollectionUtils.isEmpty(projectDbsetRelations)) {
            return new int[0];
        }
        return this.getDasClient().batchInsert(projectDbsetRelations);
    }

    public int updateProjectDbsetRelation(ProjectDbsetRelation projectDbsetRelation) throws SQLException {
        if (null == projectDbsetRelation || projectDbsetRelation.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(projectDbsetRelation);
    }

    public int deleteProjectDbsetRelation(ProjectDbsetRelation projectDbsetRelation) throws SQLException {
        if (null == projectDbsetRelation || projectDbsetRelation.getId() == null) {
            return 0;
        }
        return this.getDasClient().deleteByPk(projectDbsetRelation);
    }

    public int deleteByProjectAndDbSetIdS(Long projectId, Set<Long> dbsetIds) throws SQLException {
        String sql = "DELETE FROM project_dbset_relation WHERE project_id =  " + projectId + " and dbset_id in (" + StringUtil.joinCollectByComma(dbsetIds) + ") ";
        return this.updataBysql(sql);
    }

    public int deleteByProjectId(Long projectId) throws SQLException {
        String sql = "DELETE FROM project_dbset_relation WHERE project_id =  " + projectId;
        return this.updataBysql(sql);
    }

    public ProjectDbsetRelation getProjectDbsetRelationById(Long id) throws SQLException {
        ProjectDbsetRelation project = new ProjectDbsetRelation();
        project.setId(id);
        return this.getDasClient().queryByPk(project);
    }

    public List<ProjectDbsetRelation> getAllProjectDbsetRelation(Long projectId) throws SQLException {
        String sql = "select id, dbset_id, project_id, update_user_no, insert_time, update_time from project_dbset_relation where project_id = " + projectId;
        return this.queryBySql(sql, ProjectDbsetRelation.class);
    }

    /**
     * 全局不存在
     */
    public Long getCountByRelation(Long dbsetId, Long projectId) throws SQLException {
        return this.getCount("SELECT count(1) FROM project_dbset_relation WHERE dbset_id=" + dbsetId + " and project_id=" + projectId);
    }
}
