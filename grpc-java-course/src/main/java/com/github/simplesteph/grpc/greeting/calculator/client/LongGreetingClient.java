package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LongGreetingClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Creating stub");

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        StreamObserver<LongGreetRequest> requestStreamObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
                // onNext will be called only once
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                // on completed will be called right after onNext()
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        System.out.println("sending messages #1...");
        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(
                        Greeting.newBuilder().setFirstName("Stephane")
                ).build()
        );

        System.out.println("sending messages #2...");
        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(
                        Greeting.newBuilder().setFirstName("John")
                ).build()
        );

        System.out.println("sending messages #3...");
        requestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(
                        Greeting.newBuilder().setFirstName("Mark")
                ).build()
        );

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
