package com.ppdai.platform.das.codegen.service;

import com.ppdai.platform.das.codegen.api.DefaultConfiguration;
import com.ppdai.platform.das.codegen.api.EncdecConfiguration;
import com.ppdai.platform.das.codegen.api.UserConfiguration;
import com.ppdai.platform.das.codegen.api.model.UserIdentity;
import com.ppdai.platform.das.codegen.common.exceptions.TransactionException;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.common.utils.Transform;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.LoginUserDao;
import com.ppdai.platform.das.codegen.dao.UserGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.model.page.PagerUtil;
import com.ppdai.platform.das.codegen.dto.view.LoginUserView;
import com.ppdai.platform.das.codegen.enums.OperateUserEnum;
import com.ppdai.platform.das.codegen.enums.RoleTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class UserService {

    @Autowired
    private Message message;

    @Autowired
    private Consts consts;

    @Autowired
    private Transform transform;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private EncdecConfiguration encdecConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    @Autowired
    private UserConfiguration userConfiguration;

    /**
     * 8 - 用户管理
     */
    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    /**
     * user删和改权限控制，user权限必须大于用户权限
     * 1）超级管理远
     * 2）user在管理员组，new不在
     * 3）user权限要大于new
     */
    public boolean canUpdateOrDeleteUser(LoginUser currentUser, LoginUser newUser) throws SQLException {
        if (permissionService.isSuperManagerById(currentUser.getId())) {
            return true;
        }
        return canUpdateOrDeleteUser(currentUser.getId(), currentUser.getRole(), newUser.getId(), newUser.getRole());
    }

    public boolean canUpdateOrDeleteUser(Long userId, Integer userRole, Long newUserId, Integer newUserRole) throws SQLException {
        if (permissionService.isManagerById(userId) && !permissionService.isManagerById(newUserId)) {
            return true;
        }

        if (permissionService.isNormalSuperManagerById(userId) && permissionService.isNormalSuperManagerById(newUserId)) {
            if (userRole < newUserRole) {
                return true;
            }
            if (userRole.equals(newUserRole) && consts.canManagerCRUDManager) {
                return true;
            }
        }
        return false;
    }

    public boolean addUser(LoginUser user) throws SQLException {
        user.setPassword(encdecConfiguration.parseUnidirection(user.getPassword()));
        return loginUserDao.insertUser(user) > 1;
    }

    public boolean update(LoginUser user) throws SQLException {
        //User.setPassword(MD5Util.parseStrToMd5L32(User.getPassword()));
        if (loginUserDao.updateUser(user) > 0) {
            return true;
        }
        return false;
    }

    public ListResult<LoginUserView> findUserPageList(Paging<LoginUser> paging) throws SQLException {
        Long count = loginUserDao.getTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<LoginUserView> list = loginUserDao.findUserPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public boolean initAdminInfo(LoginUser user) throws SQLException {
        user.setId(PermissionService.getSUPERID());
        user.setUserNo(String.valueOf(PermissionService.getSUPERID()));
        user.setUserName(PermissionService.getADMIN_NAME());
        user.setUserRealName(PermissionService.getADMIN_NAME());
        user.setUserEmail(PermissionService.getADMIN_NAME());
        user.setPassword(encdecConfiguration.parseUnidirection(user.getPassword()));
        user.setUpdate_user_no(String.valueOf(PermissionService.getSUPERID()));
        return loginUserDao.getDasClient().execute(() -> {
            int id1 = loginUserDao.initAdmin(user);
            int id2 = groupService.initAdminGroup();
            Long id3 = userGroupDao.insertUserGroup(user.getId(), consts.SUPER_GROUP_ID, RoleTypeEnum.Admin.getType(), OperateUserEnum.Allow.getType());
            if (id1 <= 0 || id2 <= 0 || id3 <= 0) {
                throw new TransactionException(message.db_message_add_operation_failed);
            }
            return true;
        });
    }

    public boolean register(LoginUser user) throws SQLException {
        user.setPassword(encdecConfiguration.parseUnidirection(user.getPassword()));
        return loginUserDao.insertUser(user) > 0;
    }


    public ServiceResult<LoginUser> getUserInfoByWorkName(LoginUser user, String name) {
        try {
            UserIdentity userIdentity = userConfiguration.getUserIdentityByWorkName(user, name);
            return ServiceResult.success(transform.toLoginUser(userIdentity));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }

    public ServiceResult getUserManageButton(LoginUser user) {
        return ServiceResult.success(defaultConfiguration.getUserManageButton(user));
    }

}
