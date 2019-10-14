package com.ppdai.das.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.das.core.client.DalConnectionLocator;
import com.ppdai.das.core.client.DalLogger;
import com.ppdai.das.core.configure.DalConfigure;
import com.ppdai.das.core.configure.DatabaseSelector;
import com.ppdai.das.core.task.DalTaskFactory;

public class DasConfigureContext {
    private boolean localMode;
    private DalLogger logger;
    private DalTaskFactory taskFactory;
    private DalConnectionLocator locator;
    private DatabaseSelector selector;
    private Map<String, DalConfigure> configureMap;

    public DasConfigureContext(Map<String, DalConfigure> configureMap, DalLogger logger, DalTaskFactory taskFactory, DalConnectionLocator connectionLocator, DatabaseSelector dbSelector) {
        this.configureMap = new ConcurrentHashMap<>(configureMap);
        this.logger = logger;
        this.taskFactory = taskFactory;
        this.locator = connectionLocator;
        this.selector = dbSelector;
        localMode = true;
    }
    
    public DasConfigureContext(DalLogger logger) {
        this.logger = logger;
        localMode = false;
    }

    public boolean isLocalMode() {
        return localMode;
    }

    public DalLogger getDalLogger() {
        return logger;
    }

    public DalTaskFactory getDalTaskFactory() {
        return taskFactory;
    }

    public DalConnectionLocator getDalConnectionLocator() {
        return locator;
    }

    public DatabaseSelector getDatabaseSelector() {
        return selector;
    }
    
    public Set<String> getAppIds() {
        return new HashSet<>(configureMap.keySet());
    }

    public DalConfigure getConfigure(String appId) {
        return configureMap.get(appId);
    }
}