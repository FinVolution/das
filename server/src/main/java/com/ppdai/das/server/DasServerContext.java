package com.ppdai.das.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.core.DataBase;
import com.ppdai.das.core.DatabaseSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.das.core.DasConfigureContext;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasConfigure;
import com.ppdai.das.core.ServerConfigureLoader;
import com.ppdai.das.core.helper.ServiceLoaderHelper;
import com.ppdai.das.core.status.StatusManager;
import com.ppdai.das.core.task.DalRequestExecutor;

/**
 * @author hejiehui
 */
public class DasServerContext {
    private static Logger logger = LoggerFactory.getLogger(DasServerVersion.getLoggerName());

    private static final String NEW_LINE = System.lineSeparator();
    private String workerId;

    private String serverGroup;
    private Map<String, String> serverConfigure;
    private ServerConfigureLoader serverLoader;
    
    public DasServerContext(String address, int port) {
        // This can be designate
        // This is just for test
        workerId = String.format("Server-%s-%d", address, port);
        Map<String, DasConfigure> configureMap = new HashMap<>();

        try {
            logger.info("Load Das Server configure");
            
            serverLoader = ServiceLoaderHelper.getInstance(ServerConfigureLoader.class);
            if(serverLoader == null)
                return;
            
            logger.info("Load Das Server Group");
            serverGroup = serverLoader.getServerGroupId(address, port);
            logger.info("Das Server Group is " + serverGroup);

            logger.info("Load Das Server Configure");
            serverConfigure = serverLoader.getServerConfigure();
            logger.info("Das Server Configures are " + serverConfigure);
            
            String poolSize = serverConfigure.get(DalRequestExecutor.MAX_POOL_SIZE);
            String keepAliveTime = serverConfigure.get(DalRequestExecutor.KEEP_ALIVE_TIME);

            DalRequestExecutor.init(poolSize, keepAliveTime);
            StatusManager.initializeGlobal();

            for(String appId: serverLoader.getAppIds(serverGroup)) {
                DasConfigure config = serverLoader.load(appId);
                if(config == null)
                    throw new IllegalStateException("Can not load dal confiure for app: " + appId);
                configureMap.put(appId, config);
            }
            
            DasConfigureContext configContext = new DasConfigureContext(configureMap, serverLoader.getDasLogger(), serverLoader.getDalTaskFactory(), serverLoader.getDalConnectionLocator(), serverLoader.getDatabaseSelector());
            DasConfigureFactory.initialize(configContext);

        } catch (Throwable e) {
            throw new IllegalStateException("Das Server Context initilization fail", e);
        } finally {
            logDalConfigure(configureMap, address, port);
        }
    }

    void logDalConfigure( Map<String, DasConfigure> configureMap, String address, int port){
        StringBuilder sb = new StringBuilder();
        sb.append("This server address: [" + address + "], port: [" + port + "]" ).append(NEW_LINE);
        sb.append("Server group: [" + serverGroup + "]").append(NEW_LINE);
        sb.append("Server configuration: [" + serverConfigure + "]").append(NEW_LINE);
        for(Map.Entry<String, DasConfigure> en : configureMap.entrySet()) {
            DasConfigure dalConfigure = en.getValue();
            sb.append("DalConfigure for appId [" + en.getKey() + "], its logicDBs: " + dalConfigure.getDatabaseSetNames()).append(NEW_LINE);
            for(String logicDB: dalConfigure.getDatabaseSetNames()){
                sb.append("    logicDB: [" + logicDB + "]").append(NEW_LINE);
                DatabaseSet dbSet = dalConfigure.getDatabaseSet(logicDB);
                for(Map.Entry<String, DataBase> db: dbSet.getDatabases().entrySet()){
                    sb.append("        |- DataBase: [" + db.getKey() + "] -> ConnectionString: [" + db.getValue().getConnectionString() + "], Sharding: [" + db.getValue().getSharding() + "]").append(NEW_LINE);
                }
            }
        }
        logger.info(sb.toString());
    }

    /**
     * Return NULL if not found
     * @param ip
     * @param port
     * @return 
     */
    public String getWorkerId(){
        return workerId;
    }
    
    public Set<String> getAppIds() {
        return new HashSet<>(serverConfigure.keySet());
    }
    
    public Map<String, String> getServerConfigure() {
        return new HashMap<String, String>(serverConfigure);
    }
    
    public String getServerGroup() {
        return serverGroup;
    }
}
