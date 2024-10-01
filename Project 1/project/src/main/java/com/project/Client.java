package com.project;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.project.grpc.ServiceGrpc;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.project.grpc.RequestClient;
import com.project.grpc.ResponseProxy;

public class Client {
    private final ManagedChannel channel;
    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    // Constructor
    public Client(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = ServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // Write
    public void write(String data) {
        RequestClient request = RequestClient.newBuilder().setRequestClient(data).build();
        ResponseProxy response = blockingStub.write(request);
        System.out.println("Response from proxy: " + response.getResponseProxy());
    }

    // read
    public void read(String data) {
        RequestClient request = RequestClient.newBuilder().setRequestClient(data).build();
        ResponseProxy response = blockingStub.read(request);
        System.out.println("Response from proxy: " + response.getResponseProxy());
    }

    // main
    public static void main(String[] args) {
        String option = "";
        System.out.println("Iniciando cliente...");

        String ipProxy = "localhost";
        int port = 50051;
        Client client = new Client(ipProxy, port);
        Scanner scanner = new Scanner(System.in);
        try {
            boolean exit = false;
            while (!exit) {
                System.out.println(
                        "¿Qué desea realizar? Responde con el número de la opción: \n 1.Escribir \n 2.Leer \n 3.Salir");
                option = scanner.next();
                switch (option) {
                    case "1":
                        String dataWrite = "Hola Proxy, Escribir";
                        // write grpc
                        client.write(dataWrite);
                        break;
                    case "2":
                        String dataRead = "Hola Proxy, Leer";
                        // read grpc
                        client.read(dataRead);
                        break;
                    default:
                        exit = true;
                        break;
                }
            }
        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException e) {
                System.err.println("Error al cerrar el cliente: " + e.getMessage());
                e.printStackTrace();
            }
            scanner.close();
        }
    }
}
