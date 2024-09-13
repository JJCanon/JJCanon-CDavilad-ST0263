package org.challenge;

import io.grpc.stub.StreamObserver;
import org.challenge.GrpcChallengeProto.HelloRequest;
import org.challenge.GrpcChallengeProto.HelloReply;

public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hola, " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void sayHelloManyTimes(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        for (int i = 0; i < 5; i++) {
            HelloReply reply = HelloReply.newBuilder()
                    .setMessage("Hola " + req.getName() + ", respuesta #" + (i + 1))
                    .build();
            responseObserver.onNext(reply);
        }
        responseObserver.onCompleted();
    }
}