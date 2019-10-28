namespace java com.ppdai.das.service

enum DasOperation {
    Insert,
    BatchInsert,
    Delete,
    DeleteByPk,
    DeleteBySample,
    BatchDelete,
    Update,
    Select,
    Call,
    BatchUpdate,
    BatchSelect,
    BatchCall,
    Query,
    QueryObject,
    BatchQuery,
    QueryByPK,
    QueryBySample,
    CountBySample,
    InsertList,
    UpdateWithSqlBuilder,
    BatchUpdateWithSqlBuilder,
    QueryBySampleWithRange
}

struct EntityList{
    1: string databaseName;
    2: string tableName;
    3: list<string> names;
    4: EntityMeta entityMeta;
    5: optional list<Entity> rows;
}

struct Entity{
   1: optional string value;
   2: optional EntityMeta entityMeta;
}

struct EntityMeta{
     1: optional string tableName;
     2: optional map<string, ColumnMeta> metaMap;
     3: optional list<string> columnNames;
     4: optional list<string> columnTypes;
     5: optional list<string> primaryKeyNames;
     6: optional bool autoIncremental;
     7: optional string versionColumn;
     8: optional list<string> updatableColumnNames;
     9: optional list<string> insertableColumnNames;
     10: optional map<string, string> fieldMap;
     11: optional string identityField;
     12: optional bool mapType;
}

struct ColumnMeta {
      1: string name;
      2: optional string type;
      3: bool autoIncremental;
      4: bool primaryKey;
      5: bool insertable;
      6: bool updatable;
      7: bool version;
}

enum DasHintEnum {
    dbShardValue,
    tableShardValue,

    dbShard,
    tableShard,

    dbShardBy,
    tableShardBy,

    dbShards,
    tableShards,

    inAllDbShards,
    inAllTableShards,

    designatedDatabase,

    isolationLevel,
    timeout,
    fetchSize,
    maxRows,

    resultSetType,
    resultSetConcurrency,

    freshness,
    masterOnly,
    slaveOnly,

    enableIdentityInsert,
    setIdentityBack,

    includeNullField
    includedColumns,
    excludedColumns,

    diagnoseMode
}

struct DasHints {
    1: required map<DasHintEnum, string> hints;
}

struct DasTransactionId {
    1: required string logicDbName;
    2: required string clientAddress;
    3: required string serverAddress;
    4: required i64 createTime;
    5: required i64 sequenceNumber;
    6: required string physicalDbName;
    7: optional string shardId;
    8: required i32 level;
    9: required bool rolledBack;
    10: required bool completed;
    
}

enum DasParameterDirection {
    input,
    output,
    inputOutput,
}

struct DasCallBuilder{
   1: string name;
   2: optional list<DasParameter> parameters;
   3: bool callByIndex;
}

struct DasBatchUpdateBuilder {
   1: optional list<string> statements;
   2: optional list<string> valuesList;
   3: optional string hints;
}

struct DasSqlBuilder {
   1: optional string partials;
   2: optional DasParameters parameters;
   3: optional list<DasParameterDefinition> definitions;
   4: optional EntityMeta entityMeta;
   5: required bool nullable;
   6: optional string entityType;
}

struct DasBatchQueryBuilder {
   1: optional list<DasSqlBuilder> sqlBuilders;
   2: optional EntityMeta entityMeta;
}

struct DasBatchCallBuilder {
   1: string name;
   2: optional list<DasParameterDefinition> parameters;
   3: optional list<list<string>> valuesList;
}

struct DasParameterDefinition {
    1: required DasParameterDirection direction;
    2: optional string name;
    3: required i32 index;
    4: required i32 jdbcType;
    5: required bool inValues;
}

struct DasParameter {
    1: required DasParameterDirection direction;
    2: optional string name;
    3: required i32 index;
    4: required i32 jdbcType;
    5: required bool inValues;
    6: optional string value;
    7: optional list<string> values;
}

struct DasParameters {
    1: required list<DasParameter> parameters;
}

struct DasDiagEntry {
    1: required string stage;
    2: required i64 cost;
    3: optional map<string, string> contexts;
}

struct DasDiagInfo {
    1: string name;
    2: i32 spaceLevel;
    3: list<DasDiagInfo> entries;
    4: map<string, string> diagnoseInfoMap;
}

struct DasRequest {
    1: required string appId;
    2: required string logicDbName;
    3: required string dasClientVersion;
    4: required string ppdaiClientVersion;
    5: required DasOperation operation,
    6: optional DasTransactionId transactionId;
    7: optional DasHints hints;
    8: optional EntityList entityList;
    9: optional DasCallBuilder callBuilder;
    10: optional DasBatchUpdateBuilder batchUpdateBuilder;
    11: optional DasBatchCallBuilder batchCallBuilder
    12: optional list<DasSqlBuilder> sqlBuilders;
    13: optional DasTraceId traceId;
    14: required i64 sendTime;
    15: optional i64 receiveTime;
}

struct DasTraceId{
    1: required map<string, string> ids;
}

struct DasResult {
    1: optional i32 rowCount;
    2: optional list<Entity> rows;
    3: optional list<i32> batchRowsIndex
    4: optional DasParameters parameters;
    5: optional DasDiagInfo diagInfo;
    6: optional EntityMeta entityMeta
}

struct DasCheckRequest {
    1: required string appId;
    2: required string dasClientVersion;
    3: required string ppdaiClientVersion;
    4: required string clientAddress;
}

struct DasServerStatus {
    1: required bool online;
    2: required i32 cpuRate;
    3: required i32 memRate;
    4: required i32 clientCount;
    5: required i64 avgResponse;
    6: required i64 avgThroughput;
    7: required i32 healthyPoint;
    8: required i32 transactionCount;
}

exception DasException {
    1: string code;
    2: string message;
}

service DasService  {

   DasResult execute(1:DasRequest request) throws (1:DasException e),

   DasTransactionId start(1:string appId, 2:string database, 3:DasHints hints) throws (1:DasException e),

   void commit(1:DasTransactionId tranId) throws (1:DasException e),

   void rollback(1:DasTransactionId tranId) throws (1:DasException e)
   
   DasServerStatus check(1:DasCheckRequest request) throws (1:DasException e),
}