package com.ppdai.das.client.delegate.datasync;

import java.util.List;

public interface DasDataSynchronizer extends AutoCloseable{

    /**
     * Validation job execute interval in second
     */
    long getValidationInterval(String logicDBName);

    /**
     * Logic db names, which are in sync mode
     */
    List<String> getSyncLogicDbNames();

    /**
     * Write DataSyncContext in new data source
     */
    void syncData(DataSyncContext dataSyncContext);

    /**
     * Validation implementation, which is triggered by DataSycTimer
     */
    void validate(String logicDBName);

}
