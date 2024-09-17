package com.challenge;

import com.challenge.grpc.FileTransferGrpc;
import com.challenge.grpc.FileRequest;
import com.challenge.grpc.FileResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FileTransferClient {
    private final ManagedChannel channel;
    private final FileTransferGrpc.FileTransferBlockingStub blockingStub;

    public FileTransferClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = FileTransferGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void transferFile(String fileName) {
        FileRequest request = FileRequest.newBuilder().setFileName(fileName).build();
        FileResponse response = blockingStub.transferFile(request);
        System.out.println("Response from server: " + response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando el cliente gRPC...");
        FileTransferClient client = new FileTransferClient("localhost", 50051);
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese el nombre del archivo y el tipo de archivo a transferir:");
            String fileName = scanner.next();
            client.transferFile(fileName);
        } finally {
            client.shutdown();
        }
    }
}