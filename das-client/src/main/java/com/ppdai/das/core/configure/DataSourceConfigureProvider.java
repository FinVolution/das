package com.ppdai.das.core.configure;

import java.util.Set;

import com.ppdai.das.core.DasComponent;
import com.ppdai.das.core.DasConfigure;


/**
 * This interface is used by connection locator to provide connection configure. The assumption is different company may
 * have different way of storing connection info for safety reason. The imterface extends DalComponent to receive
 * configuration in connection locater settings
 * 
 * @author jhhe
 *
 */
public interface DataSourceConfigureProvider extends DasComponent {

    /**
     * Declare which databases we want to use.
     * 
     * @param dbNames
     */
    void setup(Set<String> dbNames);

    /**
     * Return null if no such config is found
     * 
     * @param dbName
     * 
     * @return
     */
    DataSourceConfigure getDataSourceConfigure(String dbName);

    /**
     * Allow register datasource config change listener
     * 
     * @param dbName
     * @param listener
     */
    void register(String dbName, DataSourceConfigureChangeListener listener);

    /**
     * DataSourceConfigure changed event
     * @param event
     */
    void onConfigChanged(DasConfigure.DataSourceConfigureEvent event);
}
