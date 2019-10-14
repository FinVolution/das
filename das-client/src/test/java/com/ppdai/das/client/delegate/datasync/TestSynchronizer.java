package com.ppdai.das.client.delegate.datasync;

import com.google.common.collect.Lists;

import java.util.List;

public class TestSynchronizer implements DasDataSynchronizer{

    public volatile int jobCount = 0;
    public volatile int syncCount = 0;
    @Override
    public long getValidationInterval(String logicDBName) {
        return 1;
    }

    @Override
    public List<String> getSyncLogicDbNames() {
        return Lists.newArrayList("dbA", "dbB");
    }

    @Override
    public void syncData(DataSyncContext dataSyncContext) {
        syncCount++;
    }

    @Override
    public void validate(String logicDBName) {
        jobCount++;
    }


    @Override
    public void close() throws Exception {

    }
}