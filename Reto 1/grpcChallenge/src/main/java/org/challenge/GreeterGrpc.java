package org.challenge;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Servicio gRPC
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.56.0)",
    comments = "Source: service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GreeterGrpc {

  private GreeterGrpc() {}

  public static final String SERVICE_NAME = "org.challenge.Greeter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest,
      org.challenge.GrpcChallengeProto.HelloReply> getSayHelloMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SayHello",
      requestType = org.challenge.GrpcChallengeProto.HelloRequest.class,
      responseType = org.challenge.GrpcChallengeProto.HelloReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest,
      org.challenge.GrpcChallengeProto.HelloReply> getSayHelloMethod() {
    io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest, org.challenge.GrpcChallengeProto.HelloReply> getSayHelloMethod;
    if ((getSayHelloMethod = GreeterGrpc.getSayHelloMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getSayHelloMethod = GreeterGrpc.getSayHelloMethod) == null) {
          GreeterGrpc.getSayHelloMethod = getSayHelloMethod =
              io.grpc.MethodDescriptor.<org.challenge.GrpcChallengeProto.HelloRequest, org.challenge.GrpcChallengeProto.HelloReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SayHello"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.challenge.GrpcChallengeProto.HelloRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.challenge.GrpcChallengeProto.HelloReply.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("SayHello"))
              .build();
        }
      }
    }
    return getSayHelloMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest,
      org.challenge.GrpcChallengeProto.HelloReply> getSayHelloManyTimesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SayHelloManyTimes",
      requestType = org.challenge.GrpcChallengeProto.HelloRequest.class,
      responseType = org.challenge.GrpcChallengeProto.HelloReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest,
      org.challenge.GrpcChallengeProto.HelloReply> getSayHelloManyTimesMethod() {
    io.grpc.MethodDescriptor<org.challenge.GrpcChallengeProto.HelloRequest, org.challenge.GrpcChallengeProto.HelloReply> getSayHelloManyTimesMethod;
    if ((getSayHelloManyTimesMethod = GreeterGrpc.getSayHelloManyTimesMethod) == null) {
      synchronized (GreeterGrpc.class) {
        if ((getSayHelloManyTimesMethod = GreeterGrpc.getSayHelloManyTimesMethod) == null) {
          GreeterGrpc.getSayHelloManyTimesMethod = getSayHelloManyTimesMethod =
              io.grpc.MethodDescriptor.<org.challenge.GrpcChallengeProto.HelloRequest, org.challenge.GrpcChallengeProto.HelloReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SayHelloManyTimes"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.challenge.GrpcChallengeProto.HelloRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.challenge.GrpcChallengeProto.HelloReply.getDefaultInstance()))
              .setSchemaDescriptor(new GreeterMethodDescriptorSupplier("SayHelloManyTimes"))
              .build();
        }
      }
    }
    return getSayHelloManyTimesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GreeterStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterStub>() {
        @java.lang.Override
        public GreeterStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterStub(channel, callOptions);
        }
      };
    return GreeterStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GreeterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterBlockingStub>() {
        @java.lang.Override
        public GreeterBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterBlockingStub(channel, callOptions);
        }
      };
    return GreeterBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GreeterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GreeterFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GreeterFutureStub>() {
        @java.lang.Override
        public GreeterFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GreeterFutureStub(channel, callOptions);
        }
      };
    return GreeterFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Servicio gRPC
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Método simple de gRPC para enviar un saludo
     * </pre>
     */
    default void sayHello(org.challenge.GrpcChallengeProto.HelloRequest request,
        io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSayHelloMethod(), responseObserver);
    }

    /**
     * <pre>
     * Método para enviar múltiples saludos
     * </pre>
     */
    default void sayHelloManyTimes(org.challenge.GrpcChallengeProto.HelloRequest request,
        io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSayHelloManyTimesMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Greeter.
   * <pre>
   * Servicio gRPC
   * </pre>
   */
  public static abstract class GreeterImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GreeterGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Greeter.
   * <pre>
   * Servicio gRPC
   * </pre>
   */
  public static final class GreeterStub
      extends io.grpc.stub.AbstractAsyncStub<GreeterStub> {
    private GreeterStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterStub(channel, callOptions);
    }

    /**
     * <pre>
     * Método simple de gRPC para enviar un saludo
     * </pre>
     */
    public void sayHello(org.challenge.GrpcChallengeProto.HelloRequest request,
        io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSayHelloMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Método para enviar múltiples saludos
     * </pre>
     */
    public void sayHelloManyTimes(org.challenge.GrpcChallengeProto.HelloRequest request,
        io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSayHelloManyTimesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Greeter.
   * <pre>
   * Servicio gRPC
   * </pre>
   */
  public static final class GreeterBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GreeterBlockingStub> {
    private GreeterBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Método simple de gRPC para enviar un saludo
     * </pre>
     */
    public org.challenge.GrpcChallengeProto.HelloReply sayHello(org.challenge.GrpcChallengeProto.HelloRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSayHelloMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Método para enviar múltiples saludos
     * </pre>
     */
    public java.util.Iterator<org.challenge.GrpcChallengeProto.HelloReply> sayHelloManyTimes(
        org.challenge.GrpcChallengeProto.HelloRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSayHelloManyTimesMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Greeter.
   * <pre>
   * Servicio gRPC
   * </pre>
   */
  public static final class GreeterFutureStub
      extends io.grpc.stub.AbstractFutureStub<GreeterFutureStub> {
    private GreeterFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GreeterFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GreeterFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Método simple de gRPC para enviar un saludo
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.challenge.GrpcChallengeProto.HelloReply> sayHello(
        org.challenge.GrpcChallengeProto.HelloRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSayHelloMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SAY_HELLO = 0;
  private static final int METHODID_SAY_HELLO_MANY_TIMES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SAY_HELLO:
          serviceImpl.sayHello((org.challenge.GrpcChallengeProto.HelloRequest) request,
              (io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply>) responseObserver);
          break;
        case METHODID_SAY_HELLO_MANY_TIMES:
          serviceImpl.sayHelloManyTimes((org.challenge.GrpcChallengeProto.HelloRequest) request,
              (io.grpc.stub.StreamObserver<org.challenge.GrpcChallengeProto.HelloReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSayHelloMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.challenge.GrpcChallengeProto.HelloRequest,
              org.challenge.GrpcChallengeProto.HelloReply>(
                service, METHODID_SAY_HELLO)))
        .addMethod(
          getSayHelloManyTimesMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              org.challenge.GrpcChallengeProto.HelloRequest,
              org.challenge.GrpcChallengeProto.HelloReply>(
                service, METHODID_SAY_HELLO_MANY_TIMES)))
        .build();
  }

  private static abstract class GreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GreeterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.challenge.GrpcChallengeProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Greeter");
    }
  }

  private static final class GreeterFileDescriptorSupplier
      extends GreeterBaseDescriptorSupplier {
    GreeterFileDescriptorSupplier() {}
  }

  private static final class GreeterMethodDescriptorSupplier
      extends GreeterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GreeterMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GreeterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GreeterFileDescriptorSupplier())
              .addMethod(getSayHelloMethod())
              .addMethod(getSayHelloManyTimesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
