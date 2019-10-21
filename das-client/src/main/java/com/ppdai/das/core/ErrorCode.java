package com.ppdai.das.core;

public enum ErrorCode {
    /**
     * It is expected to return only %s result. But the actually count is %s
     */
    AssertEqual(ErrorCategory.Assert, 5000, "It is expected to return only %s result. But the actually count is %s"),

    /**
     * There is no result found!
     */
    AssertGreatThan(ErrorCategory.Assert, 5001, "There is no result found!"),

    /**
     * It is expected to return only 1 or no result. But the actually count is more than 1.
     */
    AssertSingle(ErrorCategory.Assert, 5002, "It is expected to return only 1 result. But the actually count is more than 1"),

    /**
     * It is expected to return 1 result. But found none
     */
    AssertNull(ErrorCategory.Assert, 5003, "It is expected to return 1 result. But found none"),

    /**
     * The requested operation is not supported.
     */
    NotSupported(ErrorCategory.Assert, 5004, "The requested operation is not supported"),

    /**
     * The requested operation is not supported.
     */
    MoreThanOneVersionColumn(ErrorCategory.Assert, 5005, "The entity contains more than one version annotation"),

    /**
     * The primary key of this table is consists of more than one column
     */
    ValidatePrimaryKeyCount(ErrorCategory.Validate, 5100, "The primary key of this table is consists of more than one column"),

    /**
     * There is no column to be updated. Please check if needed fields have been set in pojo
     */
    ValidateFieldCount(ErrorCategory.Validate, 5101, "There is no column to be updated. Please check if needed fields have been set in pojo"),

    /**
     * Non or More than one generated keys are returned: %s
     */
    ValidateKeyHolderSize(ErrorCategory.Validate, 5102, "Non or More than one generated keys are returned: %s"),

    /**
     * Non or More than one entries found for the generated key: %s
     */
    ValidateKeyHolderFetchSize(ErrorCategory.Validate, 5103, "Non or More than one entries found for the generated key: %s"),

    /**
     * Can not convert generated key to number
     */
    ValidateKeyHolderConvert(ErrorCategory.Validate, 5104, "Can not convert generated key to number"),

    /**
     * The insertion is fail or not completed yet.
     */
    KeyGenerationFailOrNotCompleted(ErrorCategory.Validate, 5105, "The insertion is fail or not completed yet."),

    /**
     * There is no field defined in pojo
     */
    FieldNotExists(ErrorCategory.Validate, 5106, "There is no field defined in pojo %s for column %s. Please check with HintEnum.ignoreMissingFields"),

    /**
     * Can not put generated primary key back to pojo
     */
    SetPrimaryKeyFailed(ErrorCategory.Validate, 5107, "Can not put generated primary key back to pojo %s for column %s"),

    /**
     * Sql cannot be null
     */
    ValidateSql(ErrorCategory.Validate, 5200, "The given sql is null"),

    /**
     * Pojos cannot be null
     */
    ValidatePojoList(ErrorCategory.Validate, 5201, "The given pojo list is null"),

    /**
     * Pojos cannot be null
     */
    ValidatePojo(ErrorCategory.Validate, 5202, "The given pojo is null"),

    /**
     * Task cannot be null
     */
    ValidateTask(ErrorCategory.Validate, 5203, "The given dao task is null. Means the calling DAO method is not supported. Please contact your DAS team."),

    /**
     * Version column is null
     */
    ValidateVersion(ErrorCategory.Validate, 5204, "Version column can not be null"),

    /**
     * Column type is not defined
     */
    TypeNotDefined(ErrorCategory.Validate, 5206, "Column type is not defined"),

    /**
     * Duplicated column name is found
     */
    DuplicateColumnName(ErrorCategory.Validate, 5207, "Column name is already used by other field"),

    /**
     * No Database annotation found.
     */
    NoDatabaseDefined(ErrorCategory.Validate, 5208, "The entity must configure Database annotation."),

    /**
     * No Database annotation found.
     */
    AllFieldsOfPojoAreNull(ErrorCategory.Validate, 5209, "All fields value of pojo are null."),

    /**
     * Can not locate shard for %s
     */
    ShardLocated(ErrorCategory.Shard, 5900, "Can not locate shard for %s"),

    /**
     * No shard defined for id:
     */
    NoShardId(ErrorCategory.Shard, 5901, "No shard defined for id: %s"),

    /**
     * No sharding stradegy defined
     */
    NoShardStradegy(ErrorCategory.Shard, 5902, "No sharding stradegy defined"),

    /**
     * The current transaction is already rolled back or completed
     */
    TransactionState(ErrorCategory.Transaction, 5600, "The current transaction is already rolled back or completed"),

    /**
     * Transaction level mismatch. Expected: %d Actual: %d
     */
    TransactionLevelMatch(ErrorCategory.Transaction, 5601, "Transaction level mismatch. Expected: %d Actual: %d"),

    /**
     * DAS do not support distributed transaction. Current DB: %s, DB requested: %s
     */
    TransactionDistributed(ErrorCategory.Transaction, 5602, "DAS do not support distributed transaction. Current DB: %s, DB requested: %s"),

    /**
     * Calling endTransaction with empty ConnectionCache
     */
    TransactionEnd(ErrorCategory.Transaction, 5603, "Calling endTransaction with empty ConnectionCache"),

    /**
     * Calling endTransaction with empty ConnectionCache
     */
    TransactionNoFound(ErrorCategory.Transaction, 5604, "There is no transaction found"),

    /**
     * DAS do not support distributed transaction in same db but different shard
     */
    TransactionDistributedShard(ErrorCategory.Transaction, 5605, "DAS do not support distributed transaction in same DB but different shard. Current shard: %s, requested in hints: %s"),

    /**
     * The result mapping is faild.
     */
    ResultMappingError(ErrorCategory.Extract, 5700, "Can not extract from result set. If the columns in result set does not match with columns in pojo, please check with HintEnum.allowPartial or partialQuery."),

    /**
     * Can not get connection from DB %s
     */
    CantGetConnection(ErrorCategory.Connection, 5300, "Can not get connection from DB %s"),

    MarkdownConnection(ErrorCategory.Connection, 5301, "The DB or allinonekey [%s] has bean marked down"),

    NullLogicDbName(ErrorCategory.Connection, 5302, "The master/slave database set is empty"),

    NoMoreConnectionToFailOver(ErrorCategory.Connection, 5303, "There is no more fail over connections to try"),

    MarkdownLogicDb(ErrorCategory.Connection, 5304, "Database Set %s has been marked down"),

    /**
     * Logic Db Name is empty!
     */
    LogicDbEmpty(ErrorCategory.Connection, 5305, "Logic Db Name is empty!"),

    InvalidDatabaseKeyName(ErrorCategory.Connection, 5306, "The given database key name is not qualified: %s"),

    Unknown(ErrorCategory.Unknown, 9999, "Unknown Exception, caused by: %s");

    private final ErrorCategory classify;
    private final int code;
    private final String msg;

    ErrorCode(ErrorCategory classify, int code, String msg) {
        this.classify = classify;
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.msg;
    }

    public ErrorCategory getErrorClassify() {
        return this.classify;
    }
}
