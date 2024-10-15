package com.project;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.project.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DataBase2 extends ServiceGrpc.ServiceImplBase {

    // servers to receive request
    public Server server;

    // Ports
    // private final int PORT = 50061;

    private boolean leader;
    private final List<String> peersIps;
    private int[] ports = { 50061, 50062, 50063 };
    private int myPort;

    /**
     * "167.0.183.98",
     * "34.231.49.169",
     * "23.23.66.104"
     */
    // Constructor
    public DataBase2(int port) {
        this.leader = false;
        this.peersIps = new ArrayList<>(Arrays.asList(
                "localhost",
                "localhost"));
        myPort = port;
        deleteMyPort();
        // peersIps.remove(getLocalIpAddress());
        defineServers();
        try {
            startsServers();
        } catch (IOException e) {
            System.err.println("error iniciando los servers");
        }
    }

    private void deleteMyPort() {
        int[] newPorts = new int[ports.length - 1];
        int index = 0;
        for (int i = 0; i < ports.length; i++) {
            if (ports[i] != myPort) {
                newPorts[index] = ports[i];
                System.out.println(ports[i]);
                index++;
            }
        }
        ports = newPorts;
    }

    // to get Ip Address
    private String getLocalIpAddress() {
        String publicIp = "IP desconocida";
        try {
            URL url = new URL("http://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            publicIp = br.readLine(); // Lee la IP pública del servicio
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicIp;
    }

    // to defines servers
    private void defineServers() {
        this.server = ServerBuilder.forPort(myPort)
                .addService(this).build();
    }

    // to starts servers
    private void startsServers() throws IOException {
        this.server.start();
        System.out.println("Server started, listening on port " + myPort);
    }

    /**
     * basic functions in a Data Base
     */
    // write in file through rpc
    @Override
    public void writeDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        String message = "Hola Proxy, Escribir";
        if (!leader)
            message = "No soy el lider";
        System.out.println(message);
        try {
            followerCall();
            ResponseDB response = ResponseDB.newBuilder()
                    .setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // write in file through rpc
    @Override
    public void readDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        System.out.println("llamada");
        String data = request.getRequestProxy();
        System.out.println(data);
        String message = "Hola Proxy, Leer";
        System.out.println(message);
        try {
            ResponseDB response = ResponseDB.newBuilder()
                    .setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // to call write in followers through rpc
    @Override
    public void follower(RequestLeader request, StreamObserver<ResponseFollower> responseObserver) {
        System.out.println("llamada");
        String data = request.getLeaderRequest();
        System.out.println(data);
        String message = "Hola Leader, Follower escribió";
        try {
            ResponseFollower response = ResponseFollower.newBuilder()
                    .setFollowerResponse(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish conection
            responseObserver.onCompleted();
        }
    }

    // to become Leader
    @Override
    public void youreLeader(ProxyRequestL request, StreamObserver<DBResponseL> responseObserver) {
        System.out.println("llamada");
        String data = request.getRequestToLeader();
        leader = request.getLeader();
        System.out.println(data + " " + leader);
        try {
            String message = "soy el lider";
            System.out.println(message);
            DBResponseL response = DBResponseL.newBuilder()
                    .setDbResponseL(message)
                    .build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish conection
            responseObserver.onCompleted();
        }
    }

    // Heartbeat
    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        System.out.println("llamada");
        System.out.println(request.getBeat());
        try {
            HeartbeatResponse response = HeartbeatResponse.newBuilder()
                    .setBeat(true).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    // to make call from leader
    private void followerCall() {
        int indexPort = 0;
        for (String peerIp : peersIps) {
            System.out.println(peerIp + ":" + ports[indexPort]);
            ManagedChannel channel = ManagedChannelBuilder.forAddress(peerIp, ports[indexPort])
                    .usePlaintext()
                    .build();
            ServiceGrpc.ServiceBlockingStub blockingStub = ServiceGrpc.newBlockingStub(channel);
            indexPort++;
            if (indexPort == ports.length) {
                indexPort = 0;
            }
            String data = "Follower, escribe";
            RequestLeader request = RequestLeader.newBuilder()
                    .setLeaderRequest(data).build();
            try {
                ResponseFollower response = blockingStub.follower(request);
                System.out.println("Response from other data base: " + response.getFollowerResponse());
            } catch (Exception e) {
                System.err.println("Error escribiendo en DataBase: " + peerIp);
            } finally {
                channel.shutdown();
            }
        }
    }
}
/*
 * public static void main(String[] args) {
 * DataBase2 database = new DataBase2();
 * try {
 * database.server.awaitTermination();
 * } catch (Exception e) {
 * System.err.println("Error iniciando la base de datos: " + e.getMessage());
 * e.printStackTrace();
 * }
 * }
 */
