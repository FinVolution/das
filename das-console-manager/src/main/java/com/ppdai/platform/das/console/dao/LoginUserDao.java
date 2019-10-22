package com.ppdai.platform.das.console.dao;

import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.platform.das.console.common.utils.SelectCoditonBuilder;
import com.ppdai.platform.das.console.dao.base.BaseDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.view.LoginUserView;
import com.ppdai.platform.das.console.dto.view.LoginUsersView;
import com.ppdai.platform.das.console.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class LoginUserDao extends BaseDao {

    public Long insertUser(LoginUser user) throws SQLException {
        if (null == user) {
            return 0L;
        }
        this.getDasClient().insert(user, Hints.hints().setIdBack());
        return user.getId();
    }

    public int initAdmin(LoginUser user) throws SQLException {
        if (null == user) {
            return 0;
        }
        String sql = "INSERT INTO `login_users` (`id`, `user_no`, `user_name`, `user_real_name`, `user_email`, `password`,`update_user_no`) VALUES (?, ?, ?, ?, ?, ?, ?);";
        return this.updataBysql(sql,
                Parameter.integerOf(StringUtils.EMPTY, user.getId()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getUserNo()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getUserName()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getUserRealName()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getUserEmail()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getPassword()),
                Parameter.varcharOf(StringUtils.EMPTY, user.getUpdate_user_no()));
    }

    public int updateUserPassword(LoginUser user) throws SQLException {
        if (user == null) {
            return 0;
        }
        String sql = "UPDATE login_users SET password = ? WHERE id = ?";
        return this.updataBysql(sql,
                Parameter.varcharOf(StringUtils.EMPTY, user.getPassword()),
                Parameter.integerOf(StringUtils.EMPTY, user.getId()));
    }

    public List<LoginUser> getAllUsers() throws SQLException {
        LoginUser.LoginUsersDefinition p = LoginUser.LOGINUSER;
        SqlBuilder builder = SqlBuilder.selectAllFrom(p).into(LoginUser.class);
        return this.getDasClient().query(builder);
    }

    public LoginUser getUserById(Long userId) throws SQLException {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        return this.getDasClient().queryByPk(loginUser);
    }

    public LoginUser getUserByNo(String userNo) throws SQLException {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserNo(userNo);
        List<LoginUser> list = this.getDasClient().queryBySample(loginUser);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public LoginUser getUserByUserName(String userName) throws SQLException {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserName(userName);
        List<LoginUser> list = this.getDasClient().queryBySample(loginUser);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<LoginUsersView> getUserByGroupId(Long groupId) throws SQLException {
        String sql = "SELECT t2.id, t2.user_no, t2.user_name, t2.user_real_name, t2.user_email, t2.password, t1.role, t1.opt_user FROM user_group t1 inner JOIN login_users t2 ON t1.user_id = t2.id WHERE t1.group_id = ?";
        return this.queryBySql(sql, LoginUsersView.class, Parameter.integerOf(StringUtils.EMPTY, groupId));
    }

    public int updateUser(LoginUser user) throws SQLException {
        if (user == null) {
            return 0;
        }
        String sql = "UPDATE login_users SET user_email = ? WHERE id = ?";
        return this.updataBysql(sql,
                Parameter.varcharOf(StringUtils.EMPTY, user.getUserEmail()),
                Parameter.integerOf(StringUtils.EMPTY, user.getId()));
    }

    public int deleteUser(Long userId) throws SQLException {
        LoginUser user = new LoginUser();
        user.setId(userId);
        return this.getDasClient().deleteByPk(user);
    }

    public Long getTotalCount(Paging<LoginUser> paging) throws SQLException {
        String sql = "select count(1) from login_users t1 " + appenWhere(paging);
        return this.getCount(sql);
    }

    public List<LoginUserView> findUserPageList(Paging<LoginUser> paging) throws SQLException {
        String sql = "SELECT t1.id, t1.user_no, t1.user_name, t1.user_email, t1.user_real_name , t2.user_real_name as update_user_name, t4.group_name FROM login_users t1 " +
                "left join login_users t2 on t1.update_user_no = t2.user_no " +
                "left join user_group t3 on t3.user_id = t1.id " +
                "left join dal_group t4 on t3.group_id= t4.id " + appenCondition(paging);
        return this.queryBySql(sql, LoginUserView.class);
    }

    private String appenWhere(Paging<LoginUser> paging) {
        LoginUser loginUser = paging.getData();
        if (null == loginUser) {
            return SelectCoditonBuilder.getInstance().where().setTab("t1.").unEqual("id", PermissionService.getSUPERID()).builer();
        }
        return SelectCoditonBuilder.getInstance().where().setTab("t1.")
                .unEqual("id", PermissionService.getSUPERID())
                .likeLeft("user_no", loginUser.getUserNo())
                .likeLeft("user_name", loginUser.getUserName())
                .likeLeft("user_real_name", loginUser.getUserRealName())
                .likeLeft("user_email", loginUser.getUserEmail())
                .builer();
    }

    private String appenCondition(Paging<LoginUser> paging) {
        LoginUser loginUser = paging.getData();
        if (null == loginUser) {
            return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                    .orderBy(paging)
                    .limit(paging)
                    .builer();
        }
        return appenWhere(paging) + SelectCoditonBuilder.getInstance().setTab("t1.")
                .like("user_no", loginUser.getUserNo())
                .like("user_name", loginUser.getUserName())
                .orderBy(paging)
                .limit(paging)
                .builer();
    }
}
