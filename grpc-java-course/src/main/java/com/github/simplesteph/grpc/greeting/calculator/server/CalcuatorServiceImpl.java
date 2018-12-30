package com.github.simplesteph.grpc.greeting.calculator.server;

import com.proto.exercise.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;

public class CalcuatorServiceImpl extends CalcuatorServiceGrpc.CalcuatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {

        SumNumbers numbers = request.getNumbers();

        int result = numbers.getFirstNum() + numbers.getSecondNum();

        SumResponse response = SumResponse.newBuilder().setResult(result).build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void decomposePrime(DecomposePrimeRequest request, StreamObserver<DecomposePrimeResponse> responseObserver) {

        try {
            int k = 2;
            int n = request.getNum();

            while(n > 1) {
                if (n % k == 0) {
                    DecomposePrimeResponse response = DecomposePrimeResponse.newBuilder().setFactor(k).build();
                    responseObserver.onNext(response);
                    n /= k;
                } else {
                    k = k + 1;
                }
            }
        } finally {
            responseObserver.onCompleted();
        }

    }

    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {

        StreamObserver<AverageRequest> requestStreamObserver = new StreamObserver<AverageRequest>() {

            float sum = 0;
            ArrayList<Float> nums = new ArrayList<Float>();

            @Override
            public void onNext(AverageRequest value) {
                sum += value.getNum();
                nums.add(value.getNum());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                // client is done.
                responseObserver.onNext(
                        AverageResponse
                                .newBuilder()
                                .setResult(sum / nums.size())
                                .build());
            }
        };
        return requestStreamObserver;
    }

    @Override
    public StreamObserver<FindMaxRequest> findMax(StreamObserver<FindMaxResponse> responseObserver) {

        StreamObserver<FindMaxRequest> requestStreamObserver = new StreamObserver<FindMaxRequest>() {

            ArrayList<Integer> nums = new ArrayList<>();

            @Override
            public void onNext(FindMaxRequest value) {
                nums.add(value.getNum());
                System.out.println("got> " + value.getNum());
                int result = nums.stream().max(Integer::compareTo).get();
                responseObserver.onNext(
                        FindMaxResponse.newBuilder().setResult(result).build()
                );
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestStreamObserver;
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        Integer number = request.getNumber();

        if(number >=0) {
            double numberRoot = Math.sqrt(number);
            responseObserver.onNext(
                    SquareRootResponse.newBuilder().setNumberRoot(numberRoot).build()
            );
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("The number being sent is not positive")
                            .augmentDescription("Number sent: " + number)
                            .asRuntimeException()
            );
        }
    }
}
