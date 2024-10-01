package com.project;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.project.grpc.ServiceGrpc;
import com.project.grpc.RequestProxy;
import com.project.grpc.ResponseDB;

public class DataBase extends ServiceGrpc.ServiceImplBase {
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

    // main
    public static void main(String[] args) {
        int port = 50052;
        // start server
        try {
            Server db = ServerBuilder.forPort(port)
                    .addService(new DataBase())
                    .build()
                    .start();
            System.out.println("Base de datos iniciado en el puerto " + port);
            // keep running
            db.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
