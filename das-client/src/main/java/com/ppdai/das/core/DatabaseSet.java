package com.ppdai.das.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.core.enums.DatabaseCategory;
import com.ppdai.das.strategy.ShardingStrategy;

public class DatabaseSet {
	private static final String CLASS = "class";
	private static final String ENTRY_SEPARATOR = ";";
	private static final String KEY_VALUE_SEPARATOR = "=";
	
	private String name;
	private String provider;
	private DatabaseCategory dbCategory;

	private ShardingStrategy strategy;
	private Map<String, DataBase> databases;
	// Key is shard id, value is all database under in this shard
	private Map<String, List<DataBase>> masterDbByShard = new HashMap<String, List<DataBase>>();
	private Map<String, List<DataBase>> slaveDbByShard = new HashMap<String, List<DataBase>>();

	private List<DataBase> masterDbs = new ArrayList<DataBase>();
	private List<DataBase> slaveDbs = new ArrayList<DataBase>();
	
	private Set<String> readOnlyAllShards;
	
	/**
	 * The target DB set does not support shard
	 * @param name
	 * @param provider
	 * @param databases
	 * @throws Exception
	 */
	public DatabaseSet(String name, String provider, Map<String, DataBase> databases) throws Exception {
		this(name, provider, null, databases);
	}
	
	public DatabaseSet(String name, String provider, String shardStrategy, Map<String, DataBase> databases) throws Exception {
		this.name = name;
		this.provider = provider;
		dbCategory = DatabaseCategory.matchWith(provider);
		this.databases = databases;

		initStrategy(shardStrategy);
		initShards();
	}

	public DatabaseSet(String name, String provider, String shardStrategy, ShardingStrategy strategy, Map<String, DataBase> databases) throws Exception {
		this.name = name;
		this.provider = provider;
		dbCategory = DatabaseCategory.matchWith(provider);
		this.databases = databases;
        this.strategy = strategy;

		initStrategy(shardStrategy);
		initShards();
	}

	private void initStrategy(String shardStrategy) throws Exception {
		if(shardStrategy == null || shardStrategy.length() == 0)
			return;
		
		String[] values = shardStrategy.split(ENTRY_SEPARATOR);
		String[] strategyDef = values[0].split(KEY_VALUE_SEPARATOR);

		if(strategy == null) {
			if(strategyDef[0].trim().equals(CLASS))
				strategy = (ShardingStrategy)Class.forName(strategyDef[1].trim()).newInstance();
		}

		Map<String, String> settings = new HashMap<String, String>();
		for(int i = 1; i < values.length; i++) {
			String[] entry = values[i].split(KEY_VALUE_SEPARATOR);
			settings.put(entry[0].trim(), entry[1].trim());
		}
		strategy.initialize(settings);
	}

	private void initShards() throws Exception {
		if(strategy == null || strategy.isShardByDb() == false){
			// Init with no shard support
			for(DataBase db: databases.values()) {
				if(db.isMaster())
					masterDbs.add(db);
				else
					slaveDbs.add(db);
			}
		}else{
			// Init map by shard
			for(DataBase db: databases.values()) {
				Map<String, List<DataBase>> dbByShard = db.isMaster() ?
						masterDbByShard : slaveDbByShard;
					
				List<DataBase> dbList = dbByShard.get(db.getSharding());
				if(dbList == null) {
					dbList = new ArrayList<DataBase>();
					dbByShard.put(db.getSharding(), dbList);
				}
				dbList.add(db);
			}
		}
		readOnlyAllShards = Collections.unmodifiableSet(masterDbByShard.keySet());
	}
	
	public String getName() {
		return name;
	}

	public String getProvider() {
		return provider;
	}

	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
	}
	
	public boolean isShardingSupported() {
		return strategy != null && strategy.isShardByDb();
	}

	public boolean isShardByTable() {
	    return strategy != null && strategy.isShardByTable();
	}

	public boolean isTableShardingSupported(String tableName) {
		return isShardByTable() && strategy.isShardingEnable(tableName);
	}
	
	public Map<String, DataBase> getDatabases() {
		return new HashMap<>(databases);
	}
	
	public void validate(String shard) throws SQLException {
		if(!masterDbByShard.containsKey(shard))
			throw new SQLException("No shard defined for id: " + shard);
	}
	
	public Set<String> getAllShards() {
		return readOnlyAllShards;
	}

	public ShardingStrategy getStrategy() throws SQLException {
		if(strategy == null)
			throw new SQLException("No sharding stradegy defined");
		return strategy;
	}
	
    public List<DataBase> getMasterDbs() {
        return masterDbs == null ? null : new ArrayList<>(masterDbs);
    }

    public List<DataBase> getSlaveDbs() {
        return slaveDbs == null ? null : new ArrayList<>(slaveDbs);
    }
    
	public List<DataBase> getMasterDbs(String shard) {
		return masterDbByShard.containsKey(shard) ? new ArrayList<>(masterDbByShard.get(shard)) : null;
	}

	public List<DataBase> getSlaveDbs(String shard) {
	    return slaveDbByShard.containsKey(shard) ? new ArrayList<>(slaveDbByShard.get(shard)) : null;
	}
}