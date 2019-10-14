package com.ppdai.das.core.configure;

public class DataSourceConfigureChangeEvent {
    private String name;
    private DataSourceConfigure newDataSourceConfigure;
    private DataSourceConfigure oldDataSourceConfigure;

    public DataSourceConfigureChangeEvent(String name) {
        this.name = name;
    }

    public DataSourceConfigureChangeEvent(String name, DataSourceConfigure newDataSourceConfigure,
            DataSourceConfigure oldDataSourceConfigure) {
        this(name);
        setNewDataSourceConfigure(newDataSourceConfigure);
        setOldDataSourceConfigure(oldDataSourceConfigure);
    }

    public String getName() {
        return name;
    }

    public DataSourceConfigure getOldDataSourceConfigure() {
        return oldDataSourceConfigure;
    }

    public void setOldDataSourceConfigure(DataSourceConfigure oldDataSourceConfigure) {
        this.oldDataSourceConfigure = oldDataSourceConfigure;
    }

    public DataSourceConfigure getNewDataSourceConfigure() {
        return newDataSourceConfigure;
    }

    public void setNewDataSourceConfigure(DataSourceConfigure newDataSourceConfigure) {
        this.newDataSourceConfigure = newDataSourceConfigure;
    }

}
