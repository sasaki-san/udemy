package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.exercise.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class SquareRootClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC Square Root client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        // created a greet service client (blocking - synchronous)
        CalcuatorServiceGrpc.CalcuatorServiceBlockingStub client = CalcuatorServiceGrpc.newBlockingStub(channel);

        int number = -1;

        // created a protocol buffer greeting message
        try {
            SquareRootResponse response = client.squareRoot(SquareRootRequest.newBuilder().setNumber(number).build());
            System.out.println(response.getNumberRoot());
        }catch (StatusRuntimeException e){
            System.out.println("Got an exception for square root!");
            e.printStackTrace();
        }

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
