package com.ppdai.platform.das.codegen.api.impl;

import com.ppdai.platform.das.codegen.api.DefaultConfiguration;
import com.ppdai.platform.das.codegen.api.UserConfiguration;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.model.display.*;
import com.ppdai.platform.das.codegen.openapi.ConfigProvider;
import com.ppdai.platform.das.codegen.openapi.LoginProvider;
import com.ppdai.platform.das.codegen.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultManager implements DefaultConfiguration {

    @Autowired
    private ConfigProvider configProvider;

    @Autowired
    private LoginProvider loginProvider;

    @Autowired
    private UserConfiguration userConfiguration;

    @Autowired
    private PermissionService permissionService;

    private String LoginProviderImplClass = "com.ppdai.platform.das.codegen.openapi.impl.LoginProviderDefaultImpl";

    private String ConfigProviderImplClass = "com.ppdai.platform.das.codegen.openapi.impl.ConfigProviderDefaultImpl";

    @Override
    public String getConfigCenterName() {
        return configProvider.getConfigCenterName();
    }

    @Override
    public String getSecurityCenterName() {
        return "安全配置";
    }

    @Override
    public ProjectButton getProjectButton(LoginUser user) {
        ProjectButton projectButton = ProjectButton.builder().showAddButton(true).showEditorButton(true).showDownloadButton(true).build();
        try {
            if (permissionService.isManagerById(user.getId())) {
                projectButton.setShowDeleteButton(true);
                if (!isConfigDefaultImpl()) {
                    projectButton.setShowSyncButton(true);
                    projectButton.setShowCkeckButton(true);
                }
            }
        } catch (Exception e) {
            return projectButton;
        }
        return projectButton;
    }

    @Override
    public DataBaseButton getDataBaseButton(LoginUser user) {
        DataBaseButton dataBaseButton = DataBaseButton.builder().showAddButton(true).showEditorButton(true).showCatalogsButton(true).build();
        try {
            if (permissionService.isManagerById(user.getId())) {
                dataBaseButton.setShowDeleteButton(true);
                if (!isConfigDefaultImpl()) {
                    dataBaseButton.setShowCkeckButton(true);
                    dataBaseButton.setShowSyncButton(true);
                }
            }
        } catch (Exception e) {
            return dataBaseButton;
        }
        return dataBaseButton;
    }

    @Override
    public DbSetButton getDbSetButton(LoginUser user) {
        DbSetButton dbSetButton = DbSetButton.builder().showAddButton(true).showCkeckAllButton(false).showEditorButton(true).build();
        try {
            if (permissionService.isManagerById(user.getId())) {
                if (!isConfigDefaultImpl()) {
                    dbSetButton.setShowCkeckButton(true);
                    dbSetButton.setShowSyncButton(true);
                }
                dbSetButton.setShowDeleteButton(true);
            }
        } catch (Exception e) {
            return dbSetButton;
        }
        return dbSetButton;
    }

    @Override
    public DbSetEntryButton getDbSetEntryButton(LoginUser user) {
        DbSetEntryButton dbSetEntryButton = DbSetEntryButton.builder().showAddButton(true).showEditorButton(true).showDeleteButton(true).build();
        try {
            if (permissionService.isManagerById(user.getId())) {
                if (!isConfigDefaultImpl()) {
                    dbSetEntryButton.setShowSyncButton(true);
                    dbSetEntryButton.setShowCkeckButton(true);
                }
                dbSetEntryButton.setShowDeleteButton(true);
            }
        } catch (Exception e) {
            return dbSetEntryButton;
        }
        return dbSetEntryButton;
    }

    @Override
    public UserManageButton getUserManageButton(LoginUser user) {
        return UserManageButton.builder().showAddButton(userConfiguration.isUseSSO()).showEditorButton(true).showSimLoginButton(true).build();
    }

    private boolean isConfigDefaultImpl() {
        return ConfigProviderImplClass.equals(configProvider.getClass().getName());
    }
}