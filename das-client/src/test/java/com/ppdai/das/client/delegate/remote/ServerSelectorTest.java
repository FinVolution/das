package com.ppdai.das.client.delegate.remote;

import com.ppdai.das.core.configure.DasServerInstance;
import com.ppdai.das.service.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import static org.junit.Assert.fail;

public class ServerSelectorTest {
    private static TThreadedSelectorServer ttServer;
    private static int port;//ephemeral port

    private static DasServerInstance AVAILABLE_INSTANCE_1;
    private static DasServerInstance AVAILABLE_INSTANCE_2;
    private static DasServerInstance NO_AVAILABLE_INSTANCE_1;
    private static DasServerInstance NO_AVAILABLE_INSTANCE_2;

    private static final DasRequest REQUEST = new DasRequest()
            .setAppId("id").setLogicDbName("db").setDasClientVersion("v").setPpdaiClientVersion("pv").setOperation(DasOperation.Select);

    @BeforeClass
    public static void startServer() throws InterruptedException {
        Executors.newSingleThreadExecutor().submit(() -> {
            TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
            DasService.Processor<DasService.Iface> processor = new DasService.Processor<>(new MockDasServer());
            TNonblockingServerSocket nonblockingServerSocket = null;
            try {
                nonblockingServerSocket = new TNonblockingServerSocket(0);
            } catch (TTransportException e) {
                e.printStackTrace();
            }
            port = nonblockingServerSocket.getPort();

            ttServer = new TThreadedSelectorServer(
                    new TThreadedSelectorServer.Args(nonblockingServerSocket)
                            .processor(processor)
                            .protocolFactory(protocolFactory));

            AVAILABLE_INSTANCE_1 = new DasServerInstance("localhost", port);
            AVAILABLE_INSTANCE_2 = new DasServerInstance("127.0.0.1", port);
            NO_AVAILABLE_INSTANCE_1 = new DasServerInstance("UNKNOWN1", port);
            NO_AVAILABLE_INSTANCE_2 = new DasServerInstance("UNKNOWN2", port);

            ttServer.serve();
        });
        TimeUnit.SECONDS.sleep(2);
    }

    @AfterClass
    public static void shutdownServer(){
        ttServer.stop();
    }

    @Test
    public void testBasicCase() throws TException {
        ServerSelector serverSelector = new ServerSelector(
                "appId", Arrays.asList(AVAILABLE_INSTANCE_1, AVAILABLE_INSTANCE_2), "dasClientVersion", "ppdaiClientVersion", "clientAddress"
        );

        DasResult response = serverSelector.execute(REQUEST);
        Assert.assertNotNull(response);
    }

    @Test
    public void testRecover() throws TException, InterruptedException {
        ServerSelector serverSelector = new ServerSelector(
                "appId", Arrays.asList(NO_AVAILABLE_INSTANCE_1, NO_AVAILABLE_INSTANCE_2), "dasClientVersion", "ppdaiClientVersion", "clientAddress"
        );
        try {
             serverSelector.execute(REQUEST);
             fail();
        } catch (Exception e){
            //expected
        }
        serverSelector.serverInstances.add(AVAILABLE_INSTANCE_1);
        TimeUnit.SECONDS.sleep(ServerSelector.checkInterval + 1);//Wait for heart beat get correct instance
        DasResult response = serverSelector.execute(REQUEST);
        Assert.assertNotNull(response);
    }

    @Test
    public void testAlwaysChooseAvailableServer() throws TException {
        ServerSelector serverSelector = new ServerSelector(
                "appId", Arrays.asList(AVAILABLE_INSTANCE_1, NO_AVAILABLE_INSTANCE_1), "dasClientVersion", "ppdaiClientVersion", "clientAddress"
        );

        for (int i = 0 ;i < 100; i++){
            DasResult response = serverSelector.execute(REQUEST);
            Assert.assertNotNull(response);
        }
    }

    @Test
    public void testTransaction() throws TException {
        ServerSelector serverSelector = new ServerSelector(
                "appId", Arrays.asList(AVAILABLE_INSTANCE_1), "dasClientVersion", "ppdaiClientVersion", "clientAddress"
        );

         serverSelector.stickServerMode();
         DasTransactionId id = serverSelector.start("id", "db", new DasHints().setHints(new HashMap<>()));
         Assert.assertNotNull(id);
         serverSelector.commit(id);
    }

    @Test(expected = RuntimeException.class)
    public void testNoAvailableInstance() throws TException {
        ServerSelector serverSelector = new ServerSelector(
                "appId", Arrays.asList(NO_AVAILABLE_INSTANCE_1, NO_AVAILABLE_INSTANCE_2), "dasClientVersion", "ppdaiClientVersion", "clientAddress"
        );
        serverSelector.execute(REQUEST);
        fail();
    }

    static class MockDasServer implements DasService.Iface {
        @Override
        public DasResult execute(DasRequest request) throws DasException, TException {
            return new DasResult();
        }

        @Override
        public DasTransactionId start(String appId, String database, DasHints hints) throws DasException, TException {
            return new DasTransactionId()
                    .setLogicDbName("ldb")
                    .setClientAddress("localhost")
                    .setServerAddress("localhost")
                    .setCreateTime(new Date().getTime())
                    .setSequenceNumber(123L)
                    .setPhysicalDbName("phy")
                    .setLevel(0)
                    .setRolledBack(false)
                    .setCompleted(false);
        }

        @Override
        public void commit(DasTransactionId tranId) throws DasException, TException {

        }

        @Override
        public void rollback(DasTransactionId tranId) throws DasException, TException {

        }

        @Override
        public DasServerStatus check(DasCheckRequest request) throws DasException, TException {
            return new DasServerStatus().setOnline(true);
        }
    }
}
