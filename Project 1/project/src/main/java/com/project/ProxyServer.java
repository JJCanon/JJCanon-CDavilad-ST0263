package com.project;

//server
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

import com.project.grpc.ServiceGrpc;
import com.project.grpc.RequestClient;
import com.project.grpc.ResponseProxy;
//client
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.project.grpc.RequestProxy;
import com.project.grpc.ResponseDB;

public class ProxyServer extends ServiceGrpc.ServiceImplBase {

    private final ManagedChannel channel;
    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    /**
     * this is part server
     */
    // recieve read from client
    @Override
    public void write(RequestClient request, StreamObserver<ResponseProxy> responseObserver) {
        String data = request.getRequestClient();
        System.out.println(data);
        try {
            String message = "Hola Cliente, Escribir";
            System.out.println(message);
            // connect with database
            String ipDataBase = "localhost";
            int portDB = 50052;
            ProxyServer proxy = new ProxyServer(ipDataBase, portDB);
            try {
                System.out.println("Conectando con la base de datos...");
                String dataWrite = "Hola DataBase, Escribir";
                proxy.writeDB(dataWrite);
            } finally {
                proxy.shutdown();
            }
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
        try {
            String message = "Hola Cliente, Leer";
            System.out.println(message);
            // connect with database
            String ipDataBase = "localhost";
            int portDB = 50052;
            ProxyServer proxy = new ProxyServer(ipDataBase, portDB);
            try {
                System.out.println("Conectando con la base de datos...");
                String dataRead = "Hola DataBase, Leer";
                proxy.readDB(dataRead);
            } finally {
                proxy.shutdown();
            }
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
    // constructor
    public ProxyServer(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = ServiceGrpc.newBlockingStub(channel);
    }

    public ProxyServer() {
        this("localhost", 50052);
    }

    // shutdown
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // call write from database
    public void writeDB(String data) {
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        ResponseDB response = blockingStub.writeDB(request);
        String messageDB = response.getResponseDB();
        System.out.println("Response from DB: " + messageDB);
    }

    // class read from database
    public void readDB(String data) {
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        ResponseDB response = blockingStub.readDB(request);
        String messageDB = response.getResponseDB();
        System.out.println("Response from DB: " + messageDB);
    }

    // main
    public static void main(String[] args) {
        System.out.println("Iniciando el Proxy...");
        // Server
        // port
        int portProxy = 50051;
        // start server
        try {
            Server proxy = ServerBuilder.forPort(portProxy)
                    .addService(new ProxyServer())
                    .build();

            proxy.start();
            System.out.println("Proxy escuchando en el puerto " + portProxy);
            // keep running
            proxy.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando a escuchar el proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
