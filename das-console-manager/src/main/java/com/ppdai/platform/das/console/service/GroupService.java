package com.ppdai.platform.das.console.service;

import com.ppdai.platform.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.console.constant.Consts;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.GroupDao;
import com.ppdai.platform.das.console.dao.LoginUserDao;
import com.ppdai.platform.das.console.dao.ProjectDao;
import com.ppdai.platform.das.console.dao.UserGroupDao;
import com.ppdai.platform.das.console.dto.entry.das.DasGroup;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.entry.das.UserGroup;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.page.ListResult;
import com.ppdai.platform.das.console.dto.model.page.PagerUtil;
import com.ppdai.platform.das.console.dto.view.DalGroupView;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private Consts consts;

    @Autowired
    private Message message;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private LoginUserDao loginUserDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ProjectDao projectDao;

    /**
     * 4-组管理: 新增权限判断
     */
    public ValidatorChain validatePermision(LoginUser user, Errors errors) throws SQLException {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.group_message_no_pemission);
    }

    public boolean addDalGroup(LoginUser user, DasGroup group) throws SQLException {
        group.setUpdate_user_no(user.getUserNo());
        if (groupDao.insertDasGroup(group) <= 0) {
            return false;
        }
        return true;
    }

    public boolean updateDalGroup(LoginUser user, DasGroup group) throws SQLException {
        group.setUpdate_user_no(user.getUserNo());
        if (groupDao.updateDalGroup(group) <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 3-组管理：组的增删改权限
     * <p>
     * 当前user是否在超级组
     *
     * @param userNo
     * @return
     * @throws SQLException
     */
    public boolean isInSuperGroup(String userNo) throws SQLException {
        LoginUser user = loginUserDao.getUserByNo(userNo);
        List<UserGroup> groups = userGroupDao.getUserGroupByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(groups)) {
            return groups.stream().anyMatch(group -> group.getUser_id().equals(consts.SUPER_GROUP_ID));
        }
        return false;
    }

    public boolean isInCurrentGroup(Long userId, int currentGroupId) throws SQLException {
        List<UserGroup> urgroups = userGroupDao.getUserGroupByUserId(userId);
        if (CollectionUtils.isNotEmpty(urgroups) && urgroups.stream().anyMatch(ug -> ug.getUser_id() == currentGroupId)) {
            return true;
        }
        return false;
    }

    public ListResult<DalGroupView> findGroupPageList(Paging<DasGroup> paging) throws SQLException {
        Long count = groupDao.getTotalCount(paging);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DalGroupView> list = groupDao.findGroupPageList(paging);
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

    public boolean isNotExistByName(DasGroup group) throws SQLException {
        Long n = groupDao.getCountByName(group.getGroup_name());
        Long i = groupDao.getCountByIdAndName(group.getId(), group.getGroup_name());
        if (n == 0 || (n == 1 && i == 1)) {
            return true;
        }
        return false;
    }

    public boolean isNotExistInProjectAndGroup(String name) throws SQLException {
        name = name.trim();
        return projectDao.getCountByName(name) == 0 && groupDao.getCountByName(name) == 0;
    }

    public int initAdminGroup() throws SQLException {
        DasGroup group = new DasGroup();
        group.setId(PermissionService.getADMINGROUPID());
        group.setGroup_name(PermissionService.getADMIN_NAME());
        group.setGroup_comment("管理员组");
        group.setUpdate_user_no(String.valueOf(PermissionService.getSUPERID()));
        return groupDao.initAdminGroup(group);
    }
}
