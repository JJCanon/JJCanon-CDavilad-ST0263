package com.project;

//Conections
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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.project.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DataBase extends ServiceGrpc.ServiceImplBase {

    // servers to receive request
    private Server serverForProxy, serverForPeers;

    // Ports
    private final int PORT_FOR_PROXY = 50061;
    private final int PORT_FOR_PEERS = 50063;
    private final int PORT_TO_PROXY = 50062;

    private final String PROXY_IP = "52.22.91.81";

    // states posibles
    private enum NodeState {
        FOLLOWER, CANDIDATE, LEADER
    }

    // states
    private NodeState state;
    private final String nodeIp;
    private final List<String> peersIps;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> electionTimer;
    private final Random random = new Random();

    // raft
    private int currentTerm;
    private String votedFor;
    private String leaderIp;
    private AtomicInteger voteCount;
    private int connectionFailed = 0;
    // heartbeats
    private ScheduledFuture<?> heartbeatTimer;

    // Constants for timeouts
    private static final int HEARTBEAT_INTERVAL = 50;
    private static final int MIN_ELECTION_TIMEOUT = 150;
    private static final int MAX_ELECTION_TIMEOUT = 300;

    // constructor
    public DataBase() {
        this.nodeIp = getLocalIpAddress();

        this.peersIps = new ArrayList<>(Arrays.asList(
                "167.0.183.98",
                "34.231.49.169",
                "23.23.66.104"));
        peersIps.remove(nodeIp);

        defineServers();
        try {
            startsServers();
        } catch (IOException e) {
            System.err.println("error iniciando los servers");
        }
        state = NodeState.FOLLOWER;
        votedFor = "";
        voteCount = new AtomicInteger(0);
        currentTerm = 0;
        System.out.println("Nodo " + nodeIp + " iniciado como Follower. Término actual: " + currentTerm);
        resetElectionTimer();
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
        this.serverForProxy = ServerBuilder.forPort(PORT_FOR_PROXY)
                .addService(this).build();
        this.serverForPeers = ServerBuilder.forPort(PORT_FOR_PEERS)
                .addService(this).build();
    }

    // to starts servers
    private void startsServers() throws IOException {
        this.serverForProxy.start();
        this.serverForPeers.start();
        System.out.println("Server started, listening on " +
                PORT_FOR_PROXY + " for proxy and " +
                PORT_FOR_PEERS + " for peers");
    }

    /**
     * raft
     */
    // to become Follower
    private void becomeFollower(int term) {
        state = NodeState.FOLLOWER;
        votedFor = "";
        voteCount.set(0);
        currentTerm = term;
        resetElectionTimer();
        System.out.println("Nodo " + nodeIp + " iniciado como Follower. Término actual: " + term);
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }
        resetElectionTimer();
    }

    // to become Candidate
    private void becomeCandidate() {
        state = NodeState.CANDIDATE;
        currentTerm++;
        votedFor = nodeIp;
        leaderIp = "";
        voteCount.set(1); // voto por si mismo
        System.out.println("Nodo " + nodeIp + " se convierte en Candidato. Nuevo término: " + currentTerm);
        System.out.println("Soy Candidate");
        resetElectionTimer();
    }

    // to become Leader
    private void becomeLeader() {
        if (state == NodeState.CANDIDATE) {
            state = NodeState.LEADER;
            leaderIp = nodeIp;
            if (electionTimer != null) {
                electionTimer.cancel(false);
            }
            System.out.println("Soy Leader");
            imLeaderCall();
            startHeartbeats();
        }
    }

    // to reset time for start election
    private void resetElectionTimer() {
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }
        int timeout = random.nextInt(MAX_ELECTION_TIMEOUT - MIN_ELECTION_TIMEOUT) + MAX_ELECTION_TIMEOUT;
        electionTimer = scheduler.schedule(this::startElection, timeout, TimeUnit.MILLISECONDS);
    }

    // to start election
    private void startElection() {
        System.out.println("start election");
        if (state == NodeState.FOLLOWER) {
            becomeCandidate();
            requestVotesFromPeers();
        }
    }

    private void requestVotesFromPeers() {
        if (state != NodeState.CANDIDATE) {
            return;
        } else if (peersIps.size() == 0) {
            becomeLeader();
            return;
        }
        int contPort = -2;
        for (String peerIp : peersIps) {
            if (state != NodeState.CANDIDATE) {
                break;
            }
            try {
                sendVoteRequest(peerIp, contPort);
                contPort++;
            } catch (Exception e) {
                System.out.println("Error al enviar solicitud de voto a " + peerIp);
                connectionFailed++;
            }
        }
        if (state == NodeState.CANDIDATE) {
            if (connectionFailed == peersIps.size()) {
                System.out.println("Todos los peers estan desconectados.");
                becomeLeader();
            } else if (voteCount.get() <= (peersIps.size() + 1) / 2) {
                becomeFollower(currentTerm);
            }
        }
        connectionFailed = 0;
    }

    // to call VoteRequest
    private void sendVoteRequest(String peerIp, int contPort) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(peerIp, PORT_FOR_PEERS + contPort)
                .usePlaintext()
                .build();
        ServiceGrpc.ServiceBlockingStub blockingStub = ServiceGrpc.newBlockingStub(channel);
        VoteRequest request = VoteRequest.newBuilder()
                .setTerm(currentTerm)
                .setCandidateIp(nodeIp)
                .build();
        try {
            VoteResponse response = blockingStub.requestVote(request);
            if (response.getVoteGranted()) {
                voteCount.incrementAndGet();
                if (voteCount.get() > (peersIps.size() + 1) / 2) {
                    becomeLeader();
                }
            } else if (response.getTerm() > currentTerm) {
                becomeFollower(response.getTerm());
            }
        } catch (Exception e) {
            System.out.println("Error al enviar solicitud de voto a " + peerIp + ": " + e.getMessage());
            connectionFailed++;
        } finally {
            channel.shutdown();
        }
    }

    // to response rpc votes request
    @Override
    public void requestVote(VoteRequest request, StreamObserver<VoteResponse> responseObserver) {
        int candidateTerm = request.getTerm();
        String candidateIp = request.getCandidateIp();
        VoteResponse response;
        if (candidateTerm > currentTerm) {
            becomeFollower(candidateTerm);
        }
        if (candidateTerm < currentTerm || (votedFor != "" && !votedFor.equals(candidateIp))) {
            response = VoteResponse.newBuilder()
                    .setTerm(currentTerm).setVoteGranted(false).build();
        } else {
            votedFor = candidateIp;
            response = VoteResponse.newBuilder()
                    .setTerm(candidateTerm)
                    .setVoteGranted(true).build();
            resetElectionTimer();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // to send to proxy im the leader
    private void imLeaderCall() {
        System.out.println("Notificando al Proxy");
        ManagedChannel channelForProxy = ManagedChannelBuilder.forAddress(PROXY_IP, PORT_TO_PROXY)
                .usePlaintext()
                .build();
        ServiceGrpc.ServiceBlockingStub blockingStubProxy = ServiceGrpc.newBlockingStub(channelForProxy);
        LeaderRequest request = LeaderRequest.newBuilder()
                .setLeaderRequest(nodeIp).build();
        try {
            ProxyResponse response = blockingStubProxy.imLeader(request);
            System.out.println("Reponse from proxy: " + response.getProxyResponse());
        } catch (Exception e) {
            System.out.println("Error al notificar al Proxy: " + e.getMessage());
        } finally {
            channelForProxy.shutdown();
        }
    }

    /**
     * Heartbeats
     */
    // to start heartbeats
    private void startHeartbeats() {
        if (state == NodeState.LEADER) {
            stopHeartbeats();
            if (heartbeatTimer != null) {
                heartbeatTimer.cancel(false);
            }
            heartbeatTimer = scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, HEARTBEAT_INTERVAL,
                    TimeUnit.MILLISECONDS);
        }
    }

    // to stop heartbeats
    private void stopHeartbeats() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel(false);
            System.out.println("Heartbeats detenidos");
        }
    }

    // to send heartbeats to otherspeers through grpc
    private void sendHeartbeat() {
        if (state != NodeState.LEADER) {
            stopHeartbeats();
            return;
        }
        for (String peerIp : peersIps) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(peerIp, PORT_FOR_PEERS)
                    .usePlaintext()
                    .build();
            ServiceGrpc.ServiceBlockingStub blockingStub = ServiceGrpc.newBlockingStub(channel);
            AppendEntriesRequest request = AppendEntriesRequest.newBuilder()
                    .setTerm(currentTerm)
                    .setLeaderIp(nodeIp)
                    .build();
            try {
                AppendEntriesResponse response = blockingStub.appendEntries(request);
                if (!response.getSuccess() && response.getTerm() > currentTerm) {
                    becomeFollower(response.getTerm());
                }
            } catch (Exception e) {
                System.out.println("Error al enviar heartbeat a " + peerIp + ": " + e.getMessage());
            } finally {
                channel.shutdown();
            }
        }
    }

    // to response request rpc heartbeats
    @Override
    public void appendEntries(AppendEntriesRequest request, StreamObserver<AppendEntriesResponse> responseObserver) {
        int leaderTerm = request.getTerm();
        AppendEntriesResponse response;

        if (leaderTerm < currentTerm) {
            response = AppendEntriesResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setSuccess(false)
                    .build();
            System.out.println("heartbeat falso");
        } else {
            if (leaderTerm > currentTerm) {
                becomeFollower(leaderTerm);
            }
            System.out.println("Heartbeat");
            leaderIp = request.getLeaderIp();
            response = AppendEntriesResponse.newBuilder()
                    .setTerm(currentTerm)
                    .setSuccess(true)
                    .build();
            resetElectionTimer();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // to close scheduler
    public void shutdownScheduler() {
        if (electionTimer != null) {
            electionTimer.cancel(false);
        }
        scheduler.shutdown();
    }

    /**
     * basic functions in a Data Base
     */
    // write in file through rpc
    @Override
    public void writeDB(RequestProxy request, StreamObserver<ResponseDB> responseObserver) {
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Escribir";
            System.out.println(message);
            // add write in file
            // write in followers
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
        String data = request.getRequestProxy();
        System.out.println(data);
        try {
            String message = "Hola Proxy, Leer";
            System.out.println(message);
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
        String data = request.getLeaderRequest();
        System.out.println(data);
        try {
            String message = "Hola Leader, Follower escribió";
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

    // to make call from leader
    private void followerCall() {
        for (String peerIp : peersIps) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(peerIp, PORT_FOR_PEERS)
                    .usePlaintext()
                    .build();
            ServiceGrpc.ServiceBlockingStub blockingStub = ServiceGrpc.newBlockingStub(channel);
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

    public static void main(String[] args) {
        DataBase dataBase = new DataBase();
        try {
            dataBase.serverForProxy.awaitTermination();
            dataBase.serverForPeers.awaitTermination();
        } catch (Exception e) {
            System.err.println("Error iniciando la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                dataBase.shutdownScheduler();
            } catch (Exception e) {
                System.err.println("Error al cerrar la Database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
