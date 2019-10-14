package com.ppdai.das.client.delegate.datasync;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DataSyncConfigurationTest {

    @Test
    public void testDataSyncConfiguration() {
        DataSyncConfiguration.getInstance().disableSyncMode();
        assertFalse(DataSyncConfiguration.getInstance().isEnableSyncMode());
        DataSyncConfiguration.getInstance().enableSyncMode();

        DasDataSynchronizer dasDataSynchronizer1 = DataSyncConfiguration.getInstance().getDasDataSynchronizer("dbA");
        assertEquals("TestSynchronizer", dasDataSynchronizer1.getClass().getSimpleName());

        DasDataSynchronizer dasDataSynchronizer2 = DataSyncConfiguration.getInstance().getDasDataSynchronizer("dbB");
        assertEquals("TestSynchronizer", dasDataSynchronizer2.getClass().getSimpleName());
        assertTrue(DataSyncConfiguration.getInstance().isEnableSyncMode());
    }

    @Test
    public void testDataSyncConfigurationJob() throws InterruptedException {
        DataSyncConfiguration.getInstance().enableSyncMode();

        TestSynchronizer dasDataSynchronizer1 = (TestSynchronizer)DataSyncConfiguration.getInstance().getDasDataSynchronizer("dbA");
        assertEquals("TestSynchronizer", dasDataSynchronizer1.getClass().getSimpleName());
        TimeUnit.SECONDS.sleep(2);

        assertTrue(dasDataSynchronizer1.jobCount > 0);
        DataSyncConfiguration.getInstance().skipValidateScheduler();
        int t1 = dasDataSynchronizer1.jobCount;
        TimeUnit.SECONDS.sleep(2);
        assertEquals(t1, dasDataSynchronizer1.jobCount);

        DataSyncConfiguration.getInstance().continueValidateScheduler();
        TimeUnit.SECONDS.sleep(2);
        assertTrue(dasDataSynchronizer1.jobCount > t1);
    }

    @Test
    public void testDataSyncConfigurationQueue() throws InterruptedException {
        DataSyncConfiguration configuration = DataSyncConfiguration.getInstance();
        configuration.enableSyncMode();

        TestSynchronizer dasDataSynchronizer1 = (TestSynchronizer)configuration.getDasDataSynchronizer("dbA");

        configuration.sendContext(new DataSyncContext("dbA").setData("CA"));
        configuration.sendContext(new DataSyncContext("dbB").setData("CA"));
        TimeUnit.SECONDS.sleep(1);
        assertEquals(2, dasDataSynchronizer1.syncCount);
    }
}
