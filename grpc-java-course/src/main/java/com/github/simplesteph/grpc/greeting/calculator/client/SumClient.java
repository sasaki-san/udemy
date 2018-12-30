package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.exercise.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SumClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC SUM client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        // created a greet service client (blocking - synchronous)
        CalcuatorServiceGrpc.CalcuatorServiceBlockingStub client = CalcuatorServiceGrpc.newBlockingStub(channel);

        // created a protocol buffer greeting message
        SumNumbers numbers = SumNumbers.newBuilder()
                .setFirstNum(5)
                .setSecondNum(95)
                .build();

        // do the same thing for GreetRequest.
        SumRequest sumRequest = SumRequest.newBuilder()
                .setNumbers(numbers).build();

        // call the RPC and get back a GreetResponse (protocol buffers)
        SumResponse response =  client.sum(sumRequest);

        System.out.println(response.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
