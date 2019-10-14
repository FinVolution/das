package com.ppdai.das.core.status;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.das.core.DasCoreVersion;
import com.ppdai.das.core.configure.DalConfigure;
import com.ppdai.das.core.markdown.MarkdownManager;

/**
 * TODO add logic DB level markdown control
 * @author jhhe
 *
 */
public class DalStatusManager {
	private static Logger logger = LoggerFactory.getLogger(DasCoreVersion.getLoggerName());
	
	private static final String GLOBAL_CONFIG_DOMAIN_PREFIX = "com.ppdai.das";
	private static final String TYPE = "type";
	private static final String APP_LOGIC_DB_CONFIG_DOMAIN_PREFIX = "com.ppdai.das.app.%s.DatabaseSet";
//	private static final String LOGIC_DB_CONFIG_DOMAIN_PREFIX = "com.ctrip.dal.client.DatabaseSet";
	private static final String DATASOURCE_CONFIG_DOMAIN_PREFIX = "com.ppdai.das.DataSource";
	
	private static AtomicBoolean initialized = new AtomicBoolean(false);
	private static AtomicReference<TimeoutMarkdown> timeoutMarkDownRef = new AtomicReference<>();
	private static AtomicReference<HAStatus> haStatusRef = new AtomicReference<>();
	private static AtomicReference<MarkdownStatus> markdownStatusRef = new AtomicReference<>();
	private static Map<String, Map<String, DatabaseSetStatus>> appLogicDbs = new ConcurrentHashMap<>();
	private static Map<String, DataSourceStatus> dataSources = new ConcurrentHashMap<>();
	
	public static void initializeGlobal() throws Exception {
		if(initialized.get() == true)
			return;
		
		synchronized (DalStatusManager.class) {
			if(initialized.get() == true)
				return;

			verifyRegistration();
			registerGlobal();
//			registerDatabaseSets(config.getDatabaseSetNames());
//			registerDataSources(config.getDataSourceNames());
			MarkdownManager.init();
			
			initialized.set(true);;
		}
	}
	
    public static void registerApplication(String appId, DalConfigure config) throws Exception {
        synchronized (DalStatusManager.class) {
            registerDatabaseSets(appId, config.getDatabaseSetNames());
            registerDataSources(config.getDataSourceNames());
        }
    }
    
	private static void verifyRegistration() throws Exception{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		
		if(mbs.isRegistered(getGlobalName(HAStatus.class))){
			logger.warn("DAL Management Bean has already been initialized. Please make remove your application's Context registration in server.xml under Tomcat conf folder.");
		};
	}
	
	private static void registerMBean(Object mBean, ObjectName name) throws Exception{
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if(mbs.isRegistered(name))
			mbs.unregisterMBean(name);

		mbs.registerMBean(mBean, name);
	}
	
	private static void registerGlobal() throws Exception{
		haStatusRef.set(new HAStatus());
		registerMBean(haStatusRef.get(), getGlobalName(HAStatus.class));
		
		timeoutMarkDownRef.set(new TimeoutMarkdown());
		registerMBean(timeoutMarkDownRef.get(), getGlobalName(TimeoutMarkdown.class));
		
		markdownStatusRef.set(new MarkdownStatus());
		registerMBean(markdownStatusRef.get(), getGlobalName(MarkdownStatus.class));
	}

	private static void registerDatabaseSets(String appId, Set<String> logicDbNames) throws Exception {
	    Map<String, DatabaseSetStatus> logicDbs = new ConcurrentHashMap<>();
	    
		for(String name: logicDbNames) {
			DatabaseSetStatus status = new DatabaseSetStatus(appId, name);
			registerMBean(status, new ObjectName(String.format(APP_LOGIC_DB_CONFIG_DOMAIN_PREFIX, appId), TYPE, name));
			logicDbs.put(name, status);
		}
		
		appLogicDbs.put(appId, logicDbs);
	}

	private static void registerDataSources(Set<String> datasourceNames) throws Exception {
		for(String name: datasourceNames) {
			DataSourceStatus status = new DataSourceStatus(name);
			registerMBean(status, new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
			dataSources.put(name, status);
		}
	}
	
	public static void shutdown() throws Exception {
		if(initialized.get() == false)
			return;
		
		synchronized (DalStatusManager.class) {
			if(initialized.get() == false)
				return;
			MarkdownManager.shutdown();
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.unregisterMBean(getGlobalName(HAStatus.class));
			mbs.unregisterMBean(getGlobalName(TimeoutMarkdown.class));
			mbs.unregisterMBean(getGlobalName(MarkdownStatus.class));
			
			for(String name: dataSources.keySet())
				mbs.unregisterMBean(new ObjectName(DATASOURCE_CONFIG_DOMAIN_PREFIX, TYPE, name));
			dataSources.clear();
			
			for(String appId: appLogicDbs.keySet()) {
			    Map<String, DatabaseSetStatus> logicDbs = appLogicDbs.get(appId);
    			for(String name: logicDbs.keySet())
    				mbs.unregisterMBean(new ObjectName(String.format(APP_LOGIC_DB_CONFIG_DOMAIN_PREFIX, appId), TYPE, name));
                logicDbs.clear();
			}
			
			appLogicDbs.clear();
			
			initialized.set(false);
		}
	}
	
	private static ObjectName getGlobalName(Class clazz) throws Exception {
		return new ObjectName(GLOBAL_CONFIG_DOMAIN_PREFIX, TYPE, clazz.getSimpleName());
	}

	public static TimeoutMarkdown getTimeoutMarkdown() {
		return timeoutMarkDownRef.get();
	}

	public static HAStatus getHaStatus() {
		return haStatusRef.get();
	}

	public static MarkdownStatus getMarkdownStatus() {
		return markdownStatusRef.get();
	}
	
	public static DatabaseSetStatus getDatabaseSetStatus(String appId, String dbName) {
		return appLogicDbs.get(appId).get(dbName);
	}
	
    public static Set<String> getDataSourceNames() {
        return new HashSet<>(dataSources.keySet());
    }
    
	public static DataSourceStatus getDataSourceStatus(String dbName) {
		return dataSources.get(dbName);
	}
	
	public static boolean containsDataSourceStatus(String dbName) {
		return dataSources.containsKey(dbName);
	}
}
