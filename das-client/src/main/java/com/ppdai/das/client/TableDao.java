package com.ppdai.das.client;

import java.sql.SQLException;
import java.util.List;

/**
 * A handy common table DAO that user can extend from. 
 * Because it is table dao, it only contains those methods that related 
 * to single table operations. Feel free to add your own method in subclass.
 * If your application requires logic deletion, you can check out LogicDeletionDao
 * for that purpose.
 * 
 * @author hejiehui
 *
 * @param <T>
 */
public class TableDao<T> {
    protected final DasClient client;

    public TableDao(String logicDbName, Class<T> entityType) throws SQLException {
        client = DasClientFactory.getClient(logicDbName);
    }
    
    public T queryByPk(T pk, Hints...hints) throws SQLException {
        return client.queryByPk(pk, hints);
    }
    
    public List<T> queryBySample(T sample, Hints...hints) throws SQLException {
        return client.queryBySample(sample, hints);
    }
    
    public List<T> queryBySample(T sample, PageRange range, Hints...hints) throws SQLException {
        return client.queryBySample(sample, range, hints);
    }
    
    public long countBySample(T sample, Hints...hints) throws SQLException {
        return client.countBySample(sample, hints);
    }
    
    public int insert(T entity, Hints...hints) throws SQLException {
        return client.insert(entity, hints);
    }
    
    public int insert(List<T> entities, Hints...hints) throws SQLException {
        return client.insert(entities, hints);
    }
    
    public int[] batchInsert(List<T> entities, Hints...hints) throws SQLException {
        return client.batchInsert(entities, hints);
    }
    
    public int deleteByPk(T pk, Hints...hints) throws SQLException {
        return client.deleteByPk(pk, hints);
    }
    
    public int deleteBySample(T sample, Hints...hints) throws SQLException {
        return client.deleteBySample(sample, hints);
    }
    
    public int[] batchDelete(List<T> entities, Hints...hints) throws SQLException {
        return client.batchDelete(entities, hints);
    }

    public int update(T entity, Hints...hints) throws SQLException {
        return client.update(entity, hints);
    }

    public int[] batchUpdate(List<T> entities, Hints...hints) throws SQLException {
        return client.batchUpdate(entities, hints);
    }
}