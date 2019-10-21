package com.ppdai.platform.das.codegen.api;

import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.MenuItemModel;
import com.ppdai.platform.das.codegen.dto.model.display.NavigationConig;

import java.util.List;

/**
 * 导航栏的帮助说明接口
 */
public interface MenuItemConfiguration {

    /**
     * 获取导航栏配置信息
     *
     * @param user 当前操作人信息
     * @return
     */
    NavigationConig getNavigationFlag(LoginUser user) throws Exception;

    /**
     * 帮助项显示的下拉菜单的内容
     *
     * @param user 当前操作人信息
     * @return
     */
    List<MenuItemModel> getMenuItems(LoginUser user);

    /**
     * 普通用户未添加组时的提醒
     */
    MenuItemModel getRemindItem(LoginUser user);
}
