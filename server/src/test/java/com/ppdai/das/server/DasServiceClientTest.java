package com.ppdai.das.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.ppdai.das.service.DasOperation;
import com.ppdai.das.service.DasRequest;
import com.ppdai.das.service.DasResult;
import com.ppdai.das.service.DasService;

public class DasServiceClientTest {
    public static void main(String[] args) {
        System.out.println("Start client");
        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 7911, 30000);
            TProtocol protocol = new TBinaryProtocol(transport);
            DasService.Client client = new DasService.Client(protocol);
            transport.open();
            DasRequest request = new DasRequest();
            request.setLogicDbName("lovely");
            request.setOperation(DasOperation.Update);
            DasResult result = client.execute(request);
            System.out.println("Result row count: " + result.rowCount);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }
}
