package com.ppdai.platform.das.codegen.api.impl;

import com.ppdai.platform.das.codegen.api.SyncConfiguration;
import com.ppdai.platform.das.codegen.constant.Consts;
import org.springframework.beans.factory.annotation.Autowired;

public class SyncManager implements SyncConfiguration {

    @Autowired
    private Consts consts;

    @Override
    public String getSyncUrl() {
        return consts.dasSyncTarget;
    }
}
