package com.ppdai.das.client.delegate.datasync;


import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataSyncConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncConfiguration.class.getName());

    private final Map<String, DasDataSynchronizer> dasDataSynchronizers = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    private static final DataSyncConfiguration instance = new DataSyncConfiguration();

    private final AtomicBoolean isEnableValidateScheduler = new AtomicBoolean(false);

    private final AtomicBoolean isEnableSyncMode = new AtomicBoolean(false);

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public static DataSyncConfiguration getInstance(){
        return instance;
    }

    private void init() {
        ServiceLoader<DasDataSynchronizer> loader = ServiceLoader.load(DasDataSynchronizer.class);
        for (DasDataSynchronizer dasDataSynchronizer : loader) {
            for (String dbName : dasDataSynchronizer.getSyncLogicDbNames()) {
                DasDataSynchronizer prev = dasDataSynchronizers.put(dbName, dasDataSynchronizer);
                if (prev != null) {
                    throw new IllegalArgumentException("Duplicated DasDataSynchronizers found for logicBD: [" + dbName + "], " +
                            "Please check: [" + dasDataSynchronizer.getClass() + "] and [" + prev.getClass() + "]");
                }
            }
        }

        if(dasDataSynchronizers.isEmpty()) {
            throw new IllegalArgumentException("Cannot enableSyncMode, " +
                    "because no available DasDataSynchronizer implements in file com.ppdai.das.client.delegate.datasync.DasDataSynchronizer");
        }
        startValidateScheduler();
        InMemQueue.init(dasDataSynchronizers.keySet());

        initialized.set(true);
    }

    private DataSyncConfiguration() {
    }

    @Subscribe
    public void receiveContext(DataSyncContext dataSyncContext) {
        dasDataSynchronizers.get(dataSyncContext.getLogicDbName()).syncData(dataSyncContext);
    }

    /**
     * Skip validate scheduler
     */
    public void skipValidateScheduler(){
        isEnableValidateScheduler.set(false);
    }

    /**
     * Continue validate scheduler
     */
    public void continueValidateScheduler(){
        isEnableValidateScheduler.set(true);
    }

    private void startValidateScheduler() {
        if (isValidateJobActive()) {
            isEnableValidateScheduler.set(true);
            for (Map.Entry<String, DasDataSynchronizer> entry : dasDataSynchronizers.entrySet()) {
                String logicDBName = entry.getKey();
                DasDataSynchronizer dasDataSynchronizer = entry.getValue();
                long interval = dasDataSynchronizer.getValidationInterval(logicDBName);
                if(interval > 0){
                    scheduledExecutorService.scheduleAtFixedRate(
                            () -> {
                                try {
                                    if(isEnableValidateScheduler.get()){
                                        dasDataSynchronizer.validate(logicDBName);
                                    }
                                } catch (Exception e) {
                                    logger.error("validate error occur", e);
                                }
                            }, 1, interval, TimeUnit.SECONDS);
                }
            }
        }
    }

    private boolean isValidateJobActive() {
        try(InputStream stream = DataSyncConfiguration.class.getResourceAsStream("/application.properties")){
            if(stream == null) {
                return true;
            }
            Properties properties = new Properties();
            properties.load(stream);
            String validateJob = properties.getProperty("das.sync.validateJob");
            return validateJob == null ? true : Boolean.valueOf(validateJob);
        } catch (IOException ignored) {
        }
        return true;
    }

    public DasDataSynchronizer getDasDataSynchronizer(String logicDBName){
        return dasDataSynchronizers.get(logicDBName);
    }

    /**
     * return das is in Sync mode or not
     */
    public boolean isEnableSyncMode() {
        return isEnableSyncMode.get();
    }

    /**
     * Enable sync mode, default false
     */
    public void enableSyncMode() {
        if(!initialized.get()){
            init();
        }

        isEnableSyncMode.set(true);
    }

    /**
     * Disable sync mode, default false
     */
    public void disableSyncMode() {
        isEnableSyncMode.set(false);
    }

    public void sendContext(DataSyncContext syncContext) {
        InMemQueue.send(syncContext);
    }
}
