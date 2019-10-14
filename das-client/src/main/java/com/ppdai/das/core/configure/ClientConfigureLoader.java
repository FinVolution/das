package com.ppdai.das.core.configure;

import com.ppdai.das.core.client.DalConnectionLocator;
import com.ppdai.das.core.client.DalLogger;
import com.ppdai.das.core.task.DalTaskFactory;

import java.util.List;

public interface ClientConfigureLoader {
    String getAppId();

    /**
     * @return User's customized Das Client version. For logging purpose
     */
    String getCustomerDasClientVersion();

    boolean isProxyEnabled() throws Exception;

    /**
     * @return DalConfigure for default appID
     * @throws Exception
     */
    DalConfigure load() throws Exception;

    DalLogger getDalLogger() throws Exception;

    DalTaskFactory getDalTaskFactory() throws Exception;

    DalConnectionLocator getDalConnectionLocator() throws Exception;

    DatabaseSelector getDatabaseSelector() throws Exception;

    /**
     * @return all available das server instances
     */
    List<DasServerInstance> getServerInstances() throws Exception;
}
