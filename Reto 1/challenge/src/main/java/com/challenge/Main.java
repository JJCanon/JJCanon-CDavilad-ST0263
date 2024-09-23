package com.challenge;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando el servidor gRPC...");
        try {
            Server server = ServerBuilder.forPort(50051)
                    .addService(new FileTransferServer())
                    .build();

            server.start();
            System.out.println("Server started, listening on port 50051...");
            server.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}