package com.ppdai.das.client;

import com.google.common.base.Preconditions;
import com.ppdai.das.core.DalHintEnum;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasCoreVersion;
import com.ppdai.das.core.DasDiagnose;
import com.ppdai.das.core.DasVersionInfo;
import com.ppdai.das.core.KeyHolder;
import com.ppdai.das.core.client.DalHA;
import com.ppdai.das.core.exceptions.DalException;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Additional parameters for operation.
 *
 * IMPORTANT NOTE:
 * Hints is not thread safe, you should never share it among threads.
 * Hints is not designed for reuse, you should never create a Hints instance and use it in multiple operations
 *
 * Since das-client 2.2.2, DalHints is moved here and deprecated.
 * 
 * @author hejiehui
 *
 */
public class Hints {

    private boolean diagnoseMode;
    private DasDiagnose dasDiagnose;

    /**
     * Following fields are moved from DalHints
     */
    private Map<DalHintEnum, Object> hints = new ConcurrentHashMap<>();
    // It is not so nice to put keyholder here, but to make Task stateless, I have no other choice
    private KeyHolder keyHolder;
    private DasVersionInfo versionInfo;

    private static final Object NULL = new Object();

    /**
     * Creates a {@code Hints} instance.
     *
     * @return {@code Hints} instance
     */
    public static Hints hints() {
        return new Hints();
    }

    /**
     * Enable diagnose mode.
     *
     * @see DasDiagnose
     * @return this {@code Hints}
     */
    public Hints diagnose() {
        diagnoseMode = true;
        try {
            dasDiagnose = new DasDiagnose("diagnose", 1);
            set(DalHintEnum.userDefined1, dasDiagnose);
        } catch (Exception ignored) {
        }
        return this;
    }

    /**
     * Returns {@code DasDiagnose} instance.
     *
     * @return {@code DasDiagnose} instance
     */
    public DasDiagnose getDiagnose() {
        return dasDiagnose;
    }

    /**
     * Set the generated ID back into the entity.
     *
     * @return this {@code Hints}
     */
    public Hints setIdBack() {
        set(DalHintEnum.setIdentityBack);
        prepareInsert();
        return this;
    }

    /**
     * Insert recorder with the given ID in entity.
     *
     * @return this {@code Hints}
     */
    public Hints insertWithId() {
        return set(DalHintEnum.enableIdentityInsert);
    }

//    public Hints inShards(String... shardIds) {
//        //TODO finish
//        return this;
//    }

    /**
     * Set in which shard the operation will be performed.
     *
     * @param shardId
     * @return this {@code Hints}
     * @see DalHintEnum#shard
     */
    public Hints inShard(String shardId) {
        hints.put(DalHintEnum.shard, shardId);
        return this;
    }

    /**
     * Set in which shard the operation will be performed.
     *
     * @param shardId
     * @return this {@code Hints}
     * @see DalHintEnum#shard
     */
    public Hints inShard(Integer shardId) {
        hints.put(DalHintEnum.shard, shardId);
        return this;
    }

    /**
     * Set which table shard the operation will be performed.
     *
     * @param tableShardId
     * @return this {@code Hints}
     * @see DalHintEnum#tableShard
     */
    public Hints inTableShard(String tableShardId) {
        hints.put(DalHintEnum.tableShard, tableShardId);
        return this;
    }

    /**
     * Set which table shard the operation will be performed.
     *
     * @param tableShardId
     * @return this {@code Hints}
     * @see DalHintEnum#tableShard
     */
    public Hints inTableShard(Integer tableShardId) {
        hints.put(DalHintEnum.tableShard, tableShardId);
        return this;
    }
    
    /**
     * Set value used to help sharding strategy locate DB shard.
     *
     * @param value
     * @return this {@code Hints}
     * @see DalHintEnum#shardValue
     */
    public Hints shardValue(Object shardValue) {
        return set(DalHintEnum.shardValue, shardValue);
    }

    /**
     * Set value used to help sharding strategy locate table shard
     *
     * @param tableShardValue
     * @return this {@code Hints}
     * @see DalHintEnum#tableShardValue
     */
    public Hints tableShardValue(Object tableShardValue) {
        this.setTableShardValue(tableShardValue);
        return this;
    }

    /**
     * Set fetch size of statement.
     *
     * @param fetchSize
     * @return this {@code Hints}
     * @see DalHintEnum#fetchSize
     */
    public Hints fetchSize(Integer fetchSize) {
        Preconditions.checkArgument(fetchSize > 0, "Please input valid fetchSize > 0, fetchSize: " + fetchSize);
        set(DalHintEnum.fetchSize, fetchSize);
        return this;
    }
    /**
     * Set max row of statement.
     *
     * @param maxRows
     * @return this {@code Hints}
     * @see DalHintEnum#maxRows
     */
    public Hints maxRows(Integer maxRows) {
        Preconditions.checkArgument(maxRows > 0, "Please input valid maxRows > 0, maxRows: " + maxRows);
        set(DalHintEnum.maxRows, maxRows);
        return this;
    }

    /**
     * Set number of seconds the driver will wait for a Statement object to execute.
     *
     * @param seconds
     * @return this {@code Hints}
     * @see DalHintEnum#timeout
     */
    public Hints timeout(int seconds) {
        set(DalHintEnum.timeout, seconds);
        return this;
    }

    /**
     * Set how many seconds the slave is behind master.
     *
     * @param seconds
     * @return this {@code Hints}
     * @see DalHintEnum#freshness
     */
    public Hints freshness(int seconds) {
        set(DalHintEnum.freshness, seconds);
        return this;
    }

    /**
     * Indicate using master database even the operation can be routed to slave database
     *
     * @param masterOnly
     * @return this {@code Hints}
     * @see DalHintEnum#masterOnly
     */
    public Hints masterOnly() {
        set(DalHintEnum.masterOnly, Boolean.TRUE);
        hints.remove(DalHintEnum.slaveOnly);
        return this;
    }

    /**
     * Indicate using slave database even the operation is not a query.
     *
     * @param slaveOnly
     * @return this {@code Hints}
     * @see DalHintEnum#slaveOnly
     */
    public Hints slaveOnly() {
        set(DalHintEnum.slaveOnly, Boolean.TRUE);
        hints.remove(DalHintEnum.masterOnly);
        return this;
    }

    /**
     * Specify which db to execute the operation
     *
     * @param databaseName the connectionString part of the dal.xml/config
     * @return this {@code Hints}
     */
    public Hints inDatabase(String databaseName) {
        hints.put(DalHintEnum.designatedDatabase, databaseName);
        return this;
    }

    /*
      Following methods are moved from DalHints
     */

    /**
     * Returns {@code KeyHolder} instance.
     *
     * @return {@code KeyHolder} instance
     * @see KeyHolder
     */
    public KeyHolder getKeyHolder() {
        return keyHolder;
    }

    /**
     * Set {@code KeyHolder} instance.
     *
     * @param keyHolder
     * @return this {@code Hints}
     * @see KeyHolder
     */
    public Hints prepareInsert() {
        this.keyHolder = new KeyHolder();
        return this;
    }

    /**
     * Returns {@code DasVersionInfo} instance
     *
     * @return {@code DasVersionInfo}
     * @see DasVersionInfo
     */
    public DasVersionInfo getVersionInfo() {
        return versionInfo;
    }

    /**
     * Set {@code DasVersionInfo} instance.
     *
     * @param versionInfo
     * @return this {@code Hints}
     * @see DasVersionInfo
     */
    public Hints setVersionInfo(DasVersionInfo versionInfo) {
        this.versionInfo = versionInfo;
        return this;
    }

    /**
     * Deep copy from this {@code Hints}
     *
     * @return a clone {@code Hints} instance
     */
    public Hints clone() {
        Hints newHints = new Hints();
        newHints.hints.putAll(hints);

        // Make sure we do deep copy for Map
        Map fields = (Map)newHints.get(DalHintEnum.fields);
        if(fields != null)
            newHints.setFields(new LinkedHashMap<String, Object>(fields));

        newHints.keyHolder = keyHolder;
        newHints.dasDiagnose = getDiagnose();
        newHints.diagnoseMode = isDiagnose();
        newHints.versionInfo = getVersionInfo();
        return newHints;
    }

    /**
     * Constructor with version information
     */
    public Hints() {
        DasVersionInfo versionInfo = new DasVersionInfo();
        versionInfo.setDasClientVersion(DasClientVersion.getVersion());
        versionInfo.setDasCoreVersion(DasCoreVersion.getVersion());

        setVersionInfo(versionInfo);
        allowPartial();
    }

    /**
     * Make sure only shardId, tableShardId, shardValue, shardColValue will be used to locate shard Id.
     */
    public Hints cleanUp() {
        hints.remove(DalHintEnum.fields);
        hints.remove(DalHintEnum.parameters);
        return this;
    }

    /**
     * Returns <tt>true</tt> if given {@code DalHintEnum} is used.
     *
     * @param hint
     * @return <tt>true</tt> if given {@code DalHintEnum} is used
     */
    public boolean is(DalHintEnum hint) {
        return hints.containsKey(hint);
    }

    /**
     * Returns object associated with given {@code DalHintEnum}.
     *
     * @param hint
     * @return object associated with given {@code DalHintEnum}.
     */
    public Object get(DalHintEnum hint) {
        return hints.get(hint);
    }

    /**
     * Returns {@code DalHA}.
     *
     * @return {@code DalHA}
     * @see DalHintEnum#heighAvaliable
     */
    public DalHA getHA(){
        return (DalHA)hints.get(DalHintEnum.heighAvaliable);
    }

    /**
     * Set given {@code DalHA}.
     *
     * @param ha
     * @return {@code Hints}
     */
    public Hints setHA(DalHA ha){
        hints.put(DalHintEnum.heighAvaliable, ha);
        return this;
    }

    /**
     * Get integer associated with given {@code DalHintEnum}.
     *
     * @param hint
     * @param defaultValue if null is associated
     * @return integer associated with given {@code DalHintEnum}, defaultValue if null is associated
     */
    public Integer getInt(DalHintEnum hint, int defaultValue) {
        Object value = hints.get(hint);
        if(value == null)
            return defaultValue;
        return (Integer)value;
    }

    /**
     * Get integer associated with given {@code DalHintEnum}.
     *
     * @param hint
     * @return integer associated with given {@code DalHintEnum}.
     */
    public Integer getInt(DalHintEnum hint) {
        return (Integer)hints.get(hint);
    }

    /**
     * Get string associated with given {@code DalHintEnum}.
     *
     * @param hint
     * @return string associated with given {@code DalHintEnum}
     */
    public String getString(DalHintEnum hint) {
        Object value = hints.get(hint);
        if(value == null)
            return null;

        if(value instanceof String)
            return (String)value;

        return value.toString();
    }

    /**
     * Get {@code Set<String>} associated with given {@code DalHintEnum}.
     *
     * @param hint
     * @return {@code Set<String>} associated with given {@code DalHintEnum} as {@code Set<String>}
     */
    public Set<String> getStringSet(DalHintEnum hint) {
        return (Set<String>)hints.get(hint);
    }

    /**
     * Set dummy object with given {@code DalHintEnum}
     *
     * @param hint
     * @return {@code Hints}
     */
    private Hints set(DalHintEnum hint) {
        set(hint, NULL);
        return this;
    }

    /**
     * Set dummy given object with given {@code DalHintEnum}
     *
     * @param hint
     * @param value
     * @return this {@code Hints}
     */
    private Hints set(DalHintEnum hint, Object value) {
        hints.put(hint, value);
        return this;
    }

    /**
     * Set {@code Map<String, Object>} of column name value pair.
     *
     * @param tableShardValue
     * @return this {@code Hints}
     * @see DalHintEnum#shardColValues
     */
    public Hints setTableShardValue(Object tableShardValue) {
        return set(DalHintEnum.tableShardValue, tableShardValue);
    }

    /**
     * Set entity columns to help sharding strategy locate shard.
     *
     * @param fields
     * @return this {@code Hints}
     * @see DalHintEnum#fields
     */
    public Hints setFields(Map<String, ?> fields) {
        if(fields == null)
            return this;

        return set(DalHintEnum.fields, fields);
    }

    /**
     * Set {@code Parameter}s to help sharding strategy locate shard.
     *
     * @param parameters
     * @return this {@code Hints}
     * @see DalHintEnum#parameters
     */
    public Hints setParameters(List<Parameter> parameters) {
        if(parameters == null)
            return this;

        return set(DalHintEnum.parameters, parameters);
    }

    /**
     * Log {@code Throwable} with {@code DalLogger}.
     *
     * @param msg
     * @param e
     * @throws SQLException
     */
    public void handleError(String msg, Throwable e) throws SQLException {
        if(e == null)
            return;

        // Just make sure error is not swallowed by us
        DasConfigureFactory.getDalLogger().error(msg, e);
        throw DalException.wrap(e);
    }

    /**
     * Returns if insert incremental id is disabled or not.
     *
     * @return insert incremental id is disabled
     * @see DalHintEnum#enableIdentityInsert
     */
    public boolean isIdentityInsertDisabled() {
        return !is(DalHintEnum.enableIdentityInsert);
    }

    /**
     * Returns if the update field can be null value or not
     *
     * @return if the update field can be null value or not
     * @see DalHintEnum#updateNullField
     */
    public boolean isUpdateNullField() {
        return is(DalHintEnum.updateNullField);
    }

    /**
     * Returns if set the update field can be unchanged value after select from DB or not
     *
     * @return if set the update field can be unchanged value after select from DB or not
     * @see DalHintEnum#updateUnchangedField
     */
    public boolean isUpdateUnchangedField() {
        return is(DalHintEnum.updateUnchangedField);
    }

    /**
     * Returns if insert field can be null value or not.
     *
     * @return if insert field can be null value or not
     * @see DalHintEnum#insertNullField
     */
    public boolean isInsertNullField() {
        return is(DalHintEnum.insertNullField);
    }

    /**
     * Returns columns that will be included for update
     *
     * @return columns that will be included for update
     * @see DalHintEnum#includedColumns
     */
    public Set<String> getIncluded() {
        return getStringSet(DalHintEnum.includedColumns);
    }

    /**
     * Returns columns that will be excluded for update.
     *
     * @return columns that will be excluded for update
     * @see DalHintEnum#excludedColumns
     */
    public Set<String> getExcluded() {
        return getStringSet(DalHintEnum.excludedColumns);
    }

    /**
     * Returns columns that will be included for query.
     *
     * @return columns that will be included for query
     * @see DalHintEnum#partialQuery
     */
    public String[] getPartialQueryColumns() {
        return getStringSet(DalHintEnum.partialQuery).toArray(new String[getStringSet(DalHintEnum.partialQuery).size()]);
    }

    /**
     * Returns columns that will be included for query.
     *
     * @return this {@code Hints}
     * @see DalHintEnum#partialQuery
     */
    public Hints allowPartial() {
        return set(DalHintEnum.allowPartial);
    }
    // For internal use
    
    /**
     * Set {@code DasDiagnose} instance
     *
     * @param dasDiagnose
     * @return this {@code Hints}
     * @see DasDiagnose
     */
    public Hints setDasDiagnose(DasDiagnose dasDiagnose) {
        this.dasDiagnose = dasDiagnose;
        return this;
    }

    public <T> Hints setSize(List<T> pojos) {
        keyHolder = vaidateKeyHolder();

        if (keyHolder != null && pojos != null)
            keyHolder.initialize(pojos.size());

        return this;
    }

    public <T> Hints setSize(T pojo) {
        keyHolder = vaidateKeyHolder();

        if (keyHolder != null && pojo != null)
            keyHolder.initialize(1);

        return this;
    }

    private KeyHolder vaidateKeyHolder() {
        if (is(DalHintEnum.setIdentityBack))
            keyHolder = keyHolder == null ? new KeyHolder() : keyHolder;
        return keyHolder;
    }

    /**
     * Returns <tt>true</tt> if {@code setIdBack} is called.
     *
     * @return <tt>true</tt> if {@code setIdBack} is called
     */
    public boolean isSetIdBack() {
        return is(DalHintEnum.setIdentityBack);
    }

    /**
     * Returns <tt>true</tt> if diagnose mode is enabled
     *
     * @return <tt>true</tt> if diagnose mode is enabled
     */
    public boolean isDiagnose() {
        return diagnoseMode;
    }

    /**
     * Returns <tt>true</tt> if {@code insertWithId} is called.
     *
     * @return <tt>true</tt> if {@code insertWithId} is called
     */
    public boolean isInsertWithId() {
        return is(DalHintEnum.enableIdentityInsert);
    }
    
    /**
     * Returns which shard the operation will be performed.
     *
     * @return which shard the operation will be performed
     * @see DalHintEnum#shard
     */
    public String getShard() {
        return getString(DalHintEnum.shard);
    }

    /**
     * Returns value used to help sharding strategy locate DB shard.
     *
     * @return value used to help sharding strategy locate DB shard.
     * @see DalHintEnum#shardValue
     */
    public Object getShardValue() {
        return this.getString(DalHintEnum.shardValue);
    }

    /**
     * Returns value used to help sharding strategy locate table shard.
     *
     * @return value used to help sharding strategy locate table shard
     * @see DalHintEnum#tableShardValue
     */
    public Object getTableShardValue() {
        return getString(DalHintEnum.tableShardValue);
    }

    /**
     * Returns which table shard the operation will be performed.
     *
     * @return which table shard the operation will be performed
     * @see DalHintEnum#tableShard
     */
    public String getTableShard() {
        return getString(DalHintEnum.tableShard);
    }

    //The following are methods to be tested and opened

//    /**
//     * Set the insert field can be null value.
//     *
//     * @param insertNullField
//     * @return this {@code Hints}
//     * @see DalHintEnum#insertNullField
//     */
//    public Hints insertNullField() {
//        set(DalHintEnum.insertNullField, Boolean.TRUE);
//        return this;
//    }
//
//    /**
//     * Set the update field can be null value.
//     *
//     * @param updateNullField
//     * @return this {@code Hints}
//     * @see DalHintEnum#updateNullField
//     */
//    public Hints updateNullField() {
//        set(DalHintEnum.updateNullField, Boolean.TRUE);
//        return this;
//    }
//
//    /**
//     * Set isolation level should be used to set on connection.
//     *
//     * @param isolationLevel
//     * @return this {@code Hints}
//     * @see DalHintEnum#isolationLevel
//     */
//    public Hints isolationLevel(int isolationLevel) {
//        set(DalHintEnum.isolationLevel, isolationLevel);
//        return this;
//    }


}
