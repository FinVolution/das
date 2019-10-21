package com.ppdai.das.client.delegate.local;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.BatchQueryBuilder;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.CallBuilder;
import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.PageRange;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.client.delegate.DasDelegate;
import com.ppdai.das.client.delegate.EntityMetaManager;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.client.DalParser;
import com.ppdai.das.core.task.BatchQueryBuilderProvider;
import com.ppdai.das.core.task.BulkTask;
import com.ppdai.das.core.task.BulkTaskRequest;
import com.ppdai.das.core.task.DalRequest;
import com.ppdai.das.core.task.DalRequestExecutor;
import com.ppdai.das.core.task.TaskFactory;
import com.ppdai.das.core.task.SingleShardProvider;
import com.ppdai.das.core.task.SingleTask;
import com.ppdai.das.core.task.SingleTaskRequest;
import com.ppdai.das.core.task.SqlBuilderProvider;
import com.ppdai.das.core.task.SqlBuilderRequest;
import com.ppdai.das.core.task.StatementConditionProvider;

public class ClientDasDelegate implements DasDelegate {
    private String appId;
    private String logicDbName;
    private String customerClientVersion;
    private final int DEFAULT = 0;
    private final int[] BATCH_DEFAULT = new int[0];
    private DalRequestExecutor executor = new DalRequestExecutor();
    private TaskFactory taskFactory = DasConfigureFactory.getTaskFactory();


    public ClientDasDelegate(String appId, String logicDbName, String customerClientVersion) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.customerClientVersion  =customerClientVersion;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public String getLogicDbName() {
        return logicDbName;
    }

    @Override
    public <T> T queryByPk(T id, Hints hints) throws SQLException {
        validatePk(id);

        TableDefinition table = getTableDefinition(id);
        SqlBuilder builder = SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, id)).into(id.getClass()).setHints(hints);

        return executeQuery(builder, true, null);
    }

    @Override
    public <T> List<T> queryBySample(T sample, Hints hints) throws SQLException {
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder builder = SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, sample)).into(sample.getClass());

        return query(builder.setHints(hints));
    }

    public <T> List<T> queryBySample(T sample, PageRange range, Hints hints) throws SQLException {
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder builder = SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, sample)).into(sample.getClass());

        if(range.hasOrders())
            builder.orderBy(range.getOrders());

        builder.atPage(range.getPageNo(), range.getPageSize());

        return query(builder.setHints(hints));
    }

    @Override
    public <T> long countBySample(T sample, Hints hints) throws SQLException {
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder builder = SqlBuilder.selectCount().from(table).where(SegmentConstants.match(table, sample));
        return ((Number)queryObject(builder.setHints(hints))).longValue();
    }

    @Override
    public <T> int insert(T entity, Hints hints) throws SQLException {
        return exectuteSingle(entity, hints, taskFactory.createSingleInsertTask(getParser(entity)));
    }

    @Override
    public <T> int insert(List<T> entities, Hints hints) throws SQLException {
        return getSafeResult(exectuteBatch(entities, hints, DEFAULT, taskFactory.createCombinedInsertTask(getParser(entities))));
    }

    @Override
    public <T> int deleteByPk(T pk, Hints hints) throws SQLException {
        return exectuteSingle(pk, hints, taskFactory.createSingleDeleteTask(getParser(pk)));
    }

    @Override
    public <T> int deleteBySample(T sample, Hints hints) throws SQLException {
        TableDefinition table = EntityMetaManager.extract(sample.getClass()).getTableDefinition();
        SqlBuilder deleteBuilder = SqlBuilder.deleteFrom(table).where(SegmentConstants.match(table, sample));
        return update(deleteBuilder.setHints(hints));
    }

    @Override
    public <T> int update(T entity, Hints hints) throws SQLException {
        return exectuteSingle(entity, hints, taskFactory.createSingleUpdateTask(getParser(entity)));
    }

    @Override
    public <T> int[] batchInsert(List<T> entities, Hints hints) throws SQLException {
        return exectuteBatch(entities, hints, BATCH_DEFAULT, taskFactory.createBatchInsertTask(getParser(entities)));
    }

    @Override
    public <T> int[] batchDelete(List<T> entities, Hints hints) throws SQLException {
        return exectuteBatch(entities, hints, BATCH_DEFAULT, taskFactory.createBatchDeleteTask(getParser(entities)));
    }

    @Override
    public <T> int[] batchUpdate(List<T> entities, Hints hints) throws SQLException {
        return exectuteBatch(entities, hints, BATCH_DEFAULT, taskFactory.createBatchUpdateTask(getParser(entities)));
    }

    @Override
    public int update(SqlBuilder builder) throws SQLException {
        return executeRequest(SqlBuilderProvider.update(builder), true);
    }

    @Override
    public int[] batchUpdate(BatchUpdateBuilder builder) throws SQLException {
        return executeRequest(SingleShardProvider.create(builder), true);
    }

    @Override
    public void call(CallBuilder builder) throws SQLException {
        executeRequest(SingleShardProvider.create(builder), true);
    }

    @Override
    public int[] batchCall(BatchCallBuilder builder) throws SQLException {
        return executeRequest(SingleShardProvider.create(builder), true);
    }

    @Override
    public <T> T queryObject(SqlBuilder builder) throws SQLException {
        if(builder.isSelectCount())
            return executeRequest(SqlBuilderProvider.queryObject(builder, ResultMerger.LongNumberSummary::new), false);
        return executeQuery(builder, false, null);
    }

    @Override
    public <T> T queryObjectNullable(SqlBuilder builder) throws SQLException {
        if(builder.isSelectCount())
            return executeRequest(SqlBuilderProvider.queryObject(builder, ResultMerger.LongNumberSummary::new), true);
        return executeQuery(builder, true, null);
    }

    @Override
    public <T> List<T> query(SqlBuilder builder) throws SQLException {
        return executeRequest(SqlBuilderProvider.queryList(builder), true);
    }

    @Override
    public List<?> batchQuery(BatchQueryBuilder builder) throws SQLException {
        return executeRequest(new BatchQueryBuilderProvider(builder), true);
    }

    @Override
    public <T> T execute(CallableTransaction<T> transaction, Hints hints) throws SQLException {
        return executeRequest(SingleShardProvider.create(transaction, hints), true);
    }

    private <T> T executeQuery(SqlBuilder builder, boolean nullable, Supplier<ResultMerger<T>> mergerFactory) throws SQLException {
        return executeRequest(SqlBuilderProvider.queryObject(builder, mergerFactory), nullable);
    }

    private <T> T executeRequest(StatementConditionProvider provider, boolean nullable) throws SQLException {
        SqlBuilderRequest<T> request = new SqlBuilderRequest<>(appId, logicDbName, provider);
        Hints dalHints = getHints(request);
        return executor.execute(dalHints, request, nullable);
    }

    private <T> int exectuteSingle(T entity, Hints hints, SingleTask<T> singleTask) throws SQLException {
        SingleTaskRequest<T> request = new SingleTaskRequest<>(appId, logicDbName, hints, entity, singleTask);
        Hints dalHints = getHints(request);
        return getSafeResult(executor.execute(dalHints, request));
    }

    private <K, T> K exectuteBatch(List<T> entities, Hints hints, K defaultValue, BulkTask<K, T> bulkTask) throws SQLException {
        if(isInvalid(entities))
            return defaultValue;

        BulkTaskRequest<K, T> request = new BulkTaskRequest<>(appId, logicDbName, hints, entities, bulkTask);
        Hints dalHints = getHints(request);
        return executor.execute(dalHints, request);
    }
    
    private <T> boolean isInvalid(List<T> entities) {
        Objects.requireNonNull(entities);
        
        return entities.size() == 0;
    }

    protected <T> DalParser<T> getParser(T entity) throws SQLException {
        return new PPDaiDalParser(appId, logicDbName, entity.getClass());
    }

    private <T> DalParser<T> getParser(List<T> entities) throws SQLException {
        return getParser(entities.get(0));
    }

    private int getSafeResult(Integer value) {
        if (value == null)
            return 0;
        return value;
    }

    private <T> void validatePk(T pk) throws SQLException {
        Map<String, ?> pkFields = getParser(pk).getPrimaryKeys(pk);
        
        for(Map.Entry<String, ?> keyEntry: pkFields.entrySet())
            if(keyEntry.getValue() == null)
                throw new IllegalArgumentException(String.format("Primary key field %s is null", keyEntry.getKey()));
    }
    
    private <T> TableDefinition getTableDefinition(T sample) {
        return EntityMetaManager.extract(sample.getClass()).getTableDefinition();
    }
    
    private <T> Hints getHints(DalRequest<T> request) {
        Hints hints = request.getHints();
        hints.getVersionInfo().setCustomerClientVersion(customerClientVersion);
        return hints;
    }

    private Hints translateHints(Hints hints) {
        hints.getVersionInfo().setCustomerClientVersion(customerClientVersion);
        return hints;
    }
}