package com.ppdai.das.core.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasException;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.ErrorCode;

public class DalTransaction  {
	private final String logicDbName;
	private final DalConnection connHolder;
	private List<DalTransactionListener> listeners;
	private final long startTime;
	private final long timeout;
	private AtomicInteger level = new AtomicInteger(0);
	private AtomicBoolean rolledBack = new AtomicBoolean(false);
	private AtomicBoolean completed = new AtomicBoolean(false);
	private DasLogger logger;
	
	/**
	 * For Das Server transaction
	 * @param connHolder
	 * @param logicDbName
	 * @param timeout
	 * @throws SQLException
	 */
	public DalTransaction(DalConnection connHolder, String logicDbName, long timeout) throws SQLException{
		this.logicDbName = logicDbName;
		this.connHolder = connHolder;
		connHolder.getConn().setAutoCommit(false);
		this.logger = DasConfigureFactory.getLogger();
		startTime = System.currentTimeMillis();
		this.timeout = timeout;
	}

    public DalTransaction(DalConnection connHolder, String logicDbName) throws SQLException{
        this(connHolder, logicDbName, 60 * 1000);
    }
    
    public void validate(String desiganateLogicDbName, String desiganateShard) throws SQLException {
		if(desiganateLogicDbName == null || desiganateLogicDbName.length() == 0)
			throw new DasException(ErrorCode.LogicDbEmpty);
		
		if(!desiganateLogicDbName.equals(this.logicDbName))
			throw new DasException(ErrorCode.TransactionDistributed, this.logicDbName, desiganateLogicDbName);
		
		String curShard = connHolder.getShardId();
		if(curShard == null)
		    return;
		
		if(desiganateShard == null)
		    return;
		
		if(!curShard.equals(desiganateShard))
		    throw new DasException(ErrorCode.TransactionDistributedShard, curShard, desiganateShard);
	}
	
	public String getLogicDbName() {
		return logicDbName;
	}

	public DalConnection getConnection() {
		return connHolder;
	}
    
    public long getStartTime() {
        return startTime;
    }

    public long getTimeout() {
        return timeout;
    }
    
    public long getTimeoutTime() {
        return startTime + timeout;
    }
	
	public void register(DalTransactionListener listener) {
		if(listeners == null)
			listeners = new ArrayList<>();
			
			listeners.add(listener);
	}
	
	public List<DalTransactionListener> getListeners() {
		return listeners;
	}
	
	public int getLevel() {
		return level.get();
	}
	
	public boolean isRolledBack() {
		return rolledBack.get();
	}

	public int startTransaction() throws SQLException {
		if(rolledBack.get() || completed.get())
			throw new DasException(ErrorCode.TransactionState);
		
		return level.getAndIncrement();
	}
	
	public void endTransaction(int startLevel) throws SQLException {
	    if(rolledBack.get() || completed.get())
			throw new DasException(ErrorCode.TransactionState);

	    int curLevel = level.get();
		if(startLevel != (curLevel - 1)) {
			rollbackTransaction();
			throw new DasException(ErrorCode.TransactionLevelMatch, (curLevel - 1), startLevel);
		}
		
		if(curLevel > 1) {
			level.decrementAndGet();
			return;
		}
		
		// Back to the first transaction, about to commit
		beforeCommit();
		level.set(0);
		completed.set(true);
		cleanup(true);
		afterCommit();
	}
	
	public void rollbackTransaction() throws SQLException {
		if(rolledBack.get())
			return;

		beforeRollback();
		rolledBack.set(true);
		// Even the rollback fails, we still set the flag to true;
		cleanup(false);
		afterRollback();
	}
	
	private void cleanup(boolean commit) {
		Connection conn = connHolder.getConn();
		try {
			if(commit)
				conn.commit();
			else
				conn.rollback();
		} catch (Throwable e) {
			logger.error("Can not commit or rollback on current connection", e);
		}

		try {
			conn.setAutoCommit(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		connHolder.close();
		DalTransactionManager.clearCurrentTransaction();
	}
	
	private void beforeCommit() throws SQLException {
		if(listeners == null)
			return;
		
		// The before commit can cause transaction termination by throwing exception
		for(DalTransactionListener listener: listeners)
			listener.beforeCommit();
	}

	private void beforeRollback() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.beforeRollback();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	private void afterCommit() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.afterCommit();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	private void afterRollback() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.afterRollback();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	
	private void logError(Throwable e) {
		try {
			logger.error(e.getMessage(), e);
		} catch (Throwable e2) {
			System.err.println(e2);
		}
	}
}
