package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingEveryoneClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Creating stub");

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Stephane", "John", "Marc", "Patricia").forEach(name -> {
            System.out.println("Sending:" + name);
            requestStreamObserver.onNext(
                    GreetEveryoneRequest.newBuilder().setGreeting(
                            Greeting.newBuilder().setFirstName(name).build()
                    ).build());

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // we tell the server that the client is done sending data.
        requestStreamObserver.onCompleted();

        // latch is here to allow the server to send us the response.
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
