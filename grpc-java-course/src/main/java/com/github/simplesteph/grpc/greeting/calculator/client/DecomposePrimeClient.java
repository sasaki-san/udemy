package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.exercise.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DecomposePrimeClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC SUM client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        // created a greet service client (blocking - synchronous)
        CalcuatorServiceGrpc.CalcuatorServiceBlockingStub client = CalcuatorServiceGrpc.newBlockingStub(channel);

        // do the same thing for GreetRequest.
        DecomposePrimeRequest request = DecomposePrimeRequest.newBuilder()
                .setNum(789).build();

        // call the RPC and get back a GreetResponse (protocol buffers)
        client.decomposePrime(request).forEachRemaining(a -> {
            System.out.println(a.getFactor());
        });

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
