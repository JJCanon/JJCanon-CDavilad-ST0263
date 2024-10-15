package com.project;

//server
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import com.project.grpc.ServiceGrpc;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.project.ProxyServer;
import com.project.grpc.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ProxyServer2 extends ServiceGrpc.ServiceImplBase {

    private Server proxy;
    // 167.0.183.98,23.23.66.104,34.231.49.169
    private final String[] dataBasesIps = { "localhost", "localhost", "localhost" };
    private int[] ports = { 50061, 50062, 50063 };
    private int dataBase;
    private int readindex;
    private String leaderIp;
    private int portLeader;
    private final int PORT_FOR_CLIENT = 50060;
    private final int PORT_TO_DB = 50061;

    // Constructor
    public ProxyServer2() {
        dataBase = 0;
        readindex = 0;
        this.proxy = ServerBuilder.forPort(PORT_FOR_CLIENT)
                .addService(this)
                .build();
        System.out.println("Asignando Lider");
        selectLeader();
        try {
            startServer();
        } catch (Exception e) {
            System.err.println("Error iniciando a escuchar el proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void selectLeader() {
        this.leaderIp = dataBasesIps[dataBase];
        this.portLeader = ports[dataBase];
        callYoureLeader();
        this.dataBase++;
        if (this.dataBase == dataBasesIps.length) {
            dataBase = 0;
        }
    }

    public void startServer() throws IOException {
        proxy.start();
        System.out.println("Proxy escuchando en el puerto " + PORT_FOR_CLIENT);
    }

    // to asign a database to leader
    public void callYoureLeader() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(leaderIp, portLeader)
                .usePlaintext().build();
        ServiceGrpc.ServiceBlockingStub stub = ServiceGrpc.newBlockingStub(channel);
        ProxyRequestL request = ProxyRequestL.newBuilder()
                .setRequestToLeader("eres el leader").setLeader(true).build();
        try {
            DBResponseL response = stub.youreLeader(request);
            System.out.println("Response from Leader: " + response.getDbResponseL());
        } catch (Exception e) {
            System.err.println("Database no contesta, eligiendo nuevo leader");
            channel.shutdown();
            selectLeader();
        }
        try {
            channel.shutdown();
        } catch (Exception e) {
            return;
        }
    }

    /**
     * this is part server
     */
    // recieve read from client
    @Override
    public void write(RequestClient request, StreamObserver<ResponseProxy> responseObserver) {
        String data = request.getRequestClient();
        System.out.println(data);
        String message = "Hola Cliente, Escribir";
        System.out.println(message);
        // connect with database
        System.out.println("Conectando con la base de datos...");
        try {
            String dataWrite = "Hola DataBase, Escribir";
            writeDB(dataWrite);
            ResponseProxy response = ResponseProxy.newBuilder().setResponseProxy(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call
            responseObserver.onCompleted();
        }
    }

    // recieve read from client
    @Override
    public void read(RequestClient request, StreamObserver<ResponseProxy> responseObserver) {
        String data = request.getRequestClient();
        System.out.println(data);
        String message = "Hola Cliente, Leer";
        System.out.println(message);
        // connect with database

        String dataRead = "Hola DataBase, Leer";
        try {
            readDB(dataRead);
            ResponseProxy response = ResponseProxy.newBuilder().setResponseProxy(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    /**
     * this is part client
     */
    // call write from database
    public void writeDB(String data) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(leaderIp, portLeader)
                .usePlaintext().build();
        ServiceGrpc.ServiceBlockingStub stub = ServiceGrpc.newBlockingStub(channel);
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        try {
            ResponseDB response = stub.writeDB(request);
            String messageDB = response.getResponseDB();
            System.out.println("Response from DB: " + messageDB);
        } catch (Exception e) {
            System.err.println("Error escribiendo en DataBase: " + leaderIp);
            channel.shutdown();
            selectLeader();
            writeDB(data);
        }
        try {
            channel.shutdown();
        } catch (Exception e) {
            return;
        }
    }

    // class read from database
    public void readDB(String data) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(dataBasesIps[readindex], ports[readindex])
                .usePlaintext().build();
        ServiceGrpc.ServiceBlockingStub stub = ServiceGrpc.newBlockingStub(channel);
        readindex++;
        if (readindex == dataBasesIps.length) {
            readindex = 0;
        }
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        try {
            ResponseDB response = stub.readDB(request);
            String messageDB = response.getResponseDB();
            System.out.println("Response from DB: " + messageDB);
        } catch (Exception e) {
            System.err
                    .println("Error leyendo en DataBase: " + dataBasesIps[readindex - 1] + ":" + ports[readindex - 1]);
            channel.shutdown();
            readDB(data);
        }
        try {
            channel.shutdown();
        } catch (Exception e) {
            return;
        }
    }

    // to call heartbeat
    public void callHeartbeat() {
        int portnum = 0;
        for (String databaseIp : dataBasesIps) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(databaseIp, ports[portnum])
                    .usePlaintext().build();
            ServiceGrpc.ServiceBlockingStub stub = ServiceGrpc.newBlockingStub(channel);
            HeartbeatRequest request = HeartbeatRequest.newBuilder()
                    .setBeat("heartbeat").build();
            portnum++;
            try {
                HeartbeatResponse response = stub.heartbeat(request);
                if (response.getBeat()) {
                    System.out.println("hearbeat true");
                }
            } catch (Exception e) {
                System.err.println("Heartbeat false");
                System.out.println("Database " + databaseIp + " ha fallado");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando el Proxy...");
        // start server
        ProxyServer2 proxyServer = new ProxyServer2();
        try {
            proxyServer.proxy.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando a escuchar el proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
