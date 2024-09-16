package com.challenge.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.56.0)",
    comments = "Source: FileTransfer.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FileTransferGrpc {

  private FileTransferGrpc() {}

  public static final String SERVICE_NAME = "filetransfer.FileTransfer";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.challenge.grpc.FileRequest,
      com.challenge.grpc.FileResponse> getTransferFileMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransferFile",
      requestType = com.challenge.grpc.FileRequest.class,
      responseType = com.challenge.grpc.FileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.challenge.grpc.FileRequest,
      com.challenge.grpc.FileResponse> getTransferFileMethod() {
    io.grpc.MethodDescriptor<com.challenge.grpc.FileRequest, com.challenge.grpc.FileResponse> getTransferFileMethod;
    if ((getTransferFileMethod = FileTransferGrpc.getTransferFileMethod) == null) {
      synchronized (FileTransferGrpc.class) {
        if ((getTransferFileMethod = FileTransferGrpc.getTransferFileMethod) == null) {
          FileTransferGrpc.getTransferFileMethod = getTransferFileMethod =
              io.grpc.MethodDescriptor.<com.challenge.grpc.FileRequest, com.challenge.grpc.FileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "TransferFile"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.challenge.grpc.FileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.challenge.grpc.FileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FileTransferMethodDescriptorSupplier("TransferFile"))
              .build();
        }
      }
    }
    return getTransferFileMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FileTransferStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileTransferStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileTransferStub>() {
        @java.lang.Override
        public FileTransferStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileTransferStub(channel, callOptions);
        }
      };
    return FileTransferStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FileTransferBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileTransferBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileTransferBlockingStub>() {
        @java.lang.Override
        public FileTransferBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileTransferBlockingStub(channel, callOptions);
        }
      };
    return FileTransferBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FileTransferFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FileTransferFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FileTransferFutureStub>() {
        @java.lang.Override
        public FileTransferFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FileTransferFutureStub(channel, callOptions);
        }
      };
    return FileTransferFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void transferFile(com.challenge.grpc.FileRequest request,
        io.grpc.stub.StreamObserver<com.challenge.grpc.FileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getTransferFileMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FileTransfer.
   */
  public static abstract class FileTransferImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FileTransferGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FileTransfer.
   */
  public static final class FileTransferStub
      extends io.grpc.stub.AbstractAsyncStub<FileTransferStub> {
    private FileTransferStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileTransferStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileTransferStub(channel, callOptions);
    }

    /**
     */
    public void transferFile(com.challenge.grpc.FileRequest request,
        io.grpc.stub.StreamObserver<com.challenge.grpc.FileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getTransferFileMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FileTransfer.
   */
  public static final class FileTransferBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FileTransferBlockingStub> {
    private FileTransferBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileTransferBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileTransferBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.challenge.grpc.FileResponse transferFile(com.challenge.grpc.FileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getTransferFileMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FileTransfer.
   */
  public static final class FileTransferFutureStub
      extends io.grpc.stub.AbstractFutureStub<FileTransferFutureStub> {
    private FileTransferFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FileTransferFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FileTransferFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.challenge.grpc.FileResponse> transferFile(
        com.challenge.grpc.FileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getTransferFileMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_TRANSFER_FILE = 0;

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
        case METHODID_TRANSFER_FILE:
          serviceImpl.transferFile((com.challenge.grpc.FileRequest) request,
              (io.grpc.stub.StreamObserver<com.challenge.grpc.FileResponse>) responseObserver);
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
          getTransferFileMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.challenge.grpc.FileRequest,
              com.challenge.grpc.FileResponse>(
                service, METHODID_TRANSFER_FILE)))
        .build();
  }

  private static abstract class FileTransferBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FileTransferBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.challenge.grpc.FileTransferProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FileTransfer");
    }
  }

  private static final class FileTransferFileDescriptorSupplier
      extends FileTransferBaseDescriptorSupplier {
    FileTransferFileDescriptorSupplier() {}
  }

  private static final class FileTransferMethodDescriptorSupplier
      extends FileTransferBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FileTransferMethodDescriptorSupplier(String methodName) {
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
      synchronized (FileTransferGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FileTransferFileDescriptorSupplier())
              .addMethod(getTransferFileMethod())
              .build();
        }
      }
    }
    return result;
  }
}
