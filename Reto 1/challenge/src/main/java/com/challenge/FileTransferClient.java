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

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileTransferClient {
    private final ManagedChannel channel;
    private final FileTransferGrpc.FileTransferBlockingStub blockingStub;

    public FileTransferClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = FileTransferGrpc.newBlockingStub(channel);
    }

    public String getLocalIpAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "IP desconocida";
        }
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

    // Método para subir archivo en el cliente
    public void uploadFile(String fileName) {
        // Obtener la IP local del cliente
        String clientIp = getLocalIpAddress();
        System.out.println("La IP del cliente es: " + clientIp);
        FileRequest request = FileRequest.newBuilder().setFileName(fileName).setClientIp(clientIp).build();

        // Registrar el archivo en el tracker (metadataTracker.json)
        System.out.println("Registrando archivo en el tracker...");
        FileResponse trackerResponse = blockingStub.uploadFile(request);
        System.out.println("Respuesta del tracker: " + trackerResponse.getMessage());

        // Luego registrar en el servidor (metadata.json)
        FileTransferClient serverClient = new FileTransferClient(clientIp, 50051);
        try {
            System.out.println("Registrando archivo en el servidor...");
            FileResponse serverResponse = serverClient.blockingStub.uploadFile(request);
            System.out.println("Respuesta del servidor: " + serverResponse.getMessage());
        } finally {
            try {
                serverClient.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String fileName = "";
        String option = "";
        System.out.println("Iniciando el cliente gRPC...");

        String ipTracker = "localhost";
        String ipServer = "localhost";
        FileTransferClient clientT = new FileTransferClient(ipTracker, 50052);
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println(
                    "¿Qué acción deseas realizar? Responde con el número de la opción: \n 1. Descargar Archivo \n 2. Subir Archivo");
            option = scanner.next();
            switch (option) {
                case "1":
                    System.out.println(
                            "Ingrese el nombre del archivo y el tipo de archivo a transferir (i.e: hola.pdf):");
                    fileName = scanner.next();
                    ipServer = clientT.transferFileTracker(fileName);
                    System.out.println("Termina");
                    scanner.close();
                    break;
                case "2":
                    System.out.println("Ingrese el nombre del archivo que desea subir (i.e: nuevo_archivo.txt):");
                    fileName = scanner.next();
                    // Subir el archivo al tracker
                    clientT.uploadFile(fileName); // Reemplaza 'localhost' con la IP correcta si es necesario
                    scanner.close();
                    break;
                default:
                    break;
            }

        } finally {
            clientT.shutdown();
        }
        if (ipServer != "") {
            System.out.println("conectando con el server");
            FileTransferClient client = new FileTransferClient(ipServer, 50051);
            try {
                client.transferFile(fileName);
            } finally {
                client.shutdown();
            }
        } else
            System.out.println("Ip no encontrada");
    }
}