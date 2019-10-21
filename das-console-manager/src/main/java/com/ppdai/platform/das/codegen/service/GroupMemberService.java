package com.ppdai.platform.das.codegen.service;


import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dao.UserGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.UserGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.model.page.PagerUtil;
import com.ppdai.platform.das.codegen.dto.view.MemberView;
import com.ppdai.platform.das.codegen.enums.OperateUserEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMemberService {

    @Autowired
    private Consts consts;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private PermissionService permissionService;

    /**
     * 4-组员管理: 新增权限判断
     */
    public ValidatorChain validatePermisionAdd(LoginUser user, UserGroup userGroup, Errors errors) throws SQLException {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> this.validateUserOptUserPermisionInGroup(user, userGroup), "你没有当前DAS Team的组员操作权限.");
    }

    /**
     * 4-组员管理: 修改删除权限判断
     */
    public ValidatorChain validatePermisionUpdateAndDelete(LoginUser user, UserGroup userGroup, Errors errors) throws SQLException {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> this.validateUserOptUserPermisionInGroup(user, userGroup), "你没有当前DAS Team的组员操作权限.")
                .addAssert(() -> this.validateUserPermisionCompare(user, userGroup), "你所授予的权限大于你所拥有的权限.");
    }

    /**
     * 组员的增删改权限
     * 1)是否在管理员组
     * 2)是否在当前组是管理员
     */
    private boolean validateUserOptUserPermisionInGroup(LoginUser user, UserGroup userGroup) throws SQLException {
        return permissionService.isManagerById(user.getId()) || this.canOpterateGroupMember(user.getId(), userGroup.getGroup_id());
    }

    /**
     * 判断用户在当前组的权限是否大于被操作的权限
     */
    private boolean validateUserPermisionCompare(LoginUser user, UserGroup userGroup) throws SQLException {
        if (permissionService.isManagerById(user.getId())) {
            return true;
        }
        if (userService.canUpdateOrDeleteUser(user.getId(), user.getRole(), userGroup.getUser_id(), userGroup.getRole())) {
            return true;
        }
        List<UserGroup> list = userGroupDao.getUserGroupByGroupIdAndUserId(userGroup.getGroup_id(), user.getId());
        if (CollectionUtils.isNotEmpty(list)) {
            if (consts.canManagerCRUDManager) {
                return list.stream().anyMatch(_useGroup -> _useGroup.getRole() <= userGroup.getRole());
            } else {
                return list.stream().anyMatch(_useGroup -> _useGroup.getRole() < userGroup.getRole());
            }
        }
        return false;
    }

    /**
     * user在组是否有操作组员权限
     *
     * @param userId
     * @param currentGroupId
     * @return
     */
    public boolean canOpterateGroupMember(Long userId, Long currentGroupId) throws SQLException {
        List<UserGroup> groups = userGroupDao.getUserGroupByUserId(userId);
        if (CollectionUtils.isNotEmpty(groups)) {
            return groups.stream().anyMatch(group -> group.getOpt_user() == OperateUserEnum.Allow.getType());
        }
        return false;
    }

    public boolean isUserInGroup(Long userId, Long groupId) throws SQLException {
        List<UserGroup> ugGroups = userGroupDao.getUserGroupByUserId(userId);
        if (CollectionUtils.isNotEmpty(ugGroups)) {
            return ugGroups.stream().anyMatch(_userGroup -> _userGroup.getGroup_id().equals(groupId));
        }
        return false;
    }


    /**
     * 判断两个组内是否有重复组员 TODO
     */
    private boolean isRepeatMemberInGroup(Long currentGroupId, Long childGroupId) throws SQLException {
        List<UserGroup> curlist = userGroupDao.getUserGroupByGroupId(currentGroupId);
        List<UserGroup> chidist = userGroupDao.getUserGroupByGroupId(childGroupId);

        List<UserGroup> intersection = curlist.stream().filter(userGroup ->
                chidist.stream().anyMatch(_useGroup -> _useGroup.getUser_id().equals(userGroup.getUser_id()))
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(intersection)) {
            return true;
        }
        return false;
    }


    public ListResult<MemberView> findGroupMemberPageList(Paging<UserGroup> paging) throws SQLException {
        Long count = userGroupDao.getMemberTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<MemberView> list = userGroupDao.findMemberPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

}
