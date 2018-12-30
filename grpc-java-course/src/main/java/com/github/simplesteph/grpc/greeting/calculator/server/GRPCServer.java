package com.github.simplesteph.grpc.greeting.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class GRPCServer {

    public static void main(String[] args) throws InterruptedException, IOException {

        System.out.println("Hello gRPC");

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .addService(new CalcuatorServiceImpl())
                .addService(ProtoReflectionService.newInstance())
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));
        server.awaitTermination();

    }

}
