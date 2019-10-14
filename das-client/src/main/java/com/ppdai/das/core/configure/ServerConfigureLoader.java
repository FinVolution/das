package com.ppdai.das.core.configure;

import java.util.List;
import java.util.Map;

import com.ppdai.das.core.client.DalConnectionLocator;
import com.ppdai.das.core.client.DalLogger;
import com.ppdai.das.core.task.DalTaskFactory;

public interface ServerConfigureLoader {
    /**
     * @return User's customized Das Server version. For logging purpose
     */
    String getCustomerDasServerVersion();

    /**
     * @param address
     * @param port
     * @return The server group id, null if current server does not belongs to a server group
     */
    String getServerGroupId(String address, int port) throws Exception;

    /**
     * poolSize,keepAliveTime etc.
     *
     * @return Server instance level configures
     */
    Map<String, String> getServerConfigure() throws Exception ;

    /**
     * For running at server mode, provide all the app ids that allows to connect.
     *
     * @return
     */
    String[] getAppIds(String serverGroupId) throws Exception;

    /**
     * When build DalConfigure, make sure appId is set, so that DalConfigure.getAppId() is present
     *
     * @param appId
     * @return DalConfigure for given appId
     * @throws Exception
     */
    DalConfigure load(String appId) throws Exception;

    DalLogger getDalLogger() throws Exception;

    DalTaskFactory getDalTaskFactory() throws Exception;

    DalConnectionLocator getDalConnectionLocator() throws Exception;

    DatabaseSelector getDatabaseSelector() throws Exception;

    List<Integer> getListeningPorts();
}

