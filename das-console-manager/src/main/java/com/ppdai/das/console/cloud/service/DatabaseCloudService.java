package com.ppdai.das.console.cloud.service;

import com.ppdai.das.console.api.DataBaseConfiguration;
import com.ppdai.das.console.api.DefaultConfiguration;
import com.ppdai.das.console.common.utils.DasEnv;
import com.ppdai.das.console.common.validates.chain.ValidatorChain;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.dao.*;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.page.ListResult;
import com.ppdai.das.console.dto.model.page.PagerUtil;
import com.ppdai.das.console.dto.view.DataBaseView;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.SetupDataBaseService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Service
public class DatabaseCloudService {

    @Autowired
    private Consts consts;

    @Autowired
    private Message message;

    @Autowired
    private TableEntityDao tableEntityDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private DataBaseDao dataBaseDao;

    @Autowired
    private DatabaseSetDao databaseSetDao;

    @Autowired
    private DataBaseSetEntryDao dataBaseSetEntryDao;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SetupDataBaseService setupDataBaseService;

    @Autowired
    private DeleteCheckDao deleteCheckDao;

    @Autowired
    private DataBaseConfiguration dataBaseConfiguration;

    @Autowired
    private DefaultConfiguration defaultConfiguration;

    /**
     * 5 - 数据库一览 物理数据增删改
     * 删改权限：1）判断user是否在管理员组
     */
    public ValidatorChain validatePermision(LoginUser user, Errors errors) {
        return ValidatorChain.newInstance().controllerValidate(errors)
                .addAssert(() -> permissionService.isManagerById(user.getId()), message.group_message_no_pemission);
    }

    public ListResult<DataBaseView> findDbPageListByUserId(Paging<DataBaseInfo> paging, Long userId) throws SQLException {
        Long count = dataBaseDao.getTotalCountByUserId(paging, userId);
        return PagerUtil.find(count, paging.getPage(), paging.getPageSize(), () -> {
            List<DataBaseView> list = dataBaseDao.findDbPageListByUserId(paging, userId);
            for (DataBaseView dataBaseView : list) {
                dataBaseView.setDb_password(DasEnv.encdecConfiguration.decrypt(dataBaseView.getDb_password()));
            }
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list;
        });
    }

}


