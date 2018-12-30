package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.exercise.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FindMaxClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC SUM client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        CountDownLatch latch = new CountDownLatch(1);
        CalcuatorServiceGrpc.CalcuatorServiceStub asynClient = CalcuatorServiceGrpc.newStub(channel);
        StreamObserver<FindMaxRequest> requestStreamObserver = asynClient.findMax(new StreamObserver<FindMaxResponse>() {
            @Override
            public void onNext(FindMaxResponse value) {
                System.out.println("Max is: " + value.getResult());
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

        Arrays.asList(1, 5, 3, 6, 2, 20).forEach(num -> {

            System.out.println("Sending " + num);
            requestStreamObserver.onNext(FindMaxRequest.newBuilder().setNum(num).build());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
