package com.ppdai.das.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ppdai.das.core.DasDiagnose;

/**
 * Please add description here.
 *
 * @author huangyinhuang
 * @date 8/6/2018
 */
public class DasDiagnoseTest {

    @Test
    public void TestDasDiagnose() {
        DasDiagnose dasDiagnose = new DasDiagnose("diagnose", 0);
        dasDiagnose.append("version", "1.15.1");
        dasDiagnose.append("entrance", "com.ppdai.das.client.DasDiagnoseTest.TestDasDiagnose()");

        dasDiagnose.append("totalShard", "2");
        for (int i = 0; i < 2; i++) {
            String shardName = "shard" + i;
            DasDiagnose shardDiagnose = dasDiagnose.spawn(shardName);
            shardDiagnose.append(shardName + ".totalTask", "2");

            for (int j = 2; j > 0; j--) {
                String taskName = "task" + j;
                DasDiagnose taskDiagnose = shardDiagnose.spawn(taskName);
                taskDiagnose.append(taskName + ".totalStatement", "3");

                for (int k = 0; k < 3; k++) {
                    String statementName = "statement" + k;
                    DasDiagnose statementDiagnose = taskDiagnose.spawn(statementName);

                    statementDiagnose.append(statementName + ".dbConnectString", "mysql://localhost:3306");
                    statementDiagnose.append(statementName + ".sql", "select * from db");
                    statementDiagnose.append(statementName + ".startTime", "2018-08-06 01:57");
                    statementDiagnose.append(statementName + ".endTime", "2018-08-06 02:57");
                    statementDiagnose.append(statementName + ".isSuccess", "false");
                    statementDiagnose.append(statementName + ".exception", new Exception("test"));
                    statementDiagnose.append(statementName + ".cost", "6000");
                }
            }
        }

        String info = dasDiagnose.toString();
        System.out.println(info);
    }

    @Test
    public void TestDasDiagnoseGenerate(){
        DasDiagnose dasDiagnose = new DasDiagnose("diagnose", 0);
        DasDiagnose subDiagnose = dasDiagnose.spawn("test");
        DasDiagnose parentDiagnose = subDiagnose.getParentDiagnose();
        assertNotNull(parentDiagnose);
        assertEquals(dasDiagnose, parentDiagnose);
    }
}
