package com.ppdai.das.console.dao;

import com.ppdai.das.console.dao.base.BaseDao;
import com.ppdai.das.console.enums.RoleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
public class PermissionDao extends BaseDao {

    private final Long SUPER_GROUP_ID = 1L;

    public boolean isNormalSuperManagerById(Long userId) throws SQLException {
        return isExistByUserId(RoleTypeEnum.Admin.getType(), SUPER_GROUP_ID, userId);
    }

    public boolean isGroupManagerById(Long groupId, Long userId) throws SQLException {
        return isExistByUserId(RoleTypeEnum.Admin.getType(), groupId, userId);
    }

    public boolean isNormalManagerById(Long groupId, Long userId) throws SQLException {
        return isExistByUserId(RoleTypeEnum.Limited.getType(), groupId, userId);
    }

    public boolean isExistByUserId(Integer roleType, Long groupId, Long userId) throws SQLException {
        String sql = "select count(1) from login_users t1 " +
                "inner join user_group t2 on t1.id = t2.user_id " +
                "where t2.role = " + roleType + " and t2.group_id = " + groupId + " and t1.id=" + userId;
        return this.getCount(sql) > 0;
    }

    public boolean isProjectPermission(Long projectId, Long userId) throws SQLException {
        String sql = "select count(1) from dal_group t1 " +
                "inner join project t2 on t1.id = t2.dal_group_id " +
                "inner join user_group t4 on t4.group_id = t1.id " +
                "where t2.id = " + projectId + " and t4.user_id = " + userId;
        return this.getCount(sql) > 0;
    }
}
