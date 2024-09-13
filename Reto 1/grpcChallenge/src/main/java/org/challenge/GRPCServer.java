package org.challenge;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GRPCServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreeterImpl())
                .build();

        server.start();
        System.out.println("Servidor gRPC iniciado en el puerto 50051");
        server.awaitTermination();
    }
}