package com.ppdai.das.core.client;

import com.ppdai.das.core.DasDiagnose;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * You can provide your own request level context info by suclassing this class
 *
 * @author jhhe
 */
public class LogContext {
    private String appId;
    private static String execludedPackageSpace = "com.ctrip.platform.dal.dao.";
    private boolean singleTask;
    private boolean seqencialExecution;
    private Set<String> shards;
    private DasDiagnose dasDiagnose;
    private Map<String, DasDiagnose> taskDiagnoseMap = new ConcurrentHashMap<>();

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isSingleTask() {
        return singleTask;
    }

    public void setSingleTask(boolean singleTask) {
        this.singleTask = singleTask;
    }

    public boolean isSeqencialExecution() {
        return seqencialExecution;
    }

    public void setSeqencialExecution(boolean seqencialExecution) {
        this.seqencialExecution = seqencialExecution;
    }

    public Set<String> getShards() {
        return shards;
    }

    public void setShards(Set<String> shards) {
        singleTask = false;
        this.shards = shards;
    }


    public DasDiagnose getDasDiagnose() {
        return this.dasDiagnose;
    }

    public void setDasDiagnose(DasDiagnose diagnose) {
        this.dasDiagnose = diagnose;
    }

    public DasDiagnose getTaskDiagnose(String taskKey) {
        return this.taskDiagnoseMap.get(taskKey);
    }

    public void putTaskDiagnose(String taskKey, DasDiagnose taskDiagnose) {
        this.taskDiagnoseMap.put(taskKey, taskDiagnose);
    }

    /**
     * @deprecated neew fix
     * @return
     */
    public static String getRequestCaller(){
        return "N/A";
    }

    private StackTraceElement caller = new StackTraceElement("N/A", "N/A", "N/A", -1);

    public void setCaller(StackTraceElement caller) {
        this.caller = caller;
    }

    public StackTraceElement getCaller() {
        return caller;
    }
}
