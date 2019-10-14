package com.ppdai.das.core.configure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.ppdai.das.core.DalHintEnum;
import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.exceptions.DalException;

import static com.ppdai.das.core.enums.DatabaseCategory.MySql;

public class FreshnessSelector implements DatabaseSelector {
    public static final String FRESHNESS_READER = "freshnessReader";
    public static final String UPDATE_INTERVAL = "updateInterval";
    public static final String QUERY_SQL = "querySQL";
    public static final String SELECT_FIELD = "selectField";

    public static final int DEFAULT_INTERVAL = 5;
    public static final String DEFAULT_QUERY_SQL = "show slave status";
    public static final String DEFAULT_SELECT_FIELD = "Seconds_Behind_Master";
    /**
     * AppId:LogicDB:phicicalDB+shard:freshness
     */
    private static final Map<String, Map<String, Map<ConnectShard, Integer>>> appFreshnessCache = new ConcurrentHashMap<>();
    private static AtomicReference<ScheduledExecutorService> freshnessUpdatorRef = new AtomicReference<>();
    
    private static final int INVALID = FreshnessReader.INVALID;
    
    private DefaultDatabaseSelector defaultSelector = new DefaultDatabaseSelector();
    private FreshnessReader reader = new SlaveStatusFreshnessReader();
    private int interval = DEFAULT_INTERVAL;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                shutdown();
            }
        }));
    }

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        if(settings.containsKey(FRESHNESS_READER)) {
            String readerClass = settings.get(FRESHNESS_READER).trim();
            reader = (FreshnessReader)Class.forName(readerClass).newInstance();
            if(reader instanceof SlaveStatusFreshnessReader) {
                String querySQL = settings.getOrDefault(QUERY_SQL, DEFAULT_QUERY_SQL);
                String selectField = settings.getOrDefault(SELECT_FIELD, DEFAULT_SELECT_FIELD);
                ((SlaveStatusFreshnessReader)reader).setQuerySQL(querySQL);
                ((SlaveStatusFreshnessReader)reader).setSelectField(selectField);
            }
        }
        
        interval = settings.containsKey(UPDATE_INTERVAL) ? Integer.parseInt(settings.get(UPDATE_INTERVAL)) : DEFAULT_INTERVAL;
    }

    static class ConnectShard {
        String connectionString;
        String shard;

        public ConnectShard(String connectionString, String shard) {
            this.connectionString = connectionString;
            this.shard = shard;
        }

        public String getConnectionString() {
            return connectionString;
        }

        public String getShard() {
            return shard;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectShard that = (ConnectShard) o;
            return Objects.equals(connectionString, that.connectionString) &&
                    Objects.equals(shard, that.shard);
        }

        @Override
        public int hashCode() {
            return Objects.hash(connectionString, shard);
        }
    }
    /**
     * Need to be called during getQualifiedSlaveNames
     * 
     * @param configure
     */
    private void initialize() {
        if(freshnessUpdatorRef.get() != null)
            return;
        
        synchronized (FreshnessReader.class) {
            if(freshnessUpdatorRef.get() != null)
                return;
            
            appFreshnessCache.clear();
            for(String appId: DasConfigureFactory.getAppIds()) {
                DalConfigure configure = DasConfigureFactory.getDalConfigure(appId);
                Map<String, Map<ConnectShard, Integer>> freshnessCache = new ConcurrentHashMap<>();
                for(String logicDbName: configure.getDatabaseSetNames()) {
                    Map<ConnectShard, Integer> logicDbFreshnessMap = new ConcurrentHashMap<>();
                    freshnessCache.put(logicDbName, logicDbFreshnessMap);
                    
                    DatabaseSet dbSet = configure.getDatabaseSet(logicDbName);
                    if(dbSet.getDatabaseCategory() == MySql) {
                        for(Map.Entry<String, DataBase> dbEntry: dbSet.getDatabases().entrySet()) {
                            DataBase db = dbEntry.getValue();
                            if(!db.isMaster())
                                logicDbFreshnessMap.put(new ConnectShard(db.getConnectionString(), db.getSharding()), INVALID);
                        }
                    }
                }
                appFreshnessCache.put(appId, freshnessCache);
            }
            
            if(reader == null)
                return;
            
            //Init task
            ScheduledExecutorService executer = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                AtomicInteger atomic = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Dal-FreshnessScanner" + this.atomic.getAndIncrement());
                }
            });
            executer.scheduleWithFixedDelay(new FreshnessScanner(reader), 0, interval, TimeUnit.SECONDS);
            freshnessUpdatorRef.set(executer);
        }
    }
    
    private static void shutdown() {
        if (freshnessUpdatorRef.get() == null)
            return;
        
        synchronized (FreshnessReader.class) {
            if (freshnessUpdatorRef.get() == null)
                return;
            
            freshnessUpdatorRef.get().shutdown();
            freshnessUpdatorRef.set(null);
        }
    }
    
    /**
     * Freshness can also be update from outside
     * @param logicDbName
     * @param slaveConnectionString
     * @param freshness
     */
    public static void update(String appId, String logicDbName, ConnectShard cs, int freshness) {
        Map<ConnectShard, Integer> logicDbFreshnessMap = appFreshnessCache.get(appId).get(logicDbName);
        logicDbFreshnessMap.put(cs, freshness);
    }
    
    /**
     * Get freshness for given logic db and slave
     * @param logicDbName
     * @param slaveDbName
     * @return
     */
    public static int getFreshness(String appId, String logicDbName, String connectionString) {
        return appFreshnessCache.get(appId).get(logicDbName).get(connectionString);
    }
    
    /**
     * A handy way of getting qualified slaves
     * 
     * @param logicDbName
     * @param freshness
     * @return
     */
    private List<DataBase> filterQualifiedSlaves(String appId, String logicDbName, List<DataBase> slaves, int qualifiedFreshness) {
        List<DataBase> qualifiedSlaves = new ArrayList<>();
        if(!appFreshnessCache.get(appId).containsKey(logicDbName))
            return qualifiedSlaves;
        
        Map<ConnectShard, Integer> logicDbFreshnessMap = appFreshnessCache.get(appId).get(logicDbName);
        for(DataBase slaveDb: slaves) {
            Integer freshness = logicDbFreshnessMap.get(new ConnectShard(slaveDb.getConnectionString(), slaveDb.getSharding()));
            if(freshness == null || freshness.equals(INVALID))
                continue;
            
            if(freshness <= qualifiedFreshness)
                qualifiedSlaves.add(slaveDb);
        }
        
        return qualifiedSlaves;
    }

    @Override
    public String select(SelectionContext context) throws DalException {
        //Will check if already initialized
        initialize();
        
        Integer freshness = context.getHints().getInt(DalHintEnum.freshness);
        List<DataBase> slaves = context.getSlaves();
        
        // Not specified 
        if(freshness == null || slaves == null || slaves.isEmpty())
            return defaultSelector.select(context);

        context.setSlaves(filterQualifiedSlaves(context.getAppId(), context.getLogicDbName(), slaves, freshness));

        return defaultSelector.select(context);
    }
    
    private static class FreshnessScanner implements Runnable {
        private FreshnessReader reader;
        
        public FreshnessScanner(FreshnessReader reader) {
            this.reader = reader;
        }
        
        @Override
        public void run() {
            for(String appId: appFreshnessCache.keySet()) {
                Map<String, Map<ConnectShard, Integer>> freshnessCache = appFreshnessCache.get(appId);
                for(String logicDbName: freshnessCache.keySet()) {
                    for(ConnectShard cs: freshnessCache.get(logicDbName).keySet()) {
                        int freshness = INVALID;
                        try {
                            freshness = reader.getSlaveFreshness(logicDbName, cs.getConnectionString(), cs.getShard());
                        } catch (Throwable e) {
                            DasConfigureFactory.getDalLogger().error(String.format("Can not get freshness from slave %s for logic db %s. Error message: %s", logicDbName, cs.getConnectionString(), e.getMessage()), e);
                        }
                        freshness = freshness > 0 ? freshness : INVALID;
                        update(appId, logicDbName, cs, freshness);
                    }
                }
            }
        }
    }
}
