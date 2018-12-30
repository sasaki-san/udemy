package com.github.simplesteph.grpc.greeting.calculator.server;

import com.proto.greet.*;
import com.proto.greet.GreetServiceGrpc.GreetServiceImplBase;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        Greeting greeting = request.getGreeting();

        String firstName = greeting.getFirstName();
        String result = "Hello " + firstName;

        // create the response
        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number: " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder().setResult(result).build();
                responseObserver.onNext(response);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestStreamObserver = new StreamObserver<LongGreetRequest>() {

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message
                result += "Hello " + value.getGreeting().getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client is done.
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
                // this is when we want to return a response.
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {

        StreamObserver<GreetEveryoneRequest> greetEveryoneRequestStreamObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName();
                GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder().setResult(result).build();
                // we respond many times
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                // when client says they're done with sending the data, we also say we're done.
                responseObserver.onCompleted();
            }
        };
        return greetEveryoneRequestStreamObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try {
            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("Sleep for 100 ms");
                    Thread.sleep(100);
                } else {
                    return;
                }

            }
            System.out.println("Send response");
            responseObserver.onNext(
                    GreetWithDeadlineResponse.newBuilder().setResult("Hello " + request.getGreeting().getFirstName()).build()
            );
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
