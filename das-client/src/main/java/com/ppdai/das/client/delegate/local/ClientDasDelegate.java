package com.ppdai.das.client.delegate.local;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.BatchQueryBuilder;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.CallBuilder;
import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.ColumnDefinition;
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
import com.ppdai.das.core.task.SqlRequest;
import com.ppdai.das.core.task.SqlRequestExecutor;
import com.ppdai.das.core.task.SingleShardProvider;
import com.ppdai.das.core.task.SingleTask;
import com.ppdai.das.core.task.SingleTaskRequest;
import com.ppdai.das.core.task.SqlBuilderProvider;
import com.ppdai.das.core.task.SqlBuilderRequest;
import com.ppdai.das.core.task.StatementConditionProvider;
import com.ppdai.das.core.task.TaskFactory;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;

public class ClientDasDelegate implements DasDelegate {
    private String appId;
    private String logicDbName;
    private String customerClientVersion;
    private final int DEFAULT = 0;
    private final int[] BATCH_DEFAULT = new int[0];
    private SqlRequestExecutor executor = new SqlRequestExecutor();
    private TaskFactory taskFactory = DasConfigureFactory.getTaskFactory();
    
    private static final boolean NULLABLE = true;
    private static final boolean NOT_NULLABLE = false;

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

    protected <T> SqlBuilder queryByPkSqlBuilder(TableDefinition table, T id, Hints hints) throws SQLException {
        return SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, id, getParser(id))).into(id.getClass()).setHints(hints);
    }

    @Override
    public <T> T queryByPk(T id, Hints hints) throws SQLException {
        validatePk(id);

        TableDefinition table = getTableDefinition(id);
        SqlBuilder builder = queryByPkSqlBuilder(table, id, hints);

        return executeQuery(builder, NULLABLE, null);
    }

    protected <T> SqlBuilder queryBySampleSqlBuilder(TableDefinition table, T sample) throws SQLException {
        return SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, sample, getParser(sample))).into(sample.getClass());
    }

    @Override
    public <T> List<T> queryBySample(T sample, Hints hints) throws SQLException {
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder builder = queryBySampleSqlBuilder(table, sample);

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

    protected <T> SqlBuilder countBySampleSqlBuilder(TableDefinition table, T sample) throws SQLException {
        return SqlBuilder.selectCount().from(table).where(SegmentConstants.match(table, sample, getParser(sample)));
    }

    @Override
    public <T> long countBySample(T sample, Hints hints) throws SQLException {
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder builder = countBySampleSqlBuilder(table, sample);
        Object result = queryObject(builder.setHints(hints));
        return ((Number) result).longValue();

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
        TableDefinition table = getTableDefinition(sample);
        SqlBuilder deleteBuilder = SqlBuilder.deleteFrom(table).where(SegmentConstants.match(table, sample, getParser(sample)));
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
        return executeRequest(SqlBuilderProvider.update(builder), NULLABLE);
    }

    @Override
    public int[] batchUpdate(BatchUpdateBuilder builder) throws SQLException {
        return executeRequest(SingleShardProvider.create(builder), NULLABLE);
    }

    @Override
    public void call(CallBuilder builder) throws SQLException {
        executeRequest(SingleShardProvider.create(builder), NULLABLE);
    }

    @Override
    public int[] batchCall(BatchCallBuilder builder) throws SQLException {
        return executeRequest(SingleShardProvider.create(builder), NULLABLE);
    }

    @Override
    public <T> T queryObject(SqlBuilder builder) throws SQLException {
        return queryObject(builder, NOT_NULLABLE);
    }

    @Override
    public <T> T queryObjectNullable(SqlBuilder builder) throws SQLException {
        return queryObject(builder, NULLABLE);
    }

    private <T> T queryObject(SqlBuilder builder, boolean nullable) throws SQLException {
        if(builder.isSelectCount())
            return executeRequest(SqlBuilderProvider.queryObject(builder, ResultMerger.LongNumberSummary::new), nullable);
        return executeQuery(builder, nullable, null);
    }
    
    @Override
    public <T> List<T> query(SqlBuilder builder) throws SQLException {
        return executeRequest(SqlBuilderProvider.queryList(builder), NULLABLE);
    }

    @Override
    public List<?> batchQuery(BatchQueryBuilder builder) throws SQLException {
        return executeRequest(new BatchQueryBuilderProvider(builder), NULLABLE);
    }

    @Override
    public <T> T execute(CallableTransaction<T> transaction, Hints hints) throws SQLException {
        return executeRequest(SingleShardProvider.create(transaction, hints), NULLABLE);
    }

    private <T> T executeQuery(SqlBuilder builder, boolean nullable, Supplier<ResultMerger<T>> mergerFactory) throws SQLException {
        return executeRequest(SqlBuilderProvider.queryObject(builder, mergerFactory), nullable);
    }

    private <T> T executeRequest(StatementConditionProvider provider, boolean nullable) throws SQLException {
        SqlBuilderRequest<T> request = new SqlBuilderRequest<>(appId, logicDbName, provider);
        return execute(request, nullable);
    }

    private <T> int exectuteSingle(T entity, Hints hints, SingleTask<T> singleTask) throws SQLException {
        SingleTaskRequest<T> request = new SingleTaskRequest<>(appId, logicDbName, hints, entity, singleTask);
        return getSafeResult(execute(request, NOT_NULLABLE));
    }

    private <K, T> K exectuteBatch(List<T> entities, Hints hints, K defaultValue, BulkTask<K, T> bulkTask) throws SQLException {
        if(isInvalid(entities))
            return defaultValue;

        BulkTaskRequest<K, T> request = new BulkTaskRequest<>(appId, logicDbName, hints, entities, bulkTask);
        return execute(request, NOT_NULLABLE);
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
        if(sample.getClass() == Entity.class){
            EntityMeta meta =((Entity)sample).getEntityMeta();
            TableDefinition tableDefinition = new TableDefinition(meta.getTableName());
            List<ColumnDefinition> cds= new ArrayList<>();
            meta.getMetaMap().entrySet().stream().forEach(e ->{

                ColumnDefinition cd = new ColumnDefinition(tableDefinition, e.getValue().getName(), JDBCType.valueOf(e.getValue().getType()));
                cds.add(cd);
            });
            tableDefinition.setColumnDefinitions(cds.toArray(new ColumnDefinition[cds.size()]));
            return tableDefinition;
        }
        return EntityMetaManager.extract(sample.getClass()).getTableDefinition();
    }

    private <T> T execute(SqlRequest<T> request, boolean nullable) throws SQLException {
        request.getHints().getVersionInfo().setCustomerClientVersion(customerClientVersion);
        return executor.execute(request, nullable);
    }
}