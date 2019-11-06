package com.ppdai.das.core;

import java.util.Collections;
import java.util.List;

import com.ppdai.das.core.configure.DalConfigureFactory;

public class DefaultClientConfigLoader implements ClientConfigureLoader {

    private DasConfigure dalConfigure = null;

    public DefaultClientConfigLoader() throws Exception {
        try {
            dalConfigure = DalConfigureFactory.load();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getAppId() {
        return dalConfigure.getAppId();
    }

    @Override
    public String getCustomerDasClientVersion() {
        return "CustomerDasClientVersion";
    }

    @Override
    public boolean isProxyEnabled() throws Exception {
        return false;
    }

    @Override
    public DasConfigure load() throws Exception {
        return dalConfigure;
    }

    @Override
    public DasLogger getDasLogger() throws Exception {
        return dalConfigure.getDasLogger();
    }

    @Override
    public List<DasServerInstance> getServerInstances() throws Exception {
        return Collections.emptyList();
    }
}
