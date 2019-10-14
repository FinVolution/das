package com.ppdai.das.client;

import com.ppdai.das.core.client.DalConnectionLocator;
import com.ppdai.das.core.client.DalLogger;
import com.ppdai.das.core.configure.*;
import com.ppdai.das.core.task.DalTaskFactory;

import java.util.Collections;
import java.util.List;

public class XMLClientConfigLoader implements ClientConfigureLoader {

    private DalConfigure dalConfigure = null;

    public XMLClientConfigLoader() throws Exception {
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
    public DalConfigure load() throws Exception {
        return dalConfigure;
    }

    @Override
    public DalLogger getDalLogger() throws Exception {
        return dalConfigure.getDalLogger();
    }

    @Override
    public DalTaskFactory getDalTaskFactory() throws Exception {
        return dalConfigure.getFacory();
    }

    @Override
    public DalConnectionLocator getDalConnectionLocator() throws Exception {
        return dalConfigure.getLocator();
    }

    @Override
    public DatabaseSelector getDatabaseSelector() throws Exception {
        return dalConfigure.getSelector();
    }

    @Override
    public List<DasServerInstance> getServerInstances() throws Exception {
        return Collections.emptyList();
    }
}
