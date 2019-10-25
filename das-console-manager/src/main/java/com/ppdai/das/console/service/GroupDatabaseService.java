package com.ppdai.das.console.service;

import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.DataBaseDao;
import com.ppdai.das.console.dao.UserGroupDao;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.entry.das.UserGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.List;

@Service
public class GroupDatabaseService {

    @Autowired
    private Message message;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private PermissionService permissionService;

    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.permisson_user_crud);
    }

    /**
     * dbid 在 groupId 里
     */
    public boolean isGroupHadDB(Long dbId, Long groupId) throws SQLException {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(dbId);
        if (null != dataBaseInfo && null != dataBaseInfo.getDal_group_id() && dataBaseInfo.getDal_group_id().equals(groupId)) {
            return true;
        }
        return false;
    }

    /**
     * DB 是否已经分配到组
     */
    public boolean isGroupHadDB(DataBaseInfo dataBaseModel) throws SQLException {
        DataBaseInfo dataBaseInfo = dataBaseDao.getDataBaseInfoByDbId(dataBaseModel.getId());
        if (null != dataBaseInfo && null != dataBaseInfo.getDal_group_id() && dataBaseInfo.getDal_group_id() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 当前用户所在的所用组是否有dbId 暂不使用
     */
    public boolean validateTransferPermision(Long userId, Long dbId) throws SQLException {
        List<UserGroup> urGroups = userGroupDao.getUserGroupByUserId(userId);
        if (CollectionUtils.isNotEmpty(urGroups)) {
            for (UserGroup urGroup : urGroups) {
                List<DataBaseInfo> dataBaseInfos = dataBaseDao.getGroupDBsByGroup(urGroup.getGroup_id());
                if (CollectionUtils.isNotEmpty(dataBaseInfos)) {
                    return dataBaseInfos.stream().anyMatch(db -> db.getId().equals(dbId));
                }
            }
        }
        return false;
    }

}
