package com.ppdai.das.client.delegate.remote;

import java.sql.SQLException;

import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.exceptions.DalException;
import com.ppdai.das.core.exceptions.ErrorCode;
import com.ppdai.das.service.DasHints;
import com.ppdai.das.service.DasTransactionId;

public class TransactionClient {
    private String logicDbName;
    private DasLogger logger;
    private ServerSelector serverSelector;

    private static final ThreadLocal<DasTransactionId> transactionHolder = new ThreadLocal<>();

    public TransactionClient(String logicDbName, ServerSelector serverSelector) {
        this.logicDbName = logicDbName;
        this.serverSelector = serverSelector;
        logger = DasConfigureFactory.getLogger();
    }
    
    public static DasTransactionId getCurrentTransaction() {
        return transactionHolder.get();
    }
    
    public <T> T doInTransaction(CallableTransaction<T> transaction, Hints hints)throws SQLException{
        Throwable ex = null;
        T result = null;
        int level;
        try {
            level = startTransaction(hints);

            result = transaction.execute();
            
            endTransaction(level);
        } catch (Throwable e) {
            rollbackTransaction();
            ex = e;
        }

        end(result, ex);
        return result;
    }
    
    private DasHints convert(Hints hints) {
        return DasRemoteDelegate.translateHints(hints);
    }

    private <T> int startTransaction(Hints hints) throws Exception {
        DasTransactionId transactionId = transactionHolder.get();

        String appId = DasClientFactory.getAppId();
        
        if(transactionId == null) {
            transactionId = serverSelector.start(appId, logicDbName, convert(hints));
            transactionHolder.set(transactionId);
        }else{
            validate(hints);
        }
        
        return startTransaction(transactionId);
    }
    
    public int startTransaction(DasTransactionId transactionId) throws SQLException {
        checkState(transactionId);
        
        return transactionId.level++;
    }

    
    //TODO shouold be validate ay server side
    private void validate(Hints hints) {
        //transaction.validate(connManager.getLogicDbName(), connManager.evaluateShard(hints));
//        if(desiganateLogicDbName == null || desiganateLogicDbName.length() == 0)
//            throw new DalException(ErrorCode.LogicDbEmpty);
//        
//        if(!desiganateLogicDbName.equals(this.logicDbName))
//            throw new DalException(ErrorCode.TransactionDistributed, this.logicDbName, desiganateLogicDbName);
//        
//        String curShard = connHolder.getShardId();
//        if(curShard == null)
//            return;
//        
//        if(desiganateShard == null)
//            return;
//        
//        if(!curShard.equals(desiganateShard))
//            throw new DalException(ErrorCode.TransactionDistributedShard, curShard, desiganateShard);
    }

    private void endTransaction(int startLevel) throws Exception {
        DasTransactionId transactionId = transactionHolder.get();
        
        if(transactionId == null)
            throw new SQLException("calling endTransaction with empty ConnectionCache");

        checkState(transactionId);

        if(startLevel != (transactionId.level - 1)) {
            rollbackTransaction();
            throw new DalException(ErrorCode.TransactionLevelMatch, (transactionId.level - 1), startLevel);
        }
        
        if(transactionId.level > 1) {
            transactionId.level--;
            return;
        }
        
        /*
         *  Back to the first transaction, about to commit
         */
        transactionId.level = 0;
        transactionId.completed = true;
        cleanup(transactionId, true);
    }
    
    private void cleanup(DasTransactionId transactionId, boolean commit) {
        Throwable ex = null;
        try {
            if(commit)
                serverSelector.commit(transactionId);
            else
                serverSelector.rollback(transactionId);
        } catch (Throwable e) {
            logger.error("Can not commit or rollback on current connection", e);
            ex = e;
        }

        clearCurrentTransaction();
        serverSelector.removeStick();
        
        if(ex != null)
            throw new RuntimeException(ex);
    }

    private void checkState(DasTransactionId transactionId) throws DalException {
        if(transactionId.rolledBack || transactionId.completed)
            throw new DalException(ErrorCode.TransactionState);
    }

    private void rollbackTransaction() throws SQLException {
        DasTransactionId transactionId = transactionHolder.get();
        
        // Already handled in deeper level
        if(transactionId == null)
            return;

        if(transactionId.rolledBack)
            return;

        transactionId.rolledBack = true;
        // Even the rollback fails, we still set the flag to true;
        cleanup(transactionId, false);
    }    
    
    public static void clearCurrentTransaction() {
        transactionHolder.set(null);
    }
    
    private void end(Object result, Throwable e) throws SQLException {
        log(result, e);
        handleException(e);
    }
    
    private void log(Object result, Throwable e) {
        try {
//            entry.setDuration(System.currentTimeMillis() - start);
//            if(e == null) {
//                logger.success(entry, entry.getResultCount());
//            }else{
//                logger.fail(entry, e);
//            }
        } catch (Throwable e1) {
//            logger.error("Can not log", e1);
        }
    }

    private void handleException(Throwable e) throws SQLException {
        if(e != null)
            throw e instanceof SQLException ? (SQLException)e : DalException.wrap(e);
    }
}
