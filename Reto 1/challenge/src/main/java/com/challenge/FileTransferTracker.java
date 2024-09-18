package com.challenge;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.challenge.grpc.FileTransferGrpc;
import com.challenge.grpc.FileRequest;
import com.challenge.grpc.FileResponse;
import io.grpc.stub.StreamObserver;

public class FileTransferTracker extends FileTransferGrpc.FileTransferImplBase {

    public String metadataFile(String fileName) {
        String jsonFilePath = "src/main/java/com/challenge/metadataTracker.json";
        try {
            // Leer el archivo JSON
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject json = new JSONObject(content);
            // Obtener metadata del archivo
            if (json.has(fileName)) {
                return "Enviando direccion del archivo " + json.getString(fileName);
            } else {
                return "File not Found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error trying to read json file");
            return "A Ocurrido un error, intente nuevamente";
        }
    }

    @Override
    public void transferFile(FileRequest request, StreamObserver<FileResponse> responseObserver) {
        String fileName = request.getFileName();
        String message = metadataFile(fileName); // Obtener metadata del archivo
        FileResponse response = FileResponse.newBuilder()
                .setMessage(message) // Incluir la dirección en la respuesta
                .build();
        responseObserver.onNext(response); // Enviar respuesta al cliente
        responseObserver.onCompleted();
        System.out.println(message);
    }

    public static void main(String[] args) {
        System.out.println("Iniciando el tracker gRPC...");
        try {
            // Crear el servidor y agregar el servicio
            Server tracker = ServerBuilder.forPort(50052)
                    .addService(new FileTransferTracker())
                    .build();
            // Iniciar el servidor
            tracker.start();
            System.out.println("Tracker started, listening on port 50052...");
            tracker.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando el tracker: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
