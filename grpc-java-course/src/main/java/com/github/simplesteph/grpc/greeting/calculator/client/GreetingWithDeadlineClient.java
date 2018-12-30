package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.greet.*;
import io.grpc.*;

import java.util.concurrent.TimeUnit;

public class GreetingWithDeadlineClient {

    public static void main(String[] args) {

        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

        // created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // do the same thing for GreetRequest.
        send(greetClient, 3000);
        send(greetClient, 100);

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

    private static void send(GreetServiceGrpc.GreetServiceBlockingStub client, int ms) {
        Greeting greeting = Greeting.newBuilder().setFirstName("Stephane").build();
        GreetWithDeadlineRequest request = GreetWithDeadlineRequest.newBuilder().setGreeting(greeting).build();
        // call the RPC and get back a GreetResponse (protocol buffers)
        try {
            System.out.println("Sending a request with " + ms + " ms deadline....");
            GreetWithDeadlineResponse response = client
                    .withDeadline(
                            Deadline.after(ms, TimeUnit.MILLISECONDS)
                    ).greetWithDeadline(
                            request
                    );
            System.out.println(response.getResult());

        } catch (StatusRuntimeException e) {

            if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has been exceeded. we don't want the response");
            } else {
                e.printStackTrace();
            }
        }
    }

}
