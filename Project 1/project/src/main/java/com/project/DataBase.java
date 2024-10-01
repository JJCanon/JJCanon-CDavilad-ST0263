package com.project;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.project.grpc.ServiceGrpc;
import com.project.grpc.RequestProxy;
import com.project.grpc.ResponseDB;
//interdatabase
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import com.project.grpc.DataBaseRequest;
import com.project.grpc.DataBaseResponse;

public class DataBase extends ServiceGrpc.ServiceImplBase {

    private final ManagedChannel channel;
    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    private boolean leader;

    // to Write in txt
    @Override
    public void writeDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Escribir";
            System.out.println(message);
            ResponseDB response = ResponseDB.newBuilder().setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // to read from txt
    @Override
    public void readDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Leer";
            System.out.println(message);
            ResponseDB response = ResponseDB.newBuilder().setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // gRPC InterDatabase Comunication
    @Override
    public void interDataBaseComunication(DataBaseRequest request, StreamObserver<DataBaseResponse> responseObserver) {
        String data = request.getDbRequest();
        System.out.println(data);
        try {
            String message = "Hola database, soy otra database tambien";
            DataBaseResponse response = DataBaseResponse.newBuilder().setDbResponse(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish conection
            responseObserver.onCompleted();
        }
    }

    // get leader
    public boolean getLeader() {
        return leader;
    }

    // constructor
    public DataBase(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = ServiceGrpc.newBlockingStub(channel);
    }

    public DataBase() {
        this("localhost", 50053);
    }

    // make grpc to other database
    public void interDataBaseComunicationCall() {
        String data = "Hola database, soy otra database";
        DataBaseRequest request = DataBaseRequest.newBuilder().setDbRequest(data).build();
        DataBaseResponse response = blockingStub.interDataBaseComunication(request);
        System.out.println("Response from other data base: " + response.getDbResponse());
    }

    // shutdown
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // main
    public static void main(String[] args) {
        int port = 50052;
        int portPeer = 50053;
        // start server
        try {
            Server db = ServerBuilder.forPort(port)
                    .addService(new DataBase())
                    .build()
                    .start();
            Server peer = ServerBuilder.forPort(portPeer)
                    .addService(new DataBase())
                    .build()
                    .start();
            System.out.println("Base de datos iniciado en el puerto " + port);
            // keep running
            db.awaitTermination();
            peer.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
