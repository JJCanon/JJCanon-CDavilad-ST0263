package com.challenge;

import com.challenge.grpc.FileTransferGrpc;
import com.challenge.grpc.FileRequest;
import com.challenge.grpc.FileResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getIp(String message) {
        // Expresión regular para extraer la dirección IP
        String regex = "(\\d+\\.\\d+\\.\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String ipAddress = matcher.group(1);
            return ipAddress;
        } else
            return "";
    }

    public String transferFileTracker(String fileName) {
        FileRequest request = FileRequest.newBuilder().setFileName(fileName).build();
        FileResponse response = blockingStub.transferFile(request);
        String message = response.getMessage();
        System.out.println("Response from Tracker: " + message);
        return getIp(message);
    }

    public static void main(String[] args) throws InterruptedException {
        String fileName = "";
        System.out.println("Iniciando el cliente gRPC...");

        String ipTracker = "192.168.1.3";
        String ipServer = "";
        FileTransferClient clientT = new FileTransferClient("localhost", 50052);

        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Ingrese el nombre del archivo y el tipo de archivo a transferir:");
            fileName = scanner.next();
            ipServer = clientT.transferFileTracker(fileName);

        } finally {
            clientT.shutdown();
        }
        if (ipServer != "") {
            FileTransferClient client = new FileTransferClient("localhost", 50051);
            try {
                client.transferFile(fileName);
            } finally {
                client.shutdown();
            }
        } else
            System.out.println("Ip no encontrada");
    }
}