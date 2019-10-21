package com.ppdai.platform.das.codegen.controller;

import com.ppdai.platform.das.codegen.api.MenuItemConfiguration;
import com.ppdai.platform.das.codegen.api.UserConfiguration;
import com.ppdai.platform.das.codegen.common.utils.StringUtil;
import com.ppdai.platform.das.codegen.config.annotation.CurrentUser;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.MenuItemModel;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * http://adminlte.la998.com/pages/UI/icons.html
 */
@RestController
@RequestMapping(value = "/api")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuItemConfiguration menuItemConfiguration;

    @Autowired
    private UserConfiguration userConfiguration;

    /**
     * http://adminlte.la998.com/pages/UI/icons.html
     * 左侧导航
     */
    @RequestMapping(value = {"/", "/menu"}, method = RequestMethod.GET)
    public ServiceResult getAllApiConfig(@CurrentUser LoginUser user) throws Exception {
        return ServiceResult.success(menuService.getMemus(menuItemConfiguration.getNavigationFlag(user)));
    }

    @RequestMapping(value = {"/item"}, method = RequestMethod.GET)
    public ServiceResult<MenuItemModel> getMenuItem(@CurrentUser LoginUser user) {
        try {
            return ServiceResult.success(menuItemConfiguration.getRemindItem(user));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }

    }

    @RequestMapping(value = {"/items"}, method = RequestMethod.GET)
    public ServiceResult<List<MenuItemModel>> getMenuItems(@CurrentUser LoginUser user) {
        try {
            return ServiceResult.success(menuItemConfiguration.getMenuItems(user));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }


    @RequestMapping(value = {"/linkInfo"}, method = RequestMethod.GET)
    public ServiceResult<List<MenuItemModel>> linkInfo(@CurrentUser LoginUser user) {
        try {
            return ServiceResult.success(userConfiguration.getLinkItem(user));
        } catch (Exception e) {
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }
}
