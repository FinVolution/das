package com.ppdai.das.core;

import java.util.List;

public interface ClientConfigureLoader {
    String getAppId();

    /**
     * @return User's customized Das Client version. For logging purpose
     */
    String getCustomerDasClientVersion();

    boolean isProxyEnabled() throws Exception;

    /**
     * @return DasConfigure for default appID
     * @throws Exception
     */
    DasConfigure load() throws Exception;

    /**
     * @return DasLogger
     * @throws Exception
     */
    DasLogger getDasLogger() throws Exception;

    /**
     * @return all available das server instances
     */
    List<DasServerInstance> getServerInstances() throws Exception;
}
