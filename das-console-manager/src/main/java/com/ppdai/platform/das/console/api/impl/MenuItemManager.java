package com.ppdai.platform.das.console.api.impl;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.api.MenuItemConfiguration;
import com.ppdai.platform.das.console.constant.Consts;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.MenuItemModel;
import com.ppdai.platform.das.console.dto.model.display.NavigationConig;
import com.ppdai.platform.das.console.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MenuItemManager implements MenuItemConfiguration {

    @Autowired
    private Consts consts;

    @Autowired
    private PermissionService permissionService;

    @Override
    public NavigationConig getNavigationFlag(LoginUser user) throws Exception {
        if (permissionService.isManagerById(user.getId())) {
            return NavigationConig.builder()
                    .codeManage(true).projectManage(true).transManage(true).dataSearchMain(false).projectListManage(true)
                    .databaseManage(true).dataBaseGroupManage(true)
                    .dataBaseSetManage(true).publicStrategyManage(false)
                    .groupManage(true).memberManage(true)
                    .appGroupManage(false).serverManage(false)
                    .userManage(true)
                    .groupSyncManage(true).projectSyncManage(true).dataBaseSyncManage(true).dataBaseSetSyncManage(true)
                    .build();
        } else {
            return NavigationConig.builder()
                    .codeManage(true).transManage(true).projectManage(true)
                    .dataBaseSetManage(true)
                    .build();
        }
    }

    @Override
    public List<MenuItemModel> getMenuItems(LoginUser user) {
        return Lists.newArrayList(new MenuItemModel("用户手册", consts.userManualUrl),
                new MenuItemModel("联系我们", consts.adminMailAddress));
    }

    @Override
    public MenuItemModel getRemindItem(LoginUser loginUser) {
        return new MenuItemModel("请联系管理员给当前用户添加组", consts.adminMailAddress);
    }
}
