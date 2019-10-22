package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.PermissionDao;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class PermissionService {

    @Getter
    private static final String ADMIN_NAME = "admin";

    @Getter
    private static final Long SUPERID = 1L;

    @Getter
    private static final Long ADMINGROUPID = 1L;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 是否S级别超级管理员
     */
    public boolean isSuperManagerById(Long id) {
        if (null == id) {
            return false;
        }
        return id.equals(SUPERID);
    }

    public boolean isSuperManagerByUserNo(String userNo) throws SQLException {
        if (null == userNo) {
            return false;
        }
        LoginUser user = loginUserDao.getUserByNo(userNo);
        return isSuperManagerById(user.getId());
    }

    /**
     * 是否普通超级管理员
     */
    public boolean isNormalSuperManagerById(Long userId) throws SQLException {
        if (null == userId || isSuperManagerById(userId)) {
            return false;
        }
        return permissionDao.isNormalSuperManagerById(userId);
    }

    /**
     * 是否超级管理员
     */
    public boolean isManagerById(Long id) throws SQLException {
        return isSuperManagerById(id) || isNormalSuperManagerById(id);
    }

    /**
     * 是否组管理员
     */
    public boolean isGroupManagerById(Long groupId, Long userId) throws SQLException {
        if (null == userId || null == groupId) {
            return false;
        }
        return permissionDao.isGroupManagerById(groupId, userId);
    }

    /**
     * 是否普通组员
     */
    public boolean isNormalManagerById(Long groupId, Long userId) throws SQLException {
        if (null == userId || null == groupId) {
            return false;
        }
        return permissionDao.isNormalManagerById(groupId, userId);
    }

    public boolean isProjectPermission(Long projectId, Long userId) throws SQLException {
        return permissionDao.isProjectPermission(projectId, userId);
    }
}
