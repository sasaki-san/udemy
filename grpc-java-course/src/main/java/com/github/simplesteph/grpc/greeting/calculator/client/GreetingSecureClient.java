package com.github.simplesteph.grpc.greeting.calculator.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import javax.net.ssl.SSLException;
import java.io.File;

public class GreetingSecureClient {

    public static void main(String[] args) throws SSLException {

        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = NettyChannelBuilder
                .forAddress("localhost", 50051)
                .sslContext(
                        GrpcSslContexts
                                .forClient()
                                .trustManager(
                                        new File("ssl/ca.crt")
                                )
                                .build()
                ).build();

        System.out.println("Creating stub");

        // created a greet service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Stephane")
                .setLastName("Maarek")
                .build();

        // do the same thing for GreetRequest.
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting).build();

        // call the RPC and get back a GreetResponse (protocol buffers)
        GreetResponse response = greetClient.greet(greetRequest);

        System.out.println(response.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();

    }

}
