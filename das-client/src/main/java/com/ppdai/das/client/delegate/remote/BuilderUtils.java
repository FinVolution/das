package com.ppdai.das.client.delegate.remote;

import com.google.common.base.Function;
import com.ppdai.das.client.*;
import com.ppdai.das.client.sqlbuilder.SqlBuilderSerializer;
import com.ppdai.das.client.sqlbuilder.Table;
import com.ppdai.das.core.enums.ParameterDirection;
import com.ppdai.das.service.*;
import com.ppdai.das.util.ConvertUtils;
import java.nio.ByteBuffer;
import java.sql.JDBCType;
import java.util.*;
import java.util.stream.Collectors;

public class BuilderUtils {

    public static List<DasSqlBuilder> buildSqlBuilders(List<SqlBuilder> sqlBuilders) {
        return toList(sqlBuilders, b -> buildSqlBuilder(b));
    }

    public static DasSqlBuilder buildSqlBuilder(SqlBuilder builder) {
        return new DasSqlBuilder()
                .setPartials(SqlBuilderSerializer.serializeSegment(builder))
                .setParameters(new DasParameters().setParameters(buildParameters(builder.buildParameters())))
                .setDefinitions(Collections.emptyList())
                .setEntityMeta(builder.getEntityMeta());
    }

    public static SqlBuilder fromSqlBuilder(DasSqlBuilder builder) {
        SqlBuilder sqlBuilder = SqlBuilderSerializer.deserializeSegment(builder.getPartials());
        return sqlBuilder
            .setBuiltParameters(fromParameters(builder.getParameters().getParameters()))
            .setBuiltDefinitions(fromDefinition(builder.getDefinitions()))
            .setEntityMeta(builder.getEntityMeta());
    }

    public static List<SqlBuilder> fromSqlBuilders(List<DasSqlBuilder> builders) {
        return toList(builders, b -> fromSqlBuilder(b));
    }

    public static DasBatchCallBuilder buildBatchCallBuilder(BatchCallBuilder builder) {
        List<List<String>> values = builder.getValuesList().stream().map(
                obs ->  Arrays.stream(obs).map(
                        obj -> SqlBuilderSerializer.serializePrimitive(obj)
                ).collect(Collectors.toList())
        ).collect(Collectors.toList());
        return new DasBatchCallBuilder()
            .setName(builder.getName())
            .setParameters(buildParameterDefinition(builder.getParameterDefinitions()))
            .setValuesList(values);
    }

    public static BatchCallBuilder fromBatchCallBuilder(DasBatchCallBuilder builder) {
        List<Object[]> values = builder.getValuesList().stream().map(
                obs ->{
                    List<Object> row = obs.stream().map(obj -> SqlBuilderSerializer.deserializePrimitive(obj)).collect(Collectors.toList());
                    return row.toArray(new Object[row.size()]);
                }
        ).collect(Collectors.toList());
        BatchCallBuilder result = new BatchCallBuilder(builder.getName());
        result.getParameterDefinitions().addAll(fromDefinition(builder.getParameters()));
        result.getValuesList().addAll(values);
        return result;
    }

    public static DasCallBuilder buildCallBuilder(CallBuilder callBuilder) {
        List<DasParameter> parameters = buildParameters(callBuilder.buildParameters());
        return new DasCallBuilder()
                .setParameters(parameters)
                .setName(callBuilder.getName())
                .setCallByIndex(callBuilder.isCallByIndex());
    }

    public static CallBuilder fromCallBuilder(DasCallBuilder callBuilder) {
        CallBuilder result = new CallBuilder(callBuilder.getName());

        result.setCallByIndex(callBuilder.isCallByIndex());
        result.getParameters().addAll(fromParameters(callBuilder.getParameters()));
        return result;
    }

    public static List<DasParameterDefinition> buildParameterDefinition(List<ParameterDefinition> definitions) {
        List<ParameterDefinition> nonNulls = definitions.stream().filter(p -> p != null).collect(Collectors.toList());
        return toList(nonNulls, p -> {
                    return new DasParameterDefinition()
                            .setName(p.getName())
                            .setJdbcType(p.getType().getVendorTypeNumber())
                            .setInValues(p.isInValues())
                            .setDirection(toDasParameterDirection(p.getDirection()));
                }
        );
    }

    public static List<ParameterDefinition> fromDefinition(List<DasParameterDefinition> definitions) {
        return toList(definitions, p -> {
                    return new ParameterDefinition(fromDasParameterDirection(p.getDirection()), p.getName(),
                            JDBCType.valueOf(p.getJdbcType()), p.isInValues());
                }
        );
    }

    public static List<DasParameter> buildParameters(List<Parameter> parameters) {
        return toList(parameters, p ->{
                    DasParameter result =  new DasParameter()
                            .setName(p.getName())
                            .setJdbcType(p.getType().getVendorTypeNumber())
                            .setInValues(p.isInValues())
                            .setDirection(toDasParameterDirection(p.getDirection()));

                    if(p.getValue() != null) {
                        result.setValue(SqlBuilderSerializer.serializePrimitive(p.getValue()));
                    }

                    if (p.getValues() != null) {
                        List<String> values = toList(p.getValues(),
                                v -> SqlBuilderSerializer.serializePrimitive(v)
                        );
                        result.setValues(values);
                    }

                    return result;
                }
        );
    }

    public static List<Parameter> fromParameters(List<DasParameter> dasParameters) {
        return toList(dasParameters, p -> {
                    Parameter result = new Parameter(fromDasParameterDirection(p.getDirection()), p.getName(), JDBCType.valueOf(p.getJdbcType()),
                            SqlBuilderSerializer.deserializePrimitive(p.getValue()));
                    result.setInValues(p.isInValues());
                    if (p.getValues() != null) {
                        List values = toList(p.getValues(), v ->
                            SqlBuilderSerializer.deserializePrimitive(v)
                        );
                        result.setValues(values);
                    }
                    return result;
                }
        );
    }

    static DasParameterDirection toDasParameterDirection(ParameterDirection direction) {
        return direction == ParameterDirection.Input ? DasParameterDirection.input
                        : (direction == ParameterDirection.Output ? DasParameterDirection.output : DasParameterDirection.inputOutput);
    }

    static ParameterDirection  fromDasParameterDirection(DasParameterDirection direction) {
        return direction == DasParameterDirection.input ? ParameterDirection.Input
                : (direction == DasParameterDirection.output ?  ParameterDirection.Output : ParameterDirection.InputOutput);
    }
    
    private static Table table(Map tableMap, String tableNameKey, String shardIdKey, String shardValueKey) {
        return new Table(tableMap.get(tableNameKey).toString()).inShard(Objects.toString(tableMap.get(shardIdKey), null)).shardBy(Objects.toString(tableMap.get(shardValueKey), null));
    }

    public static <T,K> List<K> toList(List<T> l, Function<T, K> f) {
        return l.stream().map(f).collect(Collectors.toList());
    }

    static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> map = new HashMap<>();
        for(int i = 0; i < kv.length;) {
            map.put(kv[i].toString(), kv[i + 1]);
            i+=2;
        }
        return map;
    }
}
