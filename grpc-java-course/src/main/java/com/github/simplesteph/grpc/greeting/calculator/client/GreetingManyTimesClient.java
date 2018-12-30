package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingManyTimesClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(channel);

        GreetManyTimesRequest request = GreetManyTimesRequest
                .newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Stephane"))
                .build();

        client.greetManyTimes(request).forEachRemaining(greetManyTimesResponse -> {
            System.out.println(greetManyTimesResponse.getResult());
        });

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
