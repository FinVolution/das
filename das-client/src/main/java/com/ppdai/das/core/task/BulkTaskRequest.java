package com.ppdai.das.core.task;

import static com.ppdai.das.core.KeyHolder.mergePartial;
import static com.ppdai.das.core.KeyHolder.prepareLocalHints;
import static com.ppdai.das.core.ShardingManager.detectDistributedTransaction;
import static com.ppdai.das.core.ShardingManager.isShardingEnabled;
import static com.ppdai.das.core.ShardingManager.isTableShardingEnabled;
import static com.ppdai.das.core.ShardingManager.locateShardId;
import static com.ppdai.das.core.ShardingManager.locateTableShardId;
import static com.ppdai.das.core.ShardingManager.shuffleByTable;
import static com.ppdai.das.core.ShardingManager.shuffleEntities;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DasException;
import com.ppdai.das.core.DasVersionInfo;
import com.ppdai.das.core.ErrorCode;
import com.ppdai.das.core.KeyHolder;

public class BulkTaskRequest<K, T> implements SqlRequest<K>{
    private String appId;
    private String logicDbName;
    private String rawTableName;
    private Hints hints;
    private String shardId;
    private List<T> rawPojos;
    private List<Map<String, ?>> daoPojos;
    private BulkTask<K, T> bulkTask;
    private BulkTaskContext<T> taskContext;
    private BulkTaskResultMerger<K> dbShardMerger;
    Map<String, Map<Integer, Map<String, ?>>> shuffled;
    
    public BulkTaskRequest(String appId, String logicDbName, Hints hints, List<T> rawPojos, BulkTask<K, T> bulkTask) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.rawTableName = bulkTask.getParser().getTableName();
        this.hints = hints;
        hints.setSize(rawPojos);
        this.rawPojos = rawPojos;
        this.bulkTask = bulkTask;
    }

    @Override
    public void validate() throws SQLException {
        if(null == rawPojos)
            throw new DasException(ErrorCode.ValidatePojoList);

        if(bulkTask == null)
            throw new DasException(ErrorCode.ValidateTask);

        dbShardMerger = bulkTask.createMerger();
        daoPojos = bulkTask.getPojosFields(rawPojos);
        taskContext = bulkTask.createTaskContext(hints, daoPojos, rawPojos);

        // Locate shard id and check for distributed transaction 
        if(isShardingEnabled(appId, logicDbName)) {
            shardId = locateShardId(appId, logicDbName, hints);
            if(shardId != null)
                detectDistributedTransaction(shardId);
            else {
                shuffled = shuffleEntities(appId, logicDbName, rawTableName, daoPojos);
                detectDistributedTransaction(shuffled.keySet());
            }
        }
    }
    
    @Override
    public boolean isCrossShard() throws SQLException {
        if(!isShardingEnabled(appId, logicDbName))
            return false;
        
        // If we already located shard id from hints
        if(shardId != null)
            return false;

        return shuffled.size() > 1;
    }

    @Override
    public Callable<K> createTask() throws SQLException {
        hints = hints.clone();
        handleKeyHolder(false);
        
        if(shuffled == null) {
            // Convert to index map
            Map<Integer, Map<String, ?>> daoPojosMap = new HashMap<>();
            for(int i = 0; i < daoPojos.size(); i++)
                daoPojosMap.put(i, daoPojos.get(i));

            if(shardId != null)
                hints.inShard(shardId);

            return new BulkTaskCallable<>(appId, logicDbName, rawTableName, hints, daoPojosMap, bulkTask, taskContext);
        }else {
            //Empty case
            if(shuffled.size() == 0) {
                return new BulkTaskCallable<>(appId, logicDbName, rawTableName, hints, new HashMap<Integer, Map<String, ?>>(), bulkTask, taskContext);
            }else{
                String shard = shuffled.keySet().iterator().next();
                return new BulkTaskCallable<>(appId, logicDbName, rawTableName, hints.inShard(shard), shuffled.get(shard), bulkTask, taskContext);
            }
        }
    }

    @Override
    public Map<String, Callable<K>> createTasks() throws SQLException {
        Map<String, Callable<K>> tasks = new HashMap<>();
        
        // I know this is not so elegant.
        handleKeyHolder(true);
        
        for(String shard: shuffled.keySet()) {
            Map<Integer, Map<String, ?>> pojosInShard = shuffled.get(shard);
            
            dbShardMerger.recordPartial(shard, pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]));
            
            tasks.put(shard, new BulkTaskCallable<>(appId, logicDbName, rawTableName, hints.clone().inShard(shard), shuffled.get(shard), bulkTask, taskContext));
        }

        return tasks; 
    }
    
    private void handleKeyHolder(boolean requireMerge) {
        if(hints.getKeyHolder() == null)
            return;

        hints.getKeyHolder().requireMerge();
    }

    @Override
    public BulkTaskResultMerger<K> getMerger() {
        return dbShardMerger;
    }

    @Override
    public void endExecution() throws SQLException {
        KeyHolder.setGeneratedKeyBack(bulkTask, hints, rawPojos);
    }

    private static class BulkTaskCallable<K, T> implements Callable<K> {
        private String appId;
        private String logicDbName;
        private String rawTableName;
        private Hints hints;
        private Map<Integer, Map<String, ?>> shaffled;
        private BulkTask<K, T> task;
        private BulkTaskContext<T> taskContext;

        public BulkTaskCallable(String appId, String logicDbName, String rawTableName, Hints hints, Map<Integer, Map<String, ?>> shaffled, BulkTask<K, T> task, BulkTaskContext<T> taskContext){
            this.appId = appId;
            this.logicDbName = logicDbName;
            this.rawTableName = rawTableName;
            this.hints = hints;
            this.shaffled = shaffled;
            this.task = task;
            this.taskContext = taskContext;
        }

        @Override
        public K call() throws Exception {
            if(shaffled.isEmpty())
                return task.getEmptyValue();
            
            if(isTableShardingEnabled(appId, logicDbName, rawTableName)) {
                String tableShardId = locateTableShardId(appId, logicDbName, hints);
                
                // If there is user defined table shards id
                if(tableShardId != null)
                    return execute(hints.inTableShard(tableShardId), shaffled, taskContext);
                else
                    return executeByTableShards();
            }else{
                return execute(hints, shaffled, taskContext);
            }
        }

        private K execute(Hints hints, Map<Integer, Map<String, ?>> pojosInShard, BulkTaskContext<T> taskContext) throws SQLException {
            K partial = null;
            Throwable error = null;
            Integer[] indexList = pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]);
            Hints localHints = prepareLocalHints(task, hints);

            try {
                partial = task.execute(localHints, pojosInShard, taskContext);
            } catch (Throwable e) {
                error = e;
            }

            mergePartial(task, hints.getKeyHolder(), indexList, localHints.getKeyHolder(), error);

            // Upper level may handle continue on error
            if(error != null)
                throw DasException.wrap(error);

            return partial;
        }

        private K executeByTableShards() throws SQLException {
            BulkTaskResultMerger<K> merger = task.createMerger();
            
            Map<String, Map<Integer, Map<String, ?>>> pojosInTable = shuffleByTable(appId, logicDbName, rawTableName, shaffled);
            
            if(pojosInTable.size() > 1 && hints.getKeyHolder() != null) {
                hints.getKeyHolder().requireMerge();
            }

            Hints localHints;
            for(String curTableShardId: pojosInTable.keySet()) {
                Map<Integer, Map<String, ?>> pojosInShard = pojosInTable.get(curTableShardId);

                Integer[] indexList = pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]);

                localHints = prepareLocalHints(task, hints).inTableShard(curTableShardId);

                merger.recordPartial(curTableShardId, indexList);

                Throwable error = null;
                try {
                    K partial = task.execute(localHints, pojosInShard, taskContext);
                    merger.addPartial(curTableShardId, partial);
                } catch (Throwable e) {
                    error = e;
                }

                mergePartial(task, hints.getKeyHolder(), indexList, localHints.getKeyHolder(), error);
                DasException.handleError("Error when execute table shard operation", error);
            }
            return merger.merge();
        }
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public DasVersionInfo getVersionInfo() {
        return hints.getVersionInfo();
    }

    @Override
    public Hints getHints() {
        return hints;
    }
}
