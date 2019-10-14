package com.ppdai.das.client;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.ppdai.das.client.delegate.datasync.DataSyncDasDelegate;
import com.ppdai.das.core.configure.DasServerInstance;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ppdai.das.client.delegate.DasDelegate;
import com.ppdai.das.client.delegate.local.ClientDasDelegate;
import com.ppdai.das.client.delegate.remote.DasRemoteDelegate;
import com.ppdai.das.core.DasConfigureContext;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasCoreVersion;
import com.ppdai.das.core.configure.ClientConfigureLoader;
import com.ppdai.das.core.configure.DalConfigure;
import com.ppdai.das.core.helper.ServiceLoaderHelper;

public class DasClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(DasCoreVersion.getLoggerName());
    
    private static final AtomicReference<String> customerClientVersionRef = new AtomicReference<>();
    private static final AtomicReference<String> appIdRef = new AtomicReference<>();

    private static final AtomicBoolean initialzed = new AtomicBoolean(false);
    private static AtomicReference<ClientConfigureLoader> clientLoaderRef = new AtomicReference<>();
    private static final AtomicBoolean proxyModeRef = new AtomicBoolean(false);
    
    
	public static DasClient getClient(String logicDbName) throws SQLException {
	    initClientFactory();
		return new DasClient(create(appIdRef.get(), logicDbName, customerClientVersionRef.get()));
	}
	
    private static DasDelegate create(String appId, String logicDbName, String customerClientVersion) {
        try {
            if(proxyModeRef.get()){
                List<DasServerInstance> servers = clientLoaderRef.get().getServerInstances();
                return new DasRemoteDelegate(appId, logicDbName, customerClientVersion, servers, clientLoaderRef.get().getDalLogger());
            } else {
                ClientDasDelegate dasDelegate = new ClientDasDelegate(appId, logicDbName, customerClientVersion);
                return new DataSyncDasDelegate(dasDelegate);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
	
	public static String getAppId() {
	    return appIdRef.get();
	}
	
    public static void initClientFactory() {
        if(initialzed.get())
            return;

        synchronized(initialzed) {
            if(initialzed.get())
                return;

            logger.info("Start initialize DAS client");
            try {
                ClientConfigureLoader loader = ServiceLoaderHelper.getInstance(ClientConfigureLoader.class);
                if(loader == null)
                    throw new IllegalStateException("Can not find ClientConfigureLoader");
                
                clientLoaderRef.set(loader);
                
                customerClientVersionRef.set(loader.getCustomerDasClientVersion());
                proxyModeRef.set(loader.isProxyEnabled());
                appIdRef.set(loader.getAppId());
                
                logger.info("DAS client connection mode: " + (loader.isProxyEnabled() ? "proxy":"direct"));
                
                DasConfigureContext configContext;                
                if(loader.isProxyEnabled()) {
                    configContext = new DasConfigureContext(loader.getDalLogger());
                }else {
                    Map<String, DalConfigure> configureMap = new HashMap<>();
                    DalConfigure config = loader.load();
                    configureMap.put(loader.getAppId(), config);
                    configContext = new DasConfigureContext(configureMap, config.getDalLogger(), config.getFacory(), config.getLocator(), config.getSelector());
                    logger.info("Successfully load local DAS confiure");
                }

                DasConfigureFactory.initialize(configContext);
                initialzed.set(true);
                logger.info("DAS client successfully initialzed");
            } catch (Throwable e) {
                logger.info("DAS client initilization failed");
                throw new IllegalStateException("Das client initilization failed", e);
            }
        }
    }
}
