package com.ppdai.das.client.delegate.datasync;

import com.ppdai.das.client.Hints;
import com.ppdai.das.core.client.DalTransactionManager;
import com.ppdai.das.service.DasOperation;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Date;

public class DataSyncContext {
    private String logicDbName;
    private Object data;
    private DasOperation dasOperation;
    private Hints hints;
    private Object result;
    private Exception exception;
    private boolean inTransaction = false;
    private long sequenceId = 0; //per logic db
    private long globalSequenceId = 0; //per instance
    private Date sinceTime;
    private Date timestamp = new Date();
    private String ip;

    public DataSyncContext(String logicDbName) {
        this.logicDbName = logicDbName;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
            sinceTime = new Date(jvmStartTime);
            globalSequenceId = SequenceGenerator.getGlobalSequenceId();
            sequenceId = SequenceGenerator.getSequenceId(logicDbName);
            inTransaction = DalTransactionManager.isInTransaction();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "DataSyncContext{" +
                "logicDbName='" + logicDbName + '\'' +
                ", data=" + data +
                ", dasOperation=" + dasOperation +
                ", hints=" + hints +
                ", result=" + result +
                ", exception=" + exception +
                ", inTransaction=" + inTransaction +
                ", sequenceId=" + sequenceId +
                ", globalSequenceId=" + globalSequenceId +
                ", sinceTime=" + sinceTime +
                ", timestamp=" + timestamp +
                ", ip='" + ip + '\'' +
                '}';
    }

    public DataSyncContext setLogicDbName(String logicDbName) {
        this.logicDbName = logicDbName;
        return this;
    }

    public DataSyncContext setData(Object data) {
        this.data = data;
        return this;
    }

    public DataSyncContext setHints(Hints hints) {
        this.hints = hints;
        return this;
    }

    public DataSyncContext setResult(Object result) {
        this.result = result;
        return this;
    }

    public DataSyncContext setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public DataSyncContext setInTransaction(boolean inTransaction) {
        this.inTransaction = inTransaction;
        return this;
    }

    public DataSyncContext setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
        return this;
    }

    public DataSyncContext setGlobalSequenceId(long globalSequenceId) {
        this.globalSequenceId = globalSequenceId;
        return this;
    }

    public DataSyncContext setSinceTime(Date sinceTime) {
        this.sinceTime = sinceTime;
        return this;
    }

    public DataSyncContext setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public DataSyncContext setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public Object getData() {
        return data;
    }

    public Hints getHints() {
        return hints;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isInTransaction() {
        return inTransaction;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public long getGlobalSequenceId() {
        return globalSequenceId;
    }

    public Date getSinceTime() {
        return sinceTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getIp() {
        return ip;
    }

    public DasOperation getDasOperation() {
        return dasOperation;
    }

    public DataSyncContext setDasOperation(DasOperation dasOperation) {
        this.dasOperation = dasOperation;
        return this;
    }
}
