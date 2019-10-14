package com.ppdai.das.client;

/**
 * This interface works with LogicDeletionDao.
 * 
 * @author hejiehui
 *
 * @param <T>
 */
public interface LogicDeletionSupport<T> {
    boolean isDeleted(T entity);

    void setActiveFlag(T entity);

    void setDeletionFlag(T entity);
    
    /**
     * Remove the deletion flag for update. So to avoid accidentally change the flag
     * 
     * @param entity
     */
    void clearDeletionFlag(T entity);

    Object[] getActiveCondition(TableDefinition tableDef);

    Object[] getDeletionCondition(TableDefinition tableDef);
    
    Object[] setDeletionFlag(TableDefinition tableDef);
}
