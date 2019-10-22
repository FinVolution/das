package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.platform.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.UserGroup;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.MemberView;
import com.ppdai.platform.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class UserGroupDao extends BaseDao {

    public Long insertUserGroup(UserGroup userGroup) throws SQLException {
        if (null == userGroup) {
            return 0L;
        }
        this.getDasClient().insert(userGroup, Hints.hints().setIdBack());
        return userGroup.getId();
    }

    public Long insertUserGroup(Long user_id, Long group_id, Integer role, Integer opt_user) throws SQLException {
        UserGroup userGroup = new UserGroup();
        userGroup.setUser_id(user_id);
        userGroup.setGroup_id(group_id);
        userGroup.setRole(role);
        userGroup.setOpt_user(opt_user);
        this.getDasClient().insert(userGroup, Hints.hints().setIdBack());
        return userGroup.getId();
    }

    public int deleteUserFromGroup(Long user_id, Long group_id) throws SQLException {
        String sql = "DELETE FROM user_group WHERE user_id=? AND group_id=?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, user_id), Parameter.integerOf(StringUtils.EMPTY, group_id));
    }

    public int updateUserPersimion(UserGroup userGroup) throws SQLException {
        return this.updateUserPersimion(userGroup.getUser_id(), userGroup.getGroup_id(), userGroup.getRole(), userGroup.getOpt_user());
    }

    public int updateUserPersimion(Long userId, Long groupId, Integer role, Integer opt_user) throws SQLException {
        String sql = "UPDATE user_group SET role=?, opt_user=? WHERE user_id=? AND group_id=?";
        return this.updataBysql(sql, Parameter.integerOf(StringUtils.EMPTY, role),
                Parameter.integerOf(StringUtils.EMPTY, opt_user),
                Parameter.integerOf(StringUtils.EMPTY, userId),
                Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    public List<MemberView> getUserListByLikeUserName(String userName) throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT id as user_id, user_no, user_name, user_real_name, user_email FROM login_users ");
        if (StringUtils.isNotBlank(userName)) {
            sql.append("WHERE (user_name like '" + userName + "%' or user_real_name like '" + userName + "%') and id != " + PermissionService.getSUPERID() + " limit 10");
        } else {
            sql.append("WHERE id != " + PermissionService.getSUPERID() + " limit 10");
        }
        System.out.println(sql.toString());
        return this.queryBySql(sql.toString(), MemberView.class);
    }

    public List<UserGroup> getUserGroupByUserId(Long userId) throws SQLException {
        String sql = "SELECT id, user_id, group_id, role, opt_user FROM user_group WHERE user_id = ?";
        return this.queryBySql(sql, UserGroup.class, Parameter.integerOf(StringUtils.EMPTY, userId));
    }

    public List<UserGroup> getUserGroupByGroupId(Long groupId) throws SQLException {
        String sql = "SELECT id, user_id, group_id, role, opt_user FROM user_group WHERE group_id = ?";
        return this.queryBySql(sql, UserGroup.class, Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    public List<UserGroup> getUserGroupByGroupIdAndUserId(Long groupId, Long userId) throws SQLException {
        String sql = "SELECT id, user_id, group_id, role, opt_user FROM user_group WHERE group_id = ? AND user_id=?";
        return this.queryBySql(sql, UserGroup.class, Parameter.integerOf(StringUtils.EMPTY, groupId), Parameter.integerOf(StringUtils.EMPTY, userId));
    }

    public Long getMemberTotalCount(Paging<UserGroup> paging) throws SQLException {
        String sql = "SELECT count(1) FROM user_group t1 LEFT JOIN login_users t2 ON t1.user_id = t2.id " + appenMenberWhere(paging);
        return this.getCount(sql);
    }

    public List<MemberView> findMemberPageList(Paging<UserGroup> paging) throws SQLException {
        String sql = "SELECT t1.group_id,t1.id,t1.user_id, t1.update_time, t2.user_no, t2.user_name, t2.user_email, t1.role," +
                "t1.opt_user,t2.user_real_name, t2.user_real_name as update_user_name " +
                "FROM user_group t1 " +
                "inner JOIN login_users t2 ON t1.user_id = t2.id " + appenMenberCondition(paging);
        return this.queryBySql(sql, MemberView.class);
    }

    private String appenMenberWhere(Paging<UserGroup> paging) {
        UserGroup userGroup = paging.getData();
        if (null == userGroup) {
            return StringUtils.EMPTY;
        }
        return SelectCoditonBuilder.getInstance().setTab("t1.").where()
                .equal("group_id", userGroup.getId())
                .in("role", userGroup.getRoles(), Integer.class.getClass())
                .setTab("t2.")
                .likeLeft("user_no", userGroup.getUserNo())
                .likeLeft("user_name", userGroup.getUserName())
                .likeLeft("user_real_name", userGroup.getUserRealName())
                .likeLeft("user_email", userGroup.getUserEmail())
                .builer();
    }

    private String appenMenberCondition(Paging<UserGroup> paging) {
        return appenMenberWhere(paging) + SelectCoditonBuilder.getInstance()
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}

