package com.ppdai.platform.das.console.dto.model.display;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationConig {

    /**
     * 常用功能
     */
    private boolean codeManage;
    private boolean projectManage;
    private boolean transManage;
    private boolean dataSearchMain;
    private boolean projectListManage;

    /**
     * 物理库管理
     */
    private boolean databaseManage;
    private boolean dataBaseGroupManage;

    /**
     * 逻辑库管理
     */
    private boolean dataBaseSetManage;
    private boolean publicStrategyManage;

    /**
     * 组资源管理
     */
    private boolean groupManage;
    private boolean memberManage;

    /**
     * 服务管理
     */
    private boolean appGroupManage;
    private boolean serverManage;

    /**
     * 系统管理
     */
    private boolean userManage;

    /**
     * 数据同步
     */
    private boolean groupSyncManage;
    private boolean projectSyncManage;
    private boolean dataBaseSyncManage;
    private boolean dataBaseSetSyncManage;

}
