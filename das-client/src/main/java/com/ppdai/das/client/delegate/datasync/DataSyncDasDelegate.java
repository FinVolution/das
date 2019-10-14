package com.ppdai.das.client.delegate.datasync;

import com.ppdai.das.client.*;
import com.ppdai.das.client.delegate.DasDelegate;

import com.ppdai.das.service.DasOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import static com.ppdai.das.service.DasOperation.*;

public class DataSyncDasDelegate implements DasDelegate {

    private final static Logger logger = LoggerFactory.getLogger(DataSyncDasDelegate.class.getName());

    private DasDelegate delegate;

    public DataSyncDasDelegate(DasDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getAppId() {
        return delegate.getAppId();
    }

    @Override
    public String getLogicDbName() {
        return delegate.getLogicDbName();
    }

    private <T> T syncTemplate(Callable callable, Object entity, Hints hints, DasOperation dasOperation) {
        Object result = null;
        Exception exception = null;
        try {
            result = callable.call();
            return result == null ? null :(T) result;
        } catch (Exception e) {
            exception = e;
            throw new RuntimeException(e);
        } finally {
            DataSyncConfiguration dataSyncConfiguration = DataSyncConfiguration.getInstance();
            if(dataSyncConfiguration.isEnableSyncMode()) {
                String logicBDName = getLogicDbName();
                if(dataSyncConfiguration.getDasDataSynchronizer(logicBDName) != null){
                    DataSyncContext syncContext = null;
                    try {
                        syncContext = createDataSyncContext(entity, hints, logicBDName, result, exception, dasOperation);
                        dataSyncConfiguration.sendContext(syncContext);
                    } catch (Exception e){
                        logger.error("Exception occurs when sending sync context: " + syncContext, e);
                    }
                }
            }
        }
    }

    private DataSyncContext createDataSyncContext(Object entity, Hints hints, String logicDBName,
                                                  Object result, Exception exception, DasOperation dasOperation) {
        return new DataSyncContext(logicDBName)
                .setData(entity)
                .setHints(hints)
                .setException(exception)
                .setLogicDbName(logicDBName)
                .setResult(result)
                .setDasOperation(dasOperation);
    }

    @Override
    public <T> T queryByPk(T id, Hints hints) throws SQLException {
        return delegate.queryByPk(id, hints);
    }

    @Override
    public <T> List<T> queryBySample(T sample, Hints hints) throws SQLException {
        return delegate.queryBySample(sample, hints);
    }

    @Override
    public <T> List<T> queryBySample(T sample, PageRange range, Hints hints) throws SQLException {
        return delegate.queryBySample(sample, range, hints);
    }

    @Override
    public <T> long countBySample(T sample, Hints hints) throws SQLException {
        return delegate.countBySample(sample, hints);
    }

    @Override
    public <T> int insert(T entity, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.insert(entity, hints), entity, hints, Insert);
    }


    @Override
    public <T> int insert(List<T> entities, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.insert(entities, hints), entities, hints, InsertList);
    }

    @Override
    public <T> int deleteByPk(T pk, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.deleteByPk(pk, hints), pk, hints, DeleteByPk);
    }

    @Override
    public <T> int deleteBySample(T sample, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.deleteBySample(sample, hints), sample, hints, DeleteBySample);
    }

    @Override
    public <T> int update(T entity, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.update(entity, hints), entity, hints, Update);
    }

    @Override
    public <T> int[] batchInsert(List<T> entities, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.batchInsert(entities, hints), entities, hints, BatchInsert);
    }

    @Override
    public <T> int[] batchDelete(List<T> entities, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.batchDelete(entities, hints), entities, hints, BatchDelete);
    }

    @Override
    public <T> int[] batchUpdate(List<T> entities, Hints hints) throws SQLException {
        return syncTemplate(() -> delegate.batchUpdate(entities, hints), entities, hints, BatchUpdate);
    }

    @Override
    public int update(SqlBuilder builder) throws SQLException {
        return delegate.update(builder);
    }

    @Override
    public int[] batchUpdate(BatchUpdateBuilder builder) throws SQLException {
        return delegate.batchUpdate(builder);
    }

    @Override
    public void call(CallBuilder builder) throws SQLException {
        delegate.call(builder);
    }

    @Override
    public int[] batchCall(BatchCallBuilder builder) throws SQLException {
        return delegate.batchCall(builder);
    }

    @Override
    public <T> T queryObject(SqlBuilder builder) throws SQLException {
        return delegate.queryObject(builder);
    }

    @Override
    public <T> T queryObjectNullable(SqlBuilder builder) throws SQLException {
        return delegate.queryObjectNullable(builder);
    }

    @Override
    public <T> List<T> query(SqlBuilder builder) throws SQLException {
        return delegate.query(builder);
    }

    @Override
    public List<?> batchQuery(BatchQueryBuilder builder) throws SQLException {
        return delegate.batchQuery(builder);
    }

    @Override
    public <T> T execute(CallableTransaction<T> transaction, Hints hints) throws SQLException {
        return delegate.execute(transaction, hints);
    }


}
