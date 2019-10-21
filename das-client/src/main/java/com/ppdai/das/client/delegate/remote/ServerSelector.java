package com.ppdai.das.client.delegate.remote;

import com.google.common.cache.*;
import com.ppdai.das.core.DasServerInstance;
import com.ppdai.das.service.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ppdai.das.client.delegate.remote.DasServerInstanceWithStatus.asKey;


public class ServerSelector {

    private Rule rule;

    static long checkInterval = 5L;

    private String appId;
    private String dasClientVersion;
    private String ppdaiClientVersion;
    private String clientAddress;

    List<DasServerInstance> serverInstances = new ArrayList<>();
    protected static List<DasServerInstanceWithStatus> allServerList = new CopyOnWriteArrayList<>();
    private final static ThreadLocal<ClientObject> currentClient = new ThreadLocal<>();
    private static final LoadingCache<DasServerInstanceWithStatus, LoadingCache<String, ClientObject>> servers = createClient();
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean checkInProgress = new AtomicBoolean(false);

    public ServerSelector(String appId, List<DasServerInstance> serverInstances, String dasClientVersion, String ppdaiClientVersion, String clientAddress) {
        this.appId = appId;
        this.serverInstances.addAll(serverInstances);
        this.dasClientVersion = dasClientVersion;
        this.ppdaiClientVersion = ppdaiClientVersion;
        this.clientAddress = clientAddress;

        //Random by default
        rule = new RandomRule();
        rule.setServerSelector(this);

        startup();
    }

    static LoadingCache<DasServerInstanceWithStatus, LoadingCache<String, ClientObject>> createClient() {
        return CacheBuilder.newBuilder()
                .removalListener((RemovalListener<DasServerInstanceWithStatus, LoadingCache<String, ClientObject>>) notification -> {
                    //invalidate all ClientObject in the server
                    LoadingCache<String, ClientObject> toRemove = notification.getValue();
                    toRemove.invalidateAll();

                }).build(new CacheLoader<DasServerInstanceWithStatus, LoadingCache<String, ClientObject>>() {
                    @Override public LoadingCache<String, ClientObject> load(DasServerInstanceWithStatus server) throws Exception {
                        return CacheBuilder.newBuilder()
                                .removalListener((RemovalListener<String, ClientObject>) notification -> {
                                    ClientObject toRemove = notification.getValue();
                                    toRemove.close();

                                }).build(new CacheLoader<String, ClientObject>() {
                                    @Override public ClientObject load(String key) throws Exception {
                                        return getClientObject(server.getAddress(), server.getPort());
                                    }
                                });

                    }
                });
    }

    public DasResult execute(DasRequest dasRequest) throws TException {
        if (currentClient.get() != null) {//stick mode
            DasService.Client client = currentClient.get().getClient();
            return client.execute(dasRequest);
        } else {
            DasServerInstanceWithStatus chosen = rule.chooseServer();
            if (chosen == null) {
                throw new RuntimeException("No available server from: " + serverInstances);
            } else {
                try {
                    ClientObject co = servers.get(chosen).get(Thread.currentThread().getName());
                    return co.getClient().execute(dasRequest);
                } catch (Exception e){
                    try {
                        servers.get(chosen).invalidate(Thread.currentThread().getName());
                    } catch (ExecutionException ee) {}
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void commit(DasTransactionId transactionId) throws TException {
        try {
            currentClient.get().getClient().commit(transactionId);
        } finally {
            currentClient.get().close();
            currentClient.remove();
        }
    }

    public void rollback(DasTransactionId transactionId) throws TException {
        try{
            currentClient.get().getClient().rollback(transactionId);
        }finally {
            currentClient.get().close();
            currentClient.remove();
        }
    }

    public DasTransactionId start(String appId, String database, DasHints hints) throws TException {
        return currentClient.get().getClient().start(appId, database, hints);
    }

    public void stickServerMode() {
        DasServerInstanceWithStatus chosen = rule.chooseServer();
        if (chosen == null) {
            throw new RuntimeException("No available server from: " + serverInstances);
        } else {
               try{
                    currentClient.set(getClientObject(chosen.getAddress(), chosen.getPort()));
                } catch (TTransportException e) {
                   throw new RuntimeException(e);
                }
            }
    }

    static ClientObject getClientObject(String host, int port) throws TTransportException {
        TSocket transport = new TSocket(host, port);
        TFramedTransport ft = new TFramedTransport(transport);
        TBinaryProtocol protocol = new TBinaryProtocol(ft);
        transport.open();
        return new ClientObject(new DasService.Client(protocol), transport);
    }

    public void removeStick(){
        currentClient.remove();
    }

    static class ClientObject {
        DasService.Client client;
        TSocket transport;

        public ClientObject(DasService.Client client, TSocket transport) {
            this.client = client;
            this.transport = transport;
        }

        public DasService.Client getClient() {
            return client;
        }

        void close(){
            if(!transport.isOpen()){
                transport.close();
            }
        }
    }

    void checkServers() {
        if (!checkInProgress.compareAndSet(false, true)) {
            return; //check in progress, prevent concurrent invoke - nothing to do
        }
        try{
            List<DasServerInstanceWithStatus> newList = callCheckServers(serverInstances);

            allServerList.clear();
            allServerList.addAll(newList);
         }finally {
            checkInProgress.set(false);
        }
    }

    public void startup() {
        checkServers();
        if(!initialized.get()) {
            service.scheduleAtFixedRate(()-> checkServers(), 0, checkInterval, TimeUnit.SECONDS);
            initialized.set(true);
        }
    }

    private List<DasServerInstanceWithStatus> callCheckServers(List<DasServerInstance> serverInstances) {
        List<DasServerInstanceWithStatus> result = new ArrayList<>();
        for(DasServerInstance server : serverInstances){
            try{
             DasService.Client client = servers.get(asKey(server.getAddress(), server.getPort())).get(Thread.currentThread().getName()).getClient();
             DasCheckRequest request = new DasCheckRequest()
                        .setAppId(appId)
                        .setClientAddress(clientAddress)
                        .setDasClientVersion(dasClientVersion)
                        .setPpdaiClientVersion(ppdaiClientVersion);

                DasServerStatus status = client.check(request);
                result.add(new DasServerInstanceWithStatus(server.getAddress(), server.getPort(), status));
            } catch (Exception e) {
                DasServerInstanceWithStatus toInvalidate = asKey(server.getAddress(), server.getPort());
                servers.invalidate(toInvalidate);
                result.add(new DasServerInstanceWithStatus(server.getAddress(), server.getPort()));
            }
        }
        return result;
    }

    public List<DasServerInstanceWithStatus> getAllServers(){
        return allServerList;
    }

    static boolean isServerAvailable(DasServerInstanceWithStatus server) {
        return server.getStatus() != null && server.getStatus().isOnline();//TODO, We only check isOnline for now
    }
}
