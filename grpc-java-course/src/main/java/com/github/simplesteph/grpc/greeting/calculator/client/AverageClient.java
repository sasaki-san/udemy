package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.exercise.AverageRequest;
import com.proto.exercise.AverageResponse;
import com.proto.exercise.CalcuatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AverageClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC SUM client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        System.out.println("Creating stub");

        CountDownLatch latch = new CountDownLatch(1);
        CalcuatorServiceGrpc.CalcuatorServiceStub asynClient = CalcuatorServiceGrpc.newStub(channel);
        StreamObserver<AverageRequest> requestStreamObserver = asynClient.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                // got a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us the average");
                latch.countDown();
            }
        });

        for(int i = 0;i<10000;i++){
            requestStreamObserver.onNext(
                    AverageRequest.newBuilder().setNum(i).build()
            );
        }
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
