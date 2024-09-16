package com.challenge;

import com.challenge.grpc.FileTransferGrpc;
import com.challenge.grpc.FileRequest;
import com.challenge.grpc.FileResponse;
import io.grpc.stub.StreamObserver;

public class FileTransferServer extends FileTransferGrpc.FileTransferImplBase {
    @Override
    public void transferFile(FileRequest request, StreamObserver<FileResponse> responseObserver) {
        String fileName = request.getFileName();
        String message = "Enviando archivo '" + fileName + "'";

        FileResponse response = FileResponse.newBuilder()
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        System.out.println(message);
    }
}