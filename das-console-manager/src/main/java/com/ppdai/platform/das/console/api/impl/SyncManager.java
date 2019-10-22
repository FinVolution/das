package com.ppdai.platform.das.console.api.impl;

import com.ppdai.platform.das.console.api.SyncConfiguration;
import com.ppdai.platform.das.console.constant.Consts;
import org.springframework.beans.factory.annotation.Autowired;

public class SyncManager implements SyncConfiguration {

    @Autowired
    private Consts consts;

    @Override
    public String getSyncUrl() {
        return consts.dasSyncTarget;
    }
}
