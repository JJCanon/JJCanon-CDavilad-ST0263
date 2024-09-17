package com.challenge;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.challenge.grpc.FileTransferGrpc;
import com.challenge.grpc.FileRequest;
import com.challenge.grpc.FileResponse;
import io.grpc.stub.StreamObserver;

public class FileTransferServer extends FileTransferGrpc.FileTransferImplBase {
    public String metadataFile(String fileName) {
        String jsonFilePath = "src/main/java/com/challenge/metadata.json";
        try {
            // read json file
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject json = new JSONObject(content);
            // search if the file exist in the json
            if (json.has(fileName)) {
                return json.getString(fileName);
            } else {
                return "File not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error trying to read json file";
        }
    }

    @Override
    public void transferFile(FileRequest request, StreamObserver<FileResponse> responseObserver) {
        String fileName = metadataFile(request.getFileName());
        String message = "Enviando archivo '" + fileName + "'";

        FileResponse response = FileResponse.newBuilder()
                .setMessage(message)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        System.out.println(message);
    }
}