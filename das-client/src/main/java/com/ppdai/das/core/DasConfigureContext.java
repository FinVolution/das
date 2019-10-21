package com.ppdai.das.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.das.core.task.TaskFactory;

public class DasConfigureContext {
    private boolean localMode;
    private DasLogger logger;
    private TaskFactory taskFactory;
    private ConnectionLocator locator;
    private DatabaseSelector selector;
    private Map<String, DasConfigure> configureMap;

    public DasConfigureContext(Map<String, DasConfigure> configureMap, DasLogger logger, TaskFactory taskFactory, ConnectionLocator connectionLocator, DatabaseSelector dbSelector) {
        this.configureMap = new ConcurrentHashMap<>(configureMap);
        this.logger = logger;
        this.taskFactory = taskFactory;
        this.locator = connectionLocator;
        this.selector = dbSelector;
        localMode = true;
    }
    
    public DasConfigureContext(DasLogger logger) {
        this.logger = logger;
        localMode = false;
    }

    public boolean isLocalMode() {
        return localMode;
    }

    public DasLogger getLogger() {
        return logger;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public ConnectionLocator getConnectionLocator() {
        return locator;
    }

    public DatabaseSelector getDatabaseSelector() {
        return selector;
    }
    
    public Set<String> getAppIds() {
        return new HashSet<>(configureMap.keySet());
    }

    public DasConfigure getConfigure(String appId) {
        return configureMap.get(appId);
    }
}