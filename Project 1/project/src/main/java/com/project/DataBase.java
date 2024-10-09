package com.project;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.project.grpc.ServiceGrpc;
import com.project.grpc.RequestProxy;
import com.project.grpc.ResponseDB;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.project.grpc.DataBaseRequest;
import com.project.grpc.DataBaseResponse;

//Raft
import com.project.grpc.VoteRequest;
import com.project.grpc.VoteResponse;
import com.project.grpc.AppendEntriesRequest;
import com.project.grpc.AppendEntriesResponse;

public class DataBase extends ServiceGrpc.ServiceImplBase {

    private final ManagedChannel channel;
    private final ServiceGrpc.ServiceBlockingStub blockingStub;

    // Raft attributes
    private enum NodeState {
        FOLLOWER, CANDIDATE, LEADER
    }

    private NodeState state;
    private int currentTerm;
    private Integer votedFor;
    private List<String> log;
    private int commitIndex;
    private int lastApplied;

    private final int nodeId;
    private final List<Integer> peerIds;
    private final Random random;
    private final ScheduledExecutorService scheduler;

    private static final int ELECTION_TIMEOUT_MIN = 150;
    private static final int ELECTION_TIMEOUT_MAX = 300;

    // Constructors
    // principal Constructors
    public DataBase(String host, int port, int nodeId, List<Integer> peerIds) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = ServiceGrpc.newBlockingStub(channel);

        this.nodeId = nodeId;
        this.peerIds = peerIds;
        this.state = NodeState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = null;
        this.log = new ArrayList<>();
        this.commitIndex = -1;
        this.lastApplied = -1;
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);

        resetElectionTimeout();
    }

    // Secondary Constructor
    public DataBase(String host, int port) {
        this(host, port, 0, new ArrayList<>());
    }

    // Default Constructor
    public DataBase() {
        this("localhost", 50053);
    }

    // Raft
    private void resetElectionTimeout() {
        int timeout = ELECTION_TIMEOUT_MIN + random.nextInt(ELECTION_TIMEOUT_MAX - ELECTION_TIMEOUT_MIN);
        scheduler.schedule(this::startElection, timeout, TimeUnit.MILLISECONDS);
    }

    private void startElection() {
        if (state != NodeState.LEADER) {
            state = NodeState.CANDIDATE;
            currentTerm++;
            votedFor = nodeId;
            AtomicInteger votesReceived = new AtomicInteger(1);
            AtomicBoolean electionDecided = new AtomicBoolean(false);

            CountDownLatch latch = new CountDownLatch(peerIds.size());

            for (int peerId : peerIds) {
                new Thread(() -> {
                    try {
                        VoteRequest request = VoteRequest.newBuilder()
                                .setTerm(currentTerm)
                                .setCandidateId(nodeId)
                                .build();

                        // Aqui debo enviar la solicitud de voto al peer usando gRPC
                        VoteResponse response = blockingStub.requestVote(request);

                        if (response.getVoteGranted()) {
                            if (votesReceived.incrementAndGet() > (peerIds.size() + 1 / 2)) {
                                if (electionDecided.compareAndSet(false, true)) {
                                    becomeLeader();
                                }
                            }
                        } else if (response.getTerm() > currentTerm) {
                            currentTerm = response.getTerm();
                            state = NodeState.FOLLOWER;
                            votedFor = null;
                            electionDecided.set(true);
                        }
                    } catch (Exception e) {
                        System.out.println("Error requesting vote from peer " + peerId + ": " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }

                }).start();
            }
            try {
                latch.await(ELECTION_TIMEOUT_MAX, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (!electionDecided.get()) {
                state = NodeState.FOLLOWER;
                resetElectionTimeout();
            }
        }
    }

    private void becomeLeader() {
        state = NodeState.LEADER;
        System.out.println("Node " + nodeId + " became leader for term " + currentTerm);
        sendHeartbeat();
    }

    private void sendHeartbeat() {
        if (state != NodeState.LEADER) {
            return;
        }
        for (int peerId : peerIds) {

            new Thread(() -> {
                try {
                    // Aqui debo de enviar el heartbeat al peer usando gRPC
                    AppendEntriesRequest request = AppendEntriesRequest.newBuilder()
                            .setTerm(currentTerm)
                            .setLeaderId(nodeId)
                            .build();
                    AppendEntriesResponse response = blockingStub.appendEntries(request);
                    if (response.getSuccess()) {
                        // Heartbeat succesfully
                    } else if (response.getTerm() > currentTerm) {
                        currentTerm = response.getTerm();
                        state = NodeState.FOLLOWER;
                        votedFor = null;
                    }
                } catch (Exception e) {
                    System.out.println("Error sending heartbeat to peer " + peerId);
                }
            }).start();
        }
        scheduler.schedule(this::sendHeartbeat, 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void requestVote(VoteRequest request, StreamObserver<VoteResponse> responseObserver) {
        VoteResponse response;
        if (request.getTerm() > currentTerm) {
            currentTerm = request.getTerm();
            state = NodeState.FOLLOWER;
            votedFor = null;
        }
        if (request.getTerm() < currentTerm || (votedFor != null && votedFor != request.getCandidateId())) {
            response = VoteResponse.newBuilder().setTerm(currentTerm).setVoteGranted(false).build();
        } else {
            votedFor = request.getCandidateId();
            response = VoteResponse.newBuilder().setTerm(currentTerm).setVoteGranted(true).build();
            resetElectionTimeout();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver) {
        AppendEntriesResponse response;
        if (request.getTerm() < currentTerm) {
            response = AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(false).build();
        } else {
            if (request.getTerm() > currentTerm) {
                currentTerm = request.getTerm();
                state = NodeState.FOLLOWER;
                votedFor = null;
            }
            resetElectionTimeout();
            response = AppendEntriesResponse.newBuilder().setTerm(currentTerm).setSuccess(true).build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // to Write in txt with grpc
    @Override
    public void writeDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Escribir";
            System.out.println(message);
            int portDB = 50054;
            String ipDB = "localhost";
            DataBase clientDB = new DataBase(ipDB, portDB, nodeId + 1, peerIds);
            try {
                clientDB.interDataBaseComunicationCall();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientDB.shutdown();
                } catch (InterruptedException e) {
                    System.err.println("Error al cerrar el cliente: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            ResponseDB response = ResponseDB.newBuilder().setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // to read from txt with grpc
    @Override
    public void readDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Leer";
            System.out.println(message);
            ResponseDB response = ResponseDB.newBuilder().setResponseDB(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish call with proxy
            responseObserver.onCompleted();
        }
    }

    // gRPC InterDatabase Comunication
    @Override
    public void interDataBaseComunication(DataBaseRequest request, StreamObserver<DataBaseResponse> responseObserver) {
        String data = request.getDbRequest();
        System.out.println(data);
        try {
            String message = "Hola database, soy otra database tambien";
            DataBaseResponse response = DataBaseResponse.newBuilder().setDbResponse(message).build();
            responseObserver.onNext(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // to finish conection
            responseObserver.onCompleted();
        }
    }

    // make grpc to other database
    public void interDataBaseComunicationCall() {
        String data = "Hola database, soy otra database";
        DataBaseRequest request = DataBaseRequest.newBuilder().setDbRequest(data).build();
        DataBaseResponse response = blockingStub.interDataBaseComunication(request);
        System.out.println("Response from other data base: " + response.getDbResponse());
    }

    // shutdown
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // main
    public static void main(String[] args) {
        int port = 50052;
        int portPeer = 50053;
        List<Integer> peerIds = new ArrayList<>();
        peerIds.add(portPeer);
        // start server
        try {
            Server db = ServerBuilder.forPort(port)
                    .addService(new DataBase("localhost", port, 0, peerIds))
                    .build()
                    .start();
            Server peer = ServerBuilder.forPort(portPeer)
                    .addService(new DataBase("localhost", portPeer, 1, peerIds))
                    .build()
                    .start();
            System.out.println("Base de datos iniciado en el puerto " + port);
            System.out.println("Peer iniciado en el puerto " + portPeer);
            // keep running
            db.awaitTermination();
            peer.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
