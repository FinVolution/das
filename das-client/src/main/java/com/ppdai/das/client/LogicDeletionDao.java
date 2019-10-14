package com.ppdai.das.client;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.ppdai.das.client.delegate.local.PPDaiDalParser;
import com.ppdai.das.core.DalParser;

/**
 * Table DAO that supports logic delete.
 * 
 * It works with LogicDeletionSupport to do the job at dao level.
 * 
 * When deleted recorder is found for update, it will raise exception; 
 * When deleted recorder is found for query result, it will be silently removed
 * 
 * All the existing methods are marked as final to make sure no subclass can modifies 
 * them to avoid potential risk.
 * 
 * If you have strong reason to override any of the final method, please let me know.
 * 
 * @author hejiehui
 *
 * @param entity type that represents the underline table.
 */
public class LogicDeletionDao<T> extends TableDao<T>{
    private final TableDefinition table;

    private final LogicDeletionSupport<T> deletionSupport;
    
    public LogicDeletionDao(String logicDbName, Class<T> entityType, LogicDeletionSupport<T> deletionSupport) throws SQLException {
        this(logicDbName, entityType, deletionSupport, true, true);
    }
    
    public LogicDeletionDao(String logicDbName, Class<T> entityType, LogicDeletionSupport<T> deletionSupport, boolean validateInput, boolean filterOutput) throws SQLException {
        super(logicDbName, entityType);
        this.deletionSupport = deletionSupport;
        
        try {
            Field tableDef = entityType.getDeclaredField(entityType.getSimpleName().toUpperCase());
            table = (TableDefinition) tableDef.get(null);
        } catch (Throwable e) {
            throw new SQLException("Can not initialize table DAO", e);
        }
    }

    /**
     * @return the deletion support utility
     */
    public final LogicDeletionSupport<T> getDeletionSupport() {
        return deletionSupport;
    }

    @Override
    public final T queryByPk(T pk, Hints...hints) throws SQLException {
        validateInput(pk);
        setActiveFlag(pk);//Actually this has no effect
        return processOutput(super.queryByPk(pk, hints));
    }
    
    @Override
    public final List<T> queryBySample(T sample, Hints...hints) throws SQLException {
        validateInput(sample);
        setActiveFlag(sample);
        return processOutput(super.queryBySample(sample, hints));
    }
    
    @Override
    public List<T> queryBySample(T sample, PageRange range, Hints...hints) throws SQLException {
        validateInput(sample);
        setActiveFlag(sample);
        return processOutput(super.queryBySample(sample, range, hints));
    }

    @Override
    public final long countBySample(T sample, Hints...hints) throws SQLException {
        validateInput(sample);
        setActiveFlag(sample);
        return super.countBySample(sample, hints);
    }
    
    @Override
    public final int insert(T entity, Hints...hints) throws SQLException {
        validateInput(entity);
        setActiveFlag(entity);
        return super.insert(entity, hints);
    }
    
    @Override
    public final int insert(List<T> entities, Hints...hints) throws SQLException {
        validateInput(entities);
        setActiveFlag(entities);
        return super.insert(entities, hints);
    }
    
    @Override
    public final int[] batchInsert(List<T> entities, Hints...hints) throws SQLException {
        validateInput(entities);
        setActiveFlag(entities);
        return super.batchInsert(entities, hints);
    }
    
    @Override
    public final int deleteByPk(T pk, Hints...hints) throws SQLException {
        validateInput(pk);
        setDeletionFlag(pk);
        return super.update(pk, hints);
    }
    
    @Override
    public final int deleteBySample(T sample, Hints...hints) throws SQLException {
        validateInput(sample);
        SqlBuilder builder = SqlBuilder.update(table).set(deletionSupport.setDeletionFlag(table)).where().
                allOf(SegmentConstants.match(table, sample), getActiveCondition(table));
        
        return client.update(builder.setHints(client.checkHints(hints)));
    }
    
    @Override
    public final int[] batchDelete(List<T> entities, Hints...hints) throws SQLException {
        validateInput(entities);
        setDeletionFlag(entities);
        return super.batchUpdate(entities, hints);
    }

    public final int update(T entity, Hints...hints) throws SQLException {
        validateInput(entity);
        clearDeletionFlag(entity);
        return super.update(entity, hints);
    }

    public final int[] batchUpdate(List<T> entities, Hints...hints) throws SQLException {
        validateInput(entities);
        clearDeletionFlag(entities);
        return super.batchUpdate(entities, hints);
    }
    
    protected final boolean isDeleted(T entity) {
        return deletionSupport.isDeleted(entity);
    }

    /**
     * Helper method that removes all recorders that is marked as deleted from original list.
     * 
     * @param entities
     * @return recorders that is marked as deleted
     */
    protected final List<T> filterDeleted(List<T> entities) {
        List<T> deleted = new LinkedList<>();
        for(int i = entities.size() -1; i >= 0; i--)
            if(deletionSupport.isDeleted(entities.get(i)))
                deleted.add(entities.remove(i));
        return deleted;
    }
    
    /**
     * Helper method that checks if there is any recorders that is marked as deleted.
     * 
     * @param entities
     * @return
     */
    protected final boolean containsDeleted(List<T> entities) {
        for(T entity: entities)
            if(deletionSupport.isDeleted(entity))
                return true;
        return false;
    }
    
    /**
     * Helper method that provide query condition for active recorders 
     * 
     * @param tableDef
     * @return
     */
    protected final Object[] getActiveCondition(TableDefinition tableDef) {
        return deletionSupport.getActiveCondition(tableDef);
    }

    /**
     * Notify that recorder with deleted flag detected when doing update/delete
     * 
     * @param entity
     */
    protected final void validateInput(T entity) throws SQLException {
        if(isDeleted(entity))
            throw new SQLException("The input recorder for update is marked as deleted!");
    }
    
    /**
     * Notify that recorder with deleted flag detected when doing update/delete
     *  
     * @param entities
     */
    protected final void validateInput(List<T> entities) throws SQLException {
        if(containsDeleted(entities))
            throw new SQLException("The input recorders for update is marked as deleted!");
    }
    
    protected final T processOutput(T result)  throws SQLException {
        return result == null || deletionSupport.isDeleted(result) ? null : result;        
    }
    
    /**
     * Remove recorder with deleted flag detected in query result
     * 
     * @param removed
     */
    protected final List<T> processOutput(List<T> results)  throws SQLException {
        filterDeleted(results);
        return results;
    }
    
    protected final void setActiveFlag(T entity) {
        deletionSupport.setActiveFlag(entity);
    }

    protected final void setActiveFlag(List<T> entities) {
        for(T entity: entities)
            setActiveFlag(entity);
    }

    protected final void setDeletionFlag(T entity) {
        deletionSupport.setDeletionFlag(entity);
    }

    protected final void setDeletionFlag(List<T> entities) {
        for(T entity: entities)
            setDeletionFlag(entity);
    }

    protected final void clearDeletionFlag(T entity) {
        deletionSupport.clearDeletionFlag(entity);
    }

    protected final void clearDeletionFlag(List<T> entities) {
        for(T entity: entities)
            clearDeletionFlag(entity);
    }    
}