import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.challenge.GrpcChallengeProto.HelloRequest;
import org.challenge.GrpcChallengeProto.HelloReply;

import java.util.Iterator;

public class GRPCclient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);

        // Prueba sayHello
        HelloRequest request = HelloRequest.newBuilder().setName("Mundo").build();
        HelloReply reply = stub.sayHello(request);
        System.out.println("Respuesta de sayHello: " + reply.getMessage());

        // Prueba sayHelloManyTimes
        Iterator<HelloReply> replies = stub.sayHelloManyTimes(request);
        while (replies.hasNext()) {
            System.out.println("Respuesta de sayHelloManyTimes: " + replies.next().getMessage());
        }

        channel.shutdown();
    }
}