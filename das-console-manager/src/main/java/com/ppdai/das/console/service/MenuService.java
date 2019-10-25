package com.ppdai.das.console.service;

import com.google.common.collect.Lists;
import com.ppdai.das.console.constant.Consts;
import com.ppdai.das.console.dto.model.display.NavigationConig;
import com.ppdai.das.console.dto.view.menu.ChildModel;
import com.ppdai.das.console.dto.view.menu.ItemModel;
import com.ppdai.das.console.dto.view.menu.MemuModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private Consts consts;

    public List<MemuModel> getMemus(NavigationConig navigationConig) {
        MemuModel memuModel = MemuModel.builder().header("DAS-CONSOLE").className("fa-dashboard").build();
        List<ItemModel> items = Lists.newArrayList();
        if (navigationConig.isCodeManage() || navigationConig.isProjectManage() || navigationConig.isTransManage() || navigationConig.isDataSearchMain() || navigationConig.isProjectListManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-laptop").title("常用功能").children(new ArrayList<>()).build();
            if (navigationConig.isCodeManage()) {
                itemModel.getChildren().add(new ChildModel("代码生成器", "codeManage", "fa-file-code-o"));
            }
            if (navigationConig.isProjectManage()) {
                itemModel.getChildren().add(new ChildModel("项目管理", "projectManage", "fa-files-o"));
            }
            if (navigationConig.isTransManage()) {
                itemModel.getChildren().add(new ChildModel("代码转换", "transManage", "fa-random"));
            }
            if (navigationConig.isDataSearchMain()) {
                itemModel.getChildren().add(new ChildModel("数据查询", "dataSearchMain", " fa-search-plus"));
            }
            if (navigationConig.isProjectListManage()) {
                itemModel.getChildren().add(new ChildModel("项目一览", "projectListManage", "fa-table"));
            }
            items.add(itemModel);
        }
        if (navigationConig.isDatabaseManage() || navigationConig.isDataBaseGroupManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-database").title("物理库管理").children(new ArrayList<>()).build();
            if (navigationConig.isDatabaseManage()) {
                itemModel.getChildren().add(new ChildModel("物理库管理", "databaseManage", "fa-th-list"));
            }
            if (navigationConig.isDataBaseGroupManage()) {
                itemModel.getChildren().add(new ChildModel("物理库分组", "dataBaseGroupManage", "fa-cubes"));
            }
            items.add(itemModel);
        }
        if (navigationConig.isDataBaseSetManage() || navigationConig.isPublicStrategyManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-empire").title("逻辑库管理").children(new ArrayList<>()).build();
            if (navigationConig.isDataBaseSetManage()) {
                itemModel.getChildren().add(new ChildModel("逻辑数据库管理", "dataBaseSetManage", "fa-arrows-alt"));
            }
            if (navigationConig.isPublicStrategyManage()) {
                itemModel.getChildren().add(new ChildModel("公共策略管理", "publicStrategyManage", "fa-gg"));
            }
            items.add(itemModel);
        }
        if (navigationConig.isGroupManage() || navigationConig.isMemberManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-balance-scale").title("组资源管理").children(new ArrayList<>()).build();
            if (navigationConig.isGroupManage()) {
                itemModel.getChildren().add(new ChildModel("组管理", "groupManage", "fa-laptop"));
            }
            if (navigationConig.isMemberManage()) {
                itemModel.getChildren().add(new ChildModel("组员管理", "memberManage", "fa-group"));
            }
            items.add(itemModel);
        }
        if (navigationConig.isAppGroupManage() || navigationConig.isServerManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-gears").title("服务管理").children(new ArrayList<>()).build();
            if (navigationConig.isAppGroupManage()) {
                itemModel.getChildren().add(new ChildModel("应用组管理", "appGroupManage", "fa-th"));
            }
            if (navigationConig.isServerManage()) {
                itemModel.getChildren().add(new ChildModel("服务器管理", "serverManage", "fa-server"));
            }
            items.add(itemModel);
        }
        if (navigationConig.isUserManage()) {
            ItemModel itemModel = ItemModel.builder().className("fa-institution").title("系统管理").children(new ArrayList<>()).build();
            if (navigationConig.isUserManage()) {
                itemModel.getChildren().add(new ChildModel("用户管理", "userManage", "fa-male"));
            }
            items.add(itemModel);
        }
        if (StringUtils.isNotBlank(consts.dasSyncTarget)) {
            if (navigationConig.isGroupSyncManage() || navigationConig.isProjectSyncManage() || navigationConig.isDataBaseSyncManage() || navigationConig.isDataBaseSetSyncManage()) {
                ItemModel itemModel = ItemModel.builder().className("fa-recycle").title("数据同步").children(new ArrayList<>()).build();
                if (navigationConig.isGroupSyncManage()) {
                    itemModel.getChildren().add(new ChildModel("组同步", "groupSyncManage", "fa-laptop"));
                }
                if (navigationConig.isProjectSyncManage()) {
                    itemModel.getChildren().add(new ChildModel("项目同步", "projectSyncManage", "fa-files-o"));
                }
                if (navigationConig.isDataBaseSyncManage()) {
                    itemModel.getChildren().add(new ChildModel("物理库同步", "dataBaseSyncManage", "fa-cubes"));
                }
                if (navigationConig.isDataBaseSetSyncManage()) {
                    itemModel.getChildren().add(new ChildModel("逻辑数据库同步", "dataBaseSetSyncManage", "fa-arrows-alt"));
                }
                items.add(itemModel);
            }
        }
        memuModel.setItems(items);
        return Lists.newArrayList(memuModel);
    }
}
