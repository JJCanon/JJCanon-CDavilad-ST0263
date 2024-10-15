
//server
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.project.grpc.ServiceGrpc;
import com.project.grpc.RequestClient;
import com.project.grpc.ResponseProxy;
//client
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.project.grpc.RequestProxy;
import com.project.grpc.ResponseDB;
//raft
import com.project.grpc.LeaderRequest;
import com.project.grpc.ProxyResponse;

public class ProxyServer extends ServiceGrpc.ServiceImplBase {

    private Server proxy, proxyDB;
    private ManagedChannel channel1, channel2, channel3;
    private ServiceGrpc.ServiceBlockingStub blockingStub1, blockingStub2, blockingStub3;

    private final String[] ipsDatabases = { "167.0.183.98", "23.23.66.104", "34.231.49.169" };
    private String leaderIp;

    private static final int PORT_FOR_CLIENT = 50060;
    private static final int PORT_TO_DB = 50061;
    private static final int PORT_FOR_DB = 50062;

    // constructor
    public ProxyServer() {
        leaderIp = "";
        startServers();
        initializeStubs();
        try {
            start();
            proxy.awaitTermination();
            proxyDB.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando a escuchar el proxy: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("esperando lider");
        while (leaderIp == "") {
        }
        System.out.println("Lider asignado");

    }

    private void startServers() {
        this.proxy = ServerBuilder.forPort(PORT_FOR_CLIENT)
                .addService(this)
                .build();
        this.proxyDB = ServerBuilder.forPort(PORT_FOR_DB)
                .addService(this)
                .build();
    }

    private void initializeStubs() {
        this.channel1 = ManagedChannelBuilder.forAddress(ipsDatabases[0], PORT_TO_DB)
                .usePlaintext()
                .build();
        this.channel2 = ManagedChannelBuilder.forAddress(ipsDatabases[1], PORT_TO_DB + 3)
                .usePlaintext()
                .build();
        this.channel3 = ManagedChannelBuilder.forAddress(ipsDatabases[2], PORT_TO_DB + 4)
                .usePlaintext()
                .build();
        this.blockingStub1 = ServiceGrpc.newBlockingStub(this.channel1);
        this.blockingStub2 = ServiceGrpc.newBlockingStub(this.channel2);
        this.blockingStub3 = ServiceGrpc.newBlockingStub(this.channel3);
    }

    public void start() throws IOException {
        proxy.start();
        proxyDB.start();
        System.out.println("Proxy escuchando en el puerto " + PORT_FOR_CLIENT + " y " + PORT_FOR_DB);
    }

    /**
     * this is part server
     */
    // recieve read from client
    @Override
    public void write(RequestClient request, StreamObserver<ResponseProxy> responseObserver) {
        String data = request.getRequestClient();
        System.out.println(data);
        String message = "Hola Cliente, Escribir";
        System.out.println(message);
        // connect with database
        System.out.println("Conectando con la base de datos...");
        try {
            String dataWrite = "Hola DataBase, Escribir";
            writeDB(dataWrite);
            ResponseProxy response = ResponseProxy.newBuilder().setResponseProxy(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call
            responseObserver.onCompleted();
        }
    }

    // recieve read from client
    @Override
    public void read(RequestClient request, StreamObserver<ResponseProxy> responseObserver) {
        String data = request.getRequestClient();
        System.out.println(data);
        String message = "Hola Cliente, Leer";
        System.out.println(message);
        // connect with database

        String dataRead = "Hola DataBase, Leer";
        try {
            readDB(dataRead);
            ResponseProxy response = ResponseProxy.newBuilder().setResponseProxy(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void imLeader(LeaderRequest request, StreamObserver<ProxyResponse> responseObserver) {
        leaderIp = request.getLeaderRequest();
        System.out.println("Nuevo Lider: " + leaderIp);
        try {
            String message = "eres el lider";
            ProxyResponse response = ProxyResponse.newBuilder().setProxyResponse(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish conection
            responseObserver.onCompleted();
        }

    }

    /**
     * this is part client
     */

    // shutdown
    public void shutdown() throws InterruptedException {
        this.channel1.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        this.channel2.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        this.channel3.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // call write from database
    public void writeDB(String data) {
        if (blockingStub1 == null) {
            System.err.println("Error: blockingStub1 is not initialized");
            return;
        }
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        ResponseDB response = blockingStub1.writeDB(request);
        String messageDB = response.getResponseDB();
        System.out.println("Response from DB: " + messageDB);
    }

    // class read from database
    public void readDB(String data) {
        if (blockingStub1 == null) {
            System.err.println("Error: blockingStub1 is not initialized");
            return;
        }
        RequestProxy request = RequestProxy.newBuilder().setRequestProxy(data).build();
        ResponseDB response = blockingStub1.readDB(request);
        String messageDB = response.getResponseDB();
        System.out.println("Response from DB: " + messageDB);
    }

    // main
    public static void main(String[] args) {
        System.out.println("Iniciando el Proxy...");
        // start server
        try {
            ProxyServer proxyServer = new ProxyServer();
        } catch (Exception e) {
            System.err.println("Error iniciando a escuchar el proxy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            /*
             * try {
             * proxyServer.shutdown();
             * } catch (InterruptedException e) {
             * System.err.println("Error cerrando el proxy..." + e.getMessage());
             * }
             */
        }
    }

}
