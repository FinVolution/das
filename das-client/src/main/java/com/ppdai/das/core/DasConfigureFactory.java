package com.ppdai.das.core;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.das.client.DasClientVersion;
import com.ppdai.das.core.client.DalClient;
import com.ppdai.das.core.client.DalDirectClient;
import com.ppdai.das.core.status.StatusManager;
import com.ppdai.das.core.task.DalRequestExecutor;
import com.ppdai.das.core.task.TaskFactory;

public class DasConfigureFactory {
    public static AtomicReference<DasConfigureContext> configContextRef = new AtomicReference<>();
    
    private static Logger logger = LoggerFactory.getLogger(DasClientVersion.getLoggerName());
    private static final AtomicBoolean initialzed = new AtomicBoolean(false);
    
    private static final String THREAD_NAME = "DAS-Server-Configure-Factory-ShutdownHook";

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName(THREAD_NAME);
                shutdownFactory();
            }
        }));
    }

    public static void initialize(DasConfigureContext configContext) {
        if(initialzed.get())
            return;

        synchronized(initialzed) {
            if(initialzed.get())
                return;
            
            try {
                if(configContext.isLocalMode()) {
                    DalRequestExecutor.init(
                            configContext.getTaskFactory().getProperty(DalRequestExecutor.MAX_POOL_SIZE),
                            configContext.getTaskFactory().getProperty(DalRequestExecutor.KEEP_ALIVE_TIME));
            
                    StatusManager.initializeGlobal();
                    for(String appId: configContext.getAppIds())
                        StatusManager.registerApplication(appId, configContext.getConfigure(appId));
                }
                
                LogEntry.init();

                configContextRef.set(configContext);
                initialzed.set(true);
            } catch (Throwable e) {
                throw new IllegalStateException("DasConfigFactory initilization fail", e);
            }
        }
    }
    
    private static DasConfigureContext getContext() {
        if(!initialzed.get())
            throw new IllegalStateException("DasConfigFactory has not been initilized!");
            
        return configContextRef.get();
    }
    
    public static Set<String> getAppIds() {
        return getContext().getAppIds();
    }
    
    public static DasLogger getLogger() {
        return getContext().getLogger();
    }

    public static TaskFactory getTaskFactory() {
        return getContext().getTaskFactory();
    }

    public static ConnectionLocator getConnectionLocator() {
        return getContext().getConnectionLocator();
    }

    public static DatabaseSelector getDatabaseSelector() {
        return getContext().getDatabaseSelector();
    }

    public static void warmUpAllConnections() {
        for(String appId: getContext().getAppIds()) {
            warmUpConnections(appId);
        }
    }
    
    public static DasConfigure getConfigure(String appId) {
        return getContext().getConfigure(appId);
    }

    /**
     * Actively initialize connection pools for all the logic db in the Dal.config
     */
    public static void warmUpConnections(String appId) {
        getContext().getConfigure(appId).warmUpConnections();
    }

    public static DalClient getClient(String appId, String logicDbName) {
        if (logicDbName == null)
            throw new NullPointerException("Database Set name can not be null");

        DasConfigure config = getContext().getConfigure(appId);

        // Verify if it is existed
        config.getDatabaseSet(logicDbName);

        return new DalDirectClient(config, logicDbName);
    }
    
    /**
     * Release All resource the Dal client used.
     */
    public static void shutdownFactory() {
        if (initialzed.get() == false) {
            logger.warn("Dal Java Client Factory is already shutdown.");
            return;
        }

        synchronized (DasConfigureFactory.class) {
            if (initialzed.get() == false) {
                return;
            }

            try {
                logger.info("Start shutdown Dal Java Client Factory");
                
                getLogger().shutdown();
                logger.info("Dal Logger is shutdown");

                DalRequestExecutor.shutdown();
                logger.info("Dal Java Client Factory is shutdown");

                StatusManager.shutdown();

                LogEntry.shutdown();
                logger.info("DalWatcher has been destoryed");
            } catch (Throwable e) {
                logger.error("Error during shutdown", e);
            }
            
            initialzed.set(false);
        }
    }
}