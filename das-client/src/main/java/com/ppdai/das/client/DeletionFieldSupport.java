package com.ppdai.das.client;

import java.lang.reflect.Field;

import com.ppdai.das.client.delegate.EntityMetaManager;

public class DeletionFieldSupport<T> implements LogicDeletionSupport<T>{
    private final String deletionColumnName;
    private final Object deleted;
    private final Object active;
    private final Field deletionField;
     
    public DeletionFieldSupport(Class<T> entityType, ColumnDefinition deletionColumn, Object deleted, Object active) {
        this.deletionColumnName = deletionColumn.getColumnName();
        this.deleted = deleted;
        this.active = active;
        this.deletionField = getDeletionField(entityType);
    }
    
    private Field getDeletionField(Class<T> entityType) {
        return EntityMetaManager.extract(entityType).getFieldMap().get(deletionColumnName);
    }

    @Override
    public boolean isDeleted(T entity) {
        Object deletionFlag;
        try {
            deletionFlag = deletionField.get(entity);
        } catch (Throwable e) {
            throw new IllegalStateException("Can not evaluate deletion flag for given entity", e);
            
        }
        return deletionFlag != null && deletionFlag.equals(deleted);
    }

    @Override
    public void clearDeletionFlag(T entity) {
        setFlag(entity, null);
    }
    
    @Override
    public void setActiveFlag(T entity) {
        setFlag(entity, active);
    }

    @Override
    public void setDeletionFlag(T entity) {
        setFlag(entity, deleted);
    }

    @Override
    public Object[] getActiveCondition(TableDefinition tableDef) {
        return new Object[] {tableDef.getColumnDefinition(deletionColumnName).eq(active)};
    }

    @Override
    public Object[] getDeletionCondition(TableDefinition tableDef) {
        return new Object[] {tableDef.getColumnDefinition(deletionColumnName).eq(deleted)};
    }

    @Override
    public Object[] setDeletionFlag(TableDefinition tableDef) {
        return new Object[] {tableDef.getColumnDefinition(deletionColumnName).eq(deleted)};
    }

    private void setFlag(T entity, Object value) {
        try {
            deletionField.set(entity, value);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new IllegalStateException("Can not set flag for entity " + entity.getClass(), e);
        }
    }
}
