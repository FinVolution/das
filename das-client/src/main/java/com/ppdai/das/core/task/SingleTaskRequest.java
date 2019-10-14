package com.ppdai.das.core.task;

import static com.ppdai.das.core.helper.ShardingManager.detectDistributedTransaction;
import static com.ppdai.das.core.helper.ShardingManager.isShardingEnabled;
import static com.ppdai.das.core.helper.ShardingManager.locateShardId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.DasVersionInfo;
import com.ppdai.das.core.KeyHolder;
import com.ppdai.das.core.ResultMerger;
import com.ppdai.das.core.exceptions.DalException;
import com.ppdai.das.core.exceptions.ErrorCode;

public class SingleTaskRequest<T> implements DalRequest<Integer> {
    private String appId;
    private String logicDbName;
    private String logicTableName;
    private Hints hints;
    private T rawPojo;
    private Map<String, ?> daoPojo;
    private String shardId;
    private SingleTask<T> task;

    public SingleTaskRequest(String appId, String logicDbName, Hints rawHints, T rawPojo, SingleTask<T> task) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        logicTableName = task.getParser().getTableName();
        this.hints = rawHints;
        hints.setSize(rawPojo);
        this.rawPojo = rawPojo;
        this.task = task;
    }

    @Override
    public void validate() throws SQLException {
        if (rawPojo == null)
            throw new DalException(ErrorCode.ValidatePojo);

        if (task == null)
            throw new DalException(ErrorCode.ValidateTask);

        daoPojo = task.getPojoFields(rawPojo);

        // Locate shard id and check for distributed transaction 
        if(!isShardingEnabled(appId, logicDbName))
            return;
        
        //Locate from hints
        shardId = locateShardId(appId, logicDbName, hints);

        //Locate from pojo
        if(shardId == null)
            shardId = locateShardId(appId, logicDbName, logicTableName, daoPojo);

        if(shardId == null)
            throw new IllegalArgumentException("Can not locate shard for pojo: " + daoPojo);
        
        detectDistributedTransaction(shardId);
    }

    @Override
    public boolean isCrossShard() {
        // The single task request is always executed as if the pojos are not corss shard even they really are.
        return false;
    }

    @Override
    public Callable<Integer> createTask() {
        hints = shardId == null ? hints : hints.clone().inShard(shardId);
        return new SingleTaskCallable<>(hints, daoPojo, rawPojo, task);
    }

    @Override
    public Map<String, Callable<Integer>> createTasks() throws SQLException {
        throw new DalException(ErrorCode.NotSupported);
    }

    @Override
    public ResultMerger<Integer> getMerger() {
        // Not support for now. Maybe support for new hints in the future
        return null;
    }

    @Override
    public void endExecution() throws SQLException {
        List<T> rawPojos = new ArrayList<>(1);
        rawPojos.add(rawPojo);
        KeyHolder.setGeneratedKeyBack(task, hints, rawPojos);
    }

    private static class SingleTaskCallable<T> implements Callable<Integer> {
        private Hints hints;
        private Map<String, ?> daoPojo;
        private T rawPojo;
        private SingleTask<T> task;

        public SingleTaskCallable(Hints hints, Map<String, ?> daoPojo, T rawPojo, SingleTask<T> task) {
            this.hints = hints;
            this.daoPojo = daoPojo;
            this.rawPojo = rawPojo;
            this.task = task;
        }

        @Override
        public Integer call() throws Exception {
            return task.execute(hints, daoPojo, rawPojo);
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
