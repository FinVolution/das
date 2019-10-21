package com.ppdai.platform.das.codegen.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.dao.base.BaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.UserProject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class UserProjectDao extends BaseDao {

    public Long insertUserProject(UserProject userProject) throws SQLException {
        if (userProject == null) {
            return 0L;
        }
        this.getDasClient().insert(userProject, Hints.hints().setIdBack());
        return userProject.getId();
    }

    public int[] insertUserProjectList(List<UserProject> userProjects) throws SQLException {
        if (CollectionUtils.isEmpty(userProjects)) {
            return new int[0];
        }

        return this.getDasClient().batchInsert(userProjects);
    }

    public int updateUserProject(UserProject userProject) throws SQLException {
        if (null == userProject || userProject.getId() == null) {
            return 0;
        }
        return this.getDasClient().update(userProject);
    }

    public int deleteUserProjectByProjectId(Long project_id) throws SQLException {
        String sql = "DELETE FROM user_project WHERE project_id = ?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, project_id));
    }

    public int deleteByProjectAndUserIdS(Long projectId, Set<Long> userIds) throws SQLException {
        String sql = "DELETE FROM user_project WHERE project_id =  " + projectId + " and user_id in (" + StringUtil.joinCollectByComma(userIds) + ") ";
        return this.updataBysql(sql);
    }

    public List<UserProject> getUsersByProjectId(Long projectId) throws SQLException {
        String sql = "SELECT id, project_id, user_id FROM user_project WHERE project_id = ?";
        return this.queryBySql(sql, UserProject.class, Parameter.integerOf(StringUtils.EMPTY, projectId));
    }

    public UserProject getMinUserProjectByProjectId(Long project_id) throws SQLException {
        String sql = "SELECT id,project_id, user_id FROM user_project WHERE id=(SELECT min(id) FROM user_project WHERE project_id = ?)";
        List<UserProject> list = this.queryBySql(sql, UserProject.class, Parameter.integerOf(StringUtils.EMPTY, project_id));
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }
}
