package com.ppdai.das.core.task;

import java.util.concurrent.Callable;

import com.ppdai.das.core.DasConfigureFactory;
import com.ppdai.das.core.DasLogger;
import com.ppdai.das.core.LogContext;
import com.ppdai.das.core.LogEntry;
import com.ppdai.das.core.exceptions.DalException;

public class RequestTaskWrapper<T> implements Callable<T> {
    private DasLogger logger = DasConfigureFactory.getLogger();
    private String shard;
    private Callable<T> task;
    private LogContext logContext;

    public RequestTaskWrapper(String shard, Callable<T> task, LogContext logContext) {
        this.shard = shard;
        this.task = task;
        this.logContext = logContext;
    }

    @Override
    public T call() throws Exception {
        Throwable error = null;
        T result = null;

        logger.startTask(logContext, shard);

        try {
            LogEntry.populateCurrentCaller(logContext.getCaller());

            result = task.call();

            LogEntry.clearCurrentCaller();
        } catch (Throwable e) {
            error = e;
        }

        logger.endTask(logContext, shard, error);

        if(error != null)
            throw DalException.wrap(error);

        return result;
    }
}
