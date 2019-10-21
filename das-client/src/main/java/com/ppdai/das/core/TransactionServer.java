package com.ppdai.das.core;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.client.DalConnection;
import com.ppdai.das.core.client.DalConnectionManager;
import com.ppdai.das.core.client.DalTransaction;
import com.ppdai.das.core.client.DalTransactionManager;
import com.ppdai.das.core.configure.DataSourceConfigureConstants;
import com.ppdai.das.core.datasource.DataSourceLocator;

public class TransactionServer implements DataSourceConfigureConstants {
    private static final Map<String, DalTransaction> transactionMap = new ConcurrentHashMap<>();
    private Timer cleanupTimer = new Timer("DAS Server Transaction Cleanup Timer", true);
    private TransactionIdGenerator generator;
    private final String hostAddress;
    private final String workId;
    
    private static final long SECOND = 1000;
    private static final long INITIAL_DELAY = 1 * SECOND;
    public static final long CLEAN_UP_INTERVAL = 10 * SECOND;
    public static final double REMOVE_SCALE = 1.1;

    private class CleanupTimerTask extends TimerTask {

        @Override
        public void run() {
            // We can use a ring to do this.
            // But we first get it done
            Set<String> ids = new HashSet<>(transactionMap.keySet());
            
            for(String id: ids) {
                if(!transactionMap.containsKey(id))
                    continue;
                
                DalTransaction trans = transactionMap.get(id);
                if(isTimeout(trans))
                    cleanUp(id, trans);
            }
        }
        
        private boolean isTimeout(DalTransaction trans) {
            long curTime = System.currentTimeMillis();
            // Do we add cool down time?
            return curTime > trans.getTimeoutTime();
        }
        
        private void cleanUp(String id, DalTransaction trans) {
            try {
                DasConfigureFactory.getLogger().info("Start clean up transaction: " + id);
                trans.rollbackTransaction();
            } catch (Throwable e) {
                DasConfigureFactory.getLogger().error("Error when rollback timeout transaction: " + id, e);
            }
            transactionMap.remove(id);
        }
    }

    /**
     * @param workerId Ip + port
     * @param connManager
     * @throws SQLException 
     */
    public TransactionServer(String hostAddress, String workId) throws SQLException {
        this.hostAddress = hostAddress;
        this.workId = workId;
        this.generator = new TransactionIdGenerator();
        cleanupTimer.schedule(new CleanupTimerTask(), INITIAL_DELAY, CLEAN_UP_INTERVAL);
    }
    
    public int getCurrentCount() {
        return transactionMap.size();
    }

    public TransactionId start(String appId, String logicDb, Hints hints) throws SQLException {
        DalConnectionManager connManager = locateConnectionManager(appId, logicDb, hints);
        
        DalConnection conn = connManager.getNewConnection(hints, true, EventEnum.EXECUTE);
        long timeout = DataSourceLocator.getDataSourceConfigure(conn.getMeta().getDataBaseKeyName().toLowerCase()).getIntProperty(REMOVEABANDONEDTIMEOUT, DEFAULT_REMOVEABANDONEDTIMEOUT) * SECOND;
        timeout = (long)(timeout * REMOVE_SCALE);
        
        DalTransaction transaction = new DalTransaction(conn, connManager.getLogicDbName(), timeout);
        
        TransactionId id = generator.getNextId(connManager.getLogicDbName(), transaction.getConnection().getDatabaseName(), transaction.getConnection().getShardId(), hostAddress, workId);
        
        transactionMap.put(id.getUniqueId(), transaction);

        transaction.startTransaction();//always 0
        return id;
    }
    
    private DalConnectionManager locateConnectionManager(String appId, String logicDb, Hints hints) {
        return new DalConnectionManager(logicDb, DasConfigureFactory.getConfigure(appId));
    }

    public void commit(String transactionId) throws SQLException {
       DalTransaction transaction = transactionMap.get(transactionId);
        
        if(transaction == null)
            throw new SQLException("calling endTransaction with empty ConnectionCache");

        try {
            transaction.endTransaction(0);//always 0
        }finally{
            transactionMap.remove(transactionId);
        }
    }

    public void rollback(String transactionId) throws SQLException {
        DalTransaction transaction = transactionMap.get(transactionId);
        
        // Already handled in deeper level
        if(transaction == null)
            return;

        try {
            transaction.rollbackTransaction();
        }finally{
            transactionMap.remove(transactionId);
        }
    }

    public <T> T doInTransaction(String transactionId, Callable<T> transaction) throws Exception {
        if(transactionId == null)
            return transaction.call();
        
        prepareTransaction(transactionId);
        try {
            return transaction.call();
        }finally{
            clearTransaction();
        }
    }

    private void prepareTransaction(String transactionId) {
        DalTransactionManager.setCurrentTransaction(transactionMap.get(transactionId));
    }

    private void clearTransaction() throws SQLException {
        DalTransactionManager.setCurrentTransaction(null);
    }
}
