package com.ppdai.das.core;

public class TransactionId {
    //logicDbName, hostAddress, workId, last, index
    private static final String FORMAT = "%s-%s-%s-%d-%d";

    private String logicDbName;
    private String physicalDbName;
    private String shardId;
    private String hostAddress;
    private String workId;
    private long last;
    private long index;
    
    public TransactionId(String logicDbName, String physicalDbName, String shardId, String hostAddress, String workId, long last, long index) {
        this.logicDbName = logicDbName;
        this.physicalDbName = physicalDbName;
        this.shardId = shardId;
        this.hostAddress = hostAddress;
        this.workId = workId;
        this.last = last;
        this.index = index;
    }
    
    public String getLogicDbName() {
        return logicDbName;
    }
    public String getPhysicalDbName() {
        return physicalDbName;
    }
    public String getShardId() {
        return shardId;
    }

    public String getHostAddress() {
        return hostAddress;
    }
    public String getWorkId() {
        return workId;
    }
    public long getLast() {
        return last;
    }
    public long getIndex() {
        return index;
    }
    
    public String getUniqueId() {
        return buildUniqueId(logicDbName, hostAddress, workId, last, index); 
    }
    
    public static String buildUniqueId(String logicDbName, String hostAddress, String workId, long last, long index) {
        String value = String.format(FORMAT, logicDbName, hostAddress, workId, last, index);
        return value;
    }
}
