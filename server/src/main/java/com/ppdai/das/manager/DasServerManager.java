package com.ppdai.das.manager;

import com.google.common.collect.Iterables;
import com.ppdai.das.core.DasLogger;

import com.ppdai.das.core.LogContext;
import com.ppdai.das.core.ServerConfigureLoader;
import com.ppdai.das.core.helper.ServiceLoaderHelper;
import com.ppdai.das.server.DasServer;
import com.ppdai.das.service.DasCheckRequest;
import com.ppdai.das.service.DasServerStatus;
import com.ppdai.das.service.DasService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Each proxy servers in a host should be started by this manager.
 *
 * This manager does 2 things with scheduler:
 * 1. Startup servers in Apollo
 * 2. Shutdown servers not in Apollo
 */
public class DasServerManager {

    private static final Logger logger = LoggerFactory.getLogger(DasServerManager.class);
    private DasLogger dalLogger;
    private ServerConfigureLoader serverLoader;
    private final static String WORKING_DIR = System.getProperty("user.dir");
    private static String hostAddress;

    static {
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
    }

    public DasServerManager(ServerConfigureLoader serverLoader) throws Exception {
        this.serverLoader = serverLoader;
        this.dalLogger = serverLoader.getDasLogger();
    }

    public static void main(String[] args) throws Exception {
        int port = -1;
        ServerConfigureLoader serverLoader = ServiceLoaderHelper.getInstance(ServerConfigureLoader.class);
        if(args.length == 0) { //Read port from configuration
            logger.info("Server will start a port from configuration.");

            List<Integer> ports = serverLoader.getListeningPorts();
            if(ports.isEmpty()) {
                logger.error("No port is defined in configuration for host: [" + hostAddress+ "]");
                return;
            }
            port = Iterables.getFirst(ports, -1);
        } else { //Read port from command line, if port is given
            port = Integer.parseInt(args[0]);
        }

        DasLogger dalLogger = serverLoader.getDasLogger();
        try {
            logger.info("Server is starting: " + hostAddress + "@" + port);
            LogContext logContext = dalLogger.logTransaction("das-server start: "+ hostAddress +"@" + port, "start");
            dalLogger.completeTransaction(logContext, null);

            DasServer.startServer(port);
        }catch (Exception e){
            logger.error("Server quit: " + hostAddress + "@" + port, e);
            LogContext logContext = dalLogger.logTransaction("das-server quit: "+ hostAddress +"@" + port, "quit");
            dalLogger.completeTransaction(logContext, e);
        }finally {
            //Force exit process anyway
            System.exit(1);
        }
    }

    private void startup() {
        sendSuccessCatTransaction("das-manager-start@" + hostAddress, "start");
        startupChecker();
    }

    private void sendSuccessCatTransaction(String type, String name) {
        LogContext logContext = dalLogger.logTransaction(type, name);
        dalLogger.completeTransaction(logContext, null);
    }

    public void startServer(Integer port){
        logger.info("start server port: [" + port + "]");
        LogContext logContext = dalLogger.logTransaction("manager-startServer@"+ hostAddress + ":" +port, "start");
        String command = "sh server.sh " + port;
        Exception exception = null;
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            exception = e;
            logger.error(e.getMessage(), e);
        } finally {
            dalLogger.completeTransaction(logContext, exception);
        }
    }

    static class PortPid{
        String pid;
        int port;

        public PortPid(String pid, int port) {
            this.pid = pid;
            this.port = port;
        }
    }

    private void startupChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() ->{
            //Startup servers in Apollo
            List<Integer> allPorts = serverLoader.getListeningPorts();
            for(int port : allPorts){
               if(!isAvailable(port)){
                   startServer(port);
               }
            }

            //Shutdown servers not in Apollo
            List<PortPid> pids = readLocalPID();
            for(PortPid p: pids){
                if (!allPorts.contains(p.port)){
                    shutdownServer(p.pid, p.port);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void shutdownServer(String pid, int port) {
        logger.info("shutdown pid: [" + pid + "]");
        String command = "kill -9 " + pid;
        LogContext logContext = dalLogger.logTransaction("manager-shutdownServer@"+ hostAddress + ":" + port, "shutdown");
        Exception exception = null;
        try {
            Process process = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            exception = e;
            logger.error(e.getMessage(), e);
        } finally {
            dalLogger.completeTransaction(logContext, exception);
            try{
                File pidFile = Paths.get(WORKING_DIR, pid + ".pid").toFile();
                if(pidFile.exists()) {
                    pidFile.delete();
                }
            } catch (Exception e){
                //Ignore
            }
        }
    }

     List<PortPid> readLocalPID() {
        File f = Paths.get(WORKING_DIR).toFile();
        String[] pidFiles = f.list((dir, name) -> name.endsWith("pid"));
        return Arrays.stream(pidFiles).map(fi ->{
            try {
                String portInStr = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath(), fi))).trim();
                int port = Integer.parseInt(portInStr);
                return new PortPid(fi.replace(".pid", ""), port);
            } catch (IOException e) {
                dalLogger.error("readLocalPID", e);
                logger.warn(e.getMessage(), e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    private boolean isAvailable(int port){
        try (TSocket transport = new TSocket("localhost", port, 5 * 1000)){
            TFramedTransport ft = new TFramedTransport(transport);
            TBinaryProtocol protocol = new TBinaryProtocol(ft);
            transport.open();

            DasCheckRequest request = new DasCheckRequest()
                    .setAppId("server manager")
                    .setClientAddress("")
                    .setDasClientVersion("")
                    .setPpdaiClientVersion("");

            DasServerStatus serverStatus =  new DasService.Client(protocol).check(request);
            return serverStatus.isOnline();
        } catch (Exception e) {
            dalLogger.error("isAvailable", e);
            logger.error(e.getMessage(), e);
            return false;
        }
    }

}
