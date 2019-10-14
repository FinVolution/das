package com.ppdai.das.strategy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * An abstract implementation of ShardingStrategy that can be used as a starter for real strategy.
 * It already implements most of the access methods defined in ShardingStrategy. 
 * Subclass need to implement locateDbShards and locateTableShards.
 * 
 * @author hejiehui
 *
 */
public abstract class AbstractShardingStrategy implements ShardingStrategy {
    /**
     * Key used to declared tables that qualified for table shard. That's not every table is sharded
     */
    public static final String SHARDED_TABLES = "shardedTables";
    
    /**
     * Separator used to combine logic table name and table shard to make real table name
     */
    public static final String SEPARATOR = "separator";

    private boolean shardByDb = false;
    private boolean shardByTable = false;
    
    private Set<String> shardedTables;
    private Set<String> allTableShards;
    
    private String separator = "_";

    @Override
    public abstract Set<String> locateDbShards(ShardingContext ctx);

    @Override
    public abstract Set<String> locateTableShards(TableShardingContext ctx);
    
    @Override
    public void initialize(Map<String, String> settings) {
        if(settings.containsKey(SHARDED_TABLES)) {
            setShardedTables(parseNames(settings.get(SHARDED_TABLES)));
        }
        
        if(settings.containsKey(SEPARATOR)) {
            setSeparator(settings.get(SEPARATOR));
        }
    }

    protected Set<String> parseNames(String value) {
        // names are separated by ','
        String[] names = value.split(",");
        Set<String> nameSet = new HashSet<>();
        for(int i = 0; i < names.length; i++)
            nameSet.add(names[i].toLowerCase().trim());
        return nameSet;
    }
    
    protected void setShardedTables(Set<String> shardedTables) {
        this.shardedTables = new HashSet<>();
        for(String shard: shardedTables)
            this.shardedTables.add(shard.toLowerCase());
    }

    protected void setSeparator(String separator) {
        this.separator = separator;
    }

    protected void setShardByDb(boolean shardByDb) {
        this.shardByDb = shardByDb;
    }

    protected void setShardByTable(boolean shardByTable) {
        this.shardByTable = shardByTable;
    }

    protected void setAllTableShards(Set<String> allTableShards) {
        Objects.requireNonNull(allTableShards);
        if(allTableShards.isEmpty())
            throw new IllegalArgumentException("Table shards should not be empty");
        
        this.allTableShards = new HashSet<>(allTableShards);
    }

    @Override
    public boolean isShardByDb() {
        return shardByDb;
    }

    @Override
    public boolean isShardByTable() {
        return shardByTable;
    }

    @Override
    public boolean isShardingEnable(String logicTableName) {
        Objects.requireNonNull(logicTableName, "logicTableName is null");
        return shardedTables.contains(logicTableName.toLowerCase());
    }

    @Override
    public Set<String> getAllTableShards() {
        return allTableShards;
    }

    @Override
    public String getTableName(String logicTableName, String shard) {
        return separator == null ? logicTableName + shard : logicTableName + separator + shard;
    }
    
    /**
     * Helper method that wrap a string into set
     * @param id
     * @return
     */
    protected Set<String> toSet(String id) {
        if(id == null)
            return Collections.emptySet();

        Set<String> shards = new HashSet<>();
        shards.add(id);
        return shards;
    }
}