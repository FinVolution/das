package com.ppdai.das.client.delegate.remote;

import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Lists.newArrayList;
import static com.ppdai.das.client.delegate.remote.BuilderUtils.toList;
import static com.ppdai.das.util.ConvertUtils.entity2POJO;
import static com.ppdai.das.util.ConvertUtils.pojo2Entity;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.ppdai.das.client.*;
import com.ppdai.das.client.sqlbuilder.SqlBuilderSerializer;
import com.ppdai.das.service.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.ppdai.das.client.delegate.DasDelegate;
import com.ppdai.das.client.delegate.EntityMetaManager;
import com.ppdai.das.core.DasDiagnose;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.DasServerInstance;
import com.ppdai.das.core.LogContext;

public class DasRemoteDelegate implements DasDelegate {

    private String logicDbName;
    private String appId;
    private String customerClientVersion;

    private ServerSelector serverSelector;
    private TransactionClient transactionClient;

    private List<DasServerInstance> servers = new ArrayList<>();

    private DasLogger dalLogger;

    public DasRemoteDelegate(String appId, String logicDbName, String customerClientVersion, List<DasServerInstance> servers, DasLogger dalLogger) throws TTransportException, UnknownHostException {
        this.servers.addAll(servers);
        serverSelector = new ServerSelector(appId, this.servers,  DasClientVersion.getVersion(), customerClientVersion, InetAddress.getLocalHost().getHostAddress());
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.customerClientVersion = customerClientVersion;
        this.dalLogger = dalLogger;
        transactionClient = new TransactionClient(logicDbName, serverSelector);
    }

    // TODO should be registered into system shutdown listener/hook
    public void shutdown() {

    }

    private DasResult callRemote(DasRequest dasRequest) throws SQLException {
        LogContext logContext = dalLogger.startRemoteRequest(dasRequest);
        Throwable ex = null;
        try {
            return serverSelector.execute(dasRequest);
        } catch (TException e) {
            ex = e;
            throw new SQLException(e);
        } finally {
            dalLogger.completeRemoteRequest(logContext, ex);
        }
    }

    public static DasHints translateHints(Hints hints) {
        Map<DasHintEnum, String> map = ImmutableMap.<DasHintEnum, String>builder()
                .put(DasHintEnum.dbShard, Objects.toString(hints.getShard(), ""))
                .put(DasHintEnum.tableShard, Objects.toString(hints.getTableShard(), ""))
                .put(DasHintEnum.dbShardValue, Objects.toString(hints.getShardValue(), ""))
                .put(DasHintEnum.tableShardValue, Objects.toString(hints.getTableShardValue(), ""))
                .put(DasHintEnum.setIdentityBack, Boolean.toString(hints.isSetIdBack()))
                .put(DasHintEnum.enableIdentityInsert, Boolean.toString(hints.isInsertWithId()))
                .put(DasHintEnum.diagnoseMode, Boolean.toString(hints.isDiagnose()))
                .build();
        return new DasHints().setHints(map);
    }

    public static EntityMeta extract(Class clz) {
        com.ppdai.das.client.delegate.EntityMeta meta = EntityMetaManager.extract(clz);

        Map<String, String> fieldMap = Maps.transformEntries(meta.getFieldMap(), (key, value) -> value.getName());

        Map<String, ColumnMeta> metaMap = Maps.transformEntries(meta.getMetaMap(), (key, value) -> {
                return new ColumnMeta()
                        .setName(value.getName())
                        .setType(value.getType().getName())
                        .setAutoIncremental(value.isAutoIncremental())
                        .setPrimaryKey(value.isPrimaryKey())
                        .setInsertable(value.isInsertable())
                        .setUpdatable(value.isUpdatable())
                        .setVersion(value.isVersion());
        });

        List<String> columnTypes = Arrays.stream(meta.getColumnTypes()).map(
                t -> t.getName()
        ).collect(Collectors.toList());

        return new EntityMeta()
                .setAutoIncremental(meta.isAutoIncremental())
                .setColumnNames(newArrayList(meta.getColumnNames()))
                .setIdentityField(meta.getIdentityField().getName())
                .setInsertableColumnNames(newArrayList(meta.getInsertableColumnNames()))
                .setPrimaryKeyNames(newArrayList(meta.getPrimaryKeyNames()))
                .setTableName(meta.getTableName())
                .setVersionColumn(meta.getVersionColumn())
                .setUpdatableColumnNames(newArrayList(meta.getUpdatableColumnNames()))
                .setColumnTypes(columnTypes)
                .setFieldMap(fieldMap)
                .setMetaMap(metaMap);
    }

    private <T> DasRequest create(DasOperation operation, T pojo, Hints hints) {
        EntityMeta meta = extract(pojo.getClass());
        Entity entity = pojo2Entity(pojo, meta);

        return create(operation, hints).setEntityList(new EntityList().setEntityMeta(meta).setRows(Arrays.asList(entity)));
    }

    private <T> DasRequest create(DasOperation operation, List<T> pojos, Hints hints) {
        EntityMeta meta = extract(pojos.get(0).getClass());
        
        List<Entity> entities = new ArrayList<>(pojos.size());
        for(T pojo: pojos)
            entities.add(pojo2Entity(pojo, meta));

        return create(operation, hints).setEntityList(new EntityList().setEntityMeta(meta).setRows(entities));
    }

    private <T> List<T> call(DasOperation operation, Object obj, Hints hints, Class<T> returnClass) throws SQLException {
        DasRequest dasRequest;
        if(obj instanceof List) {
            dasRequest = create(operation, (List) obj, hints);
        } else {
            dasRequest = create(operation, obj, hints);
        }
        DasResult dasResult = callRemote(dasRequest);
        EntityMeta meta = dasRequest.getEntityList().getEntityMeta();
        DasDiagnose dasDiagnose = diagInfo2Diagnose(dasResult.getDiagInfo());
        hints.setDasDiagnose(dasDiagnose);

        List<Entity> entities = dasResult.getRows();
        if(hints.isSetIdBack()) {
            setBackId(obj, entity2POJO(entities.subList(1, entities.size()), meta,
                    obj instanceof List ? ((List)obj).get(0).getClass() : obj.getClass()));
            return (List<T>) entity2POJO(entities.subList(0, 1), meta, returnClass);
        } else {
            return (List<T>) entity2POJO(entities, meta, returnClass);
        }
    }

    private void setBackId(Object obj, List<Object> objects) {
        try {
            if(obj instanceof List) {
                List dests = ((List)obj);
                for (int i = 0; i< dests.size(); i++) {
                    BeanUtils.copyProperties(dests.get(i), objects.get(i));
                }
            }else {
                BeanUtils.copyProperties(obj, Iterables.getFirst(objects, null));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    DasDiagnose diagInfo2Diagnose(DasDiagInfo diagInfo){
        if(diagInfo == null) {
            return null;
        }
        DasDiagnose result = new DasDiagnose(diagInfo.getName(), diagInfo.getSpaceLevel());
        result.getDiagnoseInfoMap().putAll(diagInfo.getDiagnoseInfoMap());
        List<DasDiagnose> subs = diagInfo.getEntries().stream().map(d -> diagInfo2Diagnose(d)).collect(Collectors.toList());
        result.getChildDiagnoses().addAll(subs);
        return result;
    }

    private DasRequest create(DasOperation operation, Hints hints) {
        return new DasRequest()
                .setHints(translateHints(hints))
                .setLogicDbName(logicDbName)
                .setDasClientVersion(DasClientVersion.getVersion())
                .setPpdaiClientVersion(customerClientVersion)
                .setAppId(appId)
                .setOperation(operation)
                .setTransactionId(TransactionClient.getCurrentTransaction())
                .setSendTime(System.currentTimeMillis());
    }

    private DasRequest create(DasOperation operation, Hints hints, SqlBuilder builder) {
        DasRequest request = create(operation, hints);
        if(builder == null) {
            request.setSqlBuilders(Collections.emptyList());
        } else {
            request.setSqlBuilders(newArrayList(BuilderUtils.buildSqlBuilder(builder)));
        }
        return request;
    }

    private DasRequest create(DasOperation operation, Hints hints, CallBuilder callBuilder) {
        return create(operation, hints).setCallBuilder(BuilderUtils.buildCallBuilder(callBuilder));
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
        return (T) getFirst(call(DasOperation.QueryByPK, id, hints, id.getClass()), null);
    }

    @Override
    public <T> List<T> queryBySample(T sample, Hints hints) throws SQLException {
        return (List<T>) call(DasOperation.QueryBySample, sample, hints, sample.getClass());
    }
    
    @Override
    public <T> List<T> queryBySample(T sample, PageRange range, Hints hints) throws SQLException {
        //Reference: ClientDasDelegate.queryBySample
        TableDefinition table = EntityMetaManager.extract(sample.getClass()).getTableDefinition();
        SqlBuilder builder = SqlBuilder.selectAllFrom(table).where(SegmentConstants.match(table, sample)).into(sample.getClass());

        if(range.hasOrders())
            builder.orderBy(range.getOrders());

        builder.atPage(range.getPageNo(), range.getPageSize());
        builder.setHints(hints);

        //Fill EntityMeta
        EntityMeta meta = null;
        if(builder.getEntityType() == Map.class) { //Result in Map
            builder.setEntityMeta(new EntityMeta().setMapType(true));
        }else if(builder.getEntityType() != Object.class) {//Normal
            meta = extract(builder.getEntityType());
            builder.setEntityMeta(meta);
        }

        DasRequest dasRequest = create(DasOperation.QueryBySampleWithRange, builder.hints(), builder);
        DasResult dasResult = callRemote(dasRequest);
        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));

        if(builder.getEntityType() == Map.class) { //Result in Map
            //EntityMeta mapMeta = dasResult.getEntityMeta();
            return (List<T>) dasResult.getRows().stream().map(r-> new Gson().fromJson(r.getValue(), Map.class)).collect(Collectors.toList());
        } else if(builder.getEntityType() != Object.class) {//Normal POJO mode
            return  (List<T>)entity2POJO(dasResult.getRows(), meta, builder.getEntityType());
        } else { //Simple Object mode
            return  (List<T>)entity2POJO(dasResult.getRows(), null, Object.class);
        }
    }

    @Override
    public <T> long countBySample(T sample, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.CountBySample, sample, hints, long.class), 0L);
    }

    @Override
    public <T> int insert(T entity, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.Insert, entity, hints, int.class), 0);
    }

    @Override
    public <T> int insert(List<T> entities, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.InsertList, entities, hints, int.class), 0);
    }

    @Override
    public <T> int deleteByPk(T pk, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.DeleteByPk, pk, hints, int.class), 0);
    }

    @Override
    public <T> int deleteBySample(T sample, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.DeleteBySample, sample, hints, int.class), 0);
    }

    @Override
    public <T> int update(T entity, Hints hints) throws SQLException {
        return getFirst(call(DasOperation.Update, entity, hints, int.class), 0);
    }

    @Override
    public <T> int[] batchInsert(List<T> entities, Hints hints) throws SQLException {
        return Ints.toArray(call(DasOperation.BatchInsert, entities, hints, int.class));
    }

    @Override
    public <T> int[] batchDelete(List<T> entities, Hints hints) throws SQLException {
        return Ints.toArray(call(DasOperation.BatchDelete, entities, hints, int.class));
    }

    @Override
    public <T> int[] batchUpdate(List<T> entities, Hints hints) throws SQLException {
        return Ints.toArray(call(DasOperation.BatchUpdate, entities, hints, int.class));
    }

    @Override
    public int update(SqlBuilder builder) throws SQLException {
        DasRequest dasRequest = create(DasOperation.UpdateWithSqlBuilder, builder.hints(), builder);
        DasResult dasResult = callRemote(dasRequest);
        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));
        return (int)getFirst(entity2POJO(dasResult.getRows(), null, int.class), 0);
    }

    @Override
    public int[] batchUpdate(BatchUpdateBuilder batchUpdateBuilder) throws SQLException {
        DasRequest dasRequest = create(DasOperation.BatchUpdateWithSqlBuilder, batchUpdateBuilder.hints(), batchUpdateBuilder.getBuilder());

        //Set ParameterDefinition only for batchUpdate case
        if(batchUpdateBuilder.getBuilder() != null){//TODO
            List<ParameterDefinition> pds = batchUpdateBuilder.getBuilder().buildDefinitions();
            Iterables.getOnlyElement(dasRequest.getSqlBuilders()).setDefinitions(BuilderUtils.buildParameterDefinition(pds));
        }

        List<String> bs = toList(batchUpdateBuilder.getValuesList(), v -> new Gson().toJson(v));
        List<String> statements = batchUpdateBuilder.getStatements() == null ? Collections.emptyList() : newArrayList(batchUpdateBuilder.getStatements());
        dasRequest.setBatchUpdateBuilder(
                new DasBatchUpdateBuilder()
                        .setStatements(statements)
                        .setValuesList(bs));

        DasResult dasResult = callRemote(dasRequest);
        batchUpdateBuilder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));
        return Ints.toArray(BuilderUtils.toList(dasResult.getRows(), r-> new Gson().fromJson(r.getValue(), Integer.class)));
    }

    @Override
    public void call(CallBuilder builder) throws SQLException {
        DasRequest dasRequest = create(DasOperation.Call, builder.hints()).setCallBuilder(BuilderUtils.buildCallBuilder(builder));
        DasResult dasResult = callRemote(dasRequest);

        //Set back parameters
        List<Parameter> returnedParams = BuilderUtils.fromParameters(dasResult.getParameters().getParameters());
        List<Parameter> params = builder.getParameters();
        params.clear();
        params.addAll(returnedParams);

        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));
    }

    @Override
    public int[] batchCall(BatchCallBuilder builder) throws SQLException {
        DasRequest dasRequest = create(DasOperation.BatchCall, builder.hints())
                .setBatchCallBuilder(BuilderUtils.buildBatchCallBuilder(builder));
        DasResult dasResult = callRemote(dasRequest);

        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));
        return null;
       // return Ints.toArray((List<Number>) entity2POJO(dasResult.getRows(), null, int.class));
    }

    @Override
    public <T> T queryObject(SqlBuilder builder) throws SQLException {
        return doQueryObject(builder, false);
    }

    @Override
    public <T> T queryObjectNullable(SqlBuilder builder) throws SQLException {
        return doQueryObject(builder, true);
    }

    private <T> T doQueryObject(SqlBuilder builder, boolean isNullable) throws SQLException {
        EntityMeta meta = null;
        if(!isSimpleType(builder.getEntityType())) {//Normal
            meta = extract(builder.getEntityType());
            builder.setEntityMeta(meta);
        }

        DasRequest dasRequest = create(DasOperation.QueryObject, builder.hints(), builder);
        dasRequest.getSqlBuilders().get(0).setNullable(isNullable);
        DasResult dasResult = callRemote(dasRequest);
        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));

        if(!isSimpleType(builder.getEntityType())) {//Normal POJO mode
            return (T) getFirst(entity2POJO(dasResult.getRows(), meta, builder.getEntityType()), null);
        } else { //Simple Object mode
            return (T)SqlBuilderSerializer.deserializePrimitive(dasResult.getRows().get(0).getValue());
        }
    }


    private boolean isSimpleType(Class clz){
        return clz == Object.class || clz == String.class || clz == Long.class
                || clz == Integer.class;
    }

    @Override
    public <T> List<T> query(SqlBuilder builder) throws SQLException {
        //Fill EntityMeta
        EntityMeta meta = null;
        if(builder.getEntityType() == Map.class) { //Result in Map
            builder.setEntityMeta(new EntityMeta().setMapType(true));
        }else if(builder.getEntityType() != Object.class) {//Normal
            meta = extract(builder.getEntityType());
            builder.setEntityMeta(meta);
        }

        DasRequest dasRequest = create(DasOperation.Query, builder.hints(), builder);
        DasResult dasResult = callRemote(dasRequest);
        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));

        if(builder.getEntityType() == Map.class) { //Result in Map
           // EntityMeta mapMeta = dasResult.getEntityMeta();
            return (List<T>) dasResult.getRows().stream().map(r-> new Gson().fromJson(r.getValue(), Map.class)).collect(Collectors.toList());
    } else if(builder.getEntityType() != Object.class) {//Normal POJO mode
            return  (List<T>)entity2POJO(dasResult.getRows(), meta, builder.getEntityType());
        } else { //Simple Object mode

            return  (List<T>)  dasResult.getRows().stream().map(v->{
                return SqlBuilderSerializer.deserializePrimitive(v.getValue());
            }).collect(Collectors.toList());
            //return  (List<T>) entity2POJO(dasResult.getRows(), null, Object.class);
        }
    }

    @Override
    public List<?> batchQuery(BatchQueryBuilder builder) throws SQLException {
        //Fill EntityMeta
        SqlBuilder firstBuilder = builder.getQueries().get(0);
        EntityMeta meta = null;
        if(firstBuilder.getEntityType() != Object.class) {//Normal
            meta = extract(firstBuilder.getEntityType());
            for(SqlBuilder sqlBuilder : builder.getQueries()){
                sqlBuilder.setEntityMeta(meta);
            }
        }

        DasRequest dasRequest = create(DasOperation.BatchQuery, builder.hints())
                .setSqlBuilders(BuilderUtils.buildSqlBuilders(builder.getQueries()));
        DasResult dasResult = callRemote(dasRequest);

        builder.hints().setDasDiagnose(diagInfo2Diagnose(dasResult.getDiagInfo()));
        List flatList;
        if(firstBuilder.getEntityType() != Object.class) {//Normal POJO mode
            flatList = entity2POJO(dasResult.getRows(), meta, firstBuilder.getEntityType());
        } else {
            flatList = entity2POJO(dasResult.getRows(), null, Object.class);
        }
        return toListOfList(flatList, dasResult.getBatchRowsIndex());
    }

    private List<List> toListOfList(List flatList, List<Integer> index) {
        List<List> result = new ArrayList<>(index.size());

        int total = 0;
        for(Integer i: index) {
            result.add(flatList.subList(total, total + i));
            total += i;
        }
        return result;
    }

    @Override
    public <T> T execute(CallableTransaction<T> transaction, Hints hints) throws SQLException {
        serverSelector.stickServerMode();
        return transactionClient.doInTransaction(transaction, hints);
    }
}
