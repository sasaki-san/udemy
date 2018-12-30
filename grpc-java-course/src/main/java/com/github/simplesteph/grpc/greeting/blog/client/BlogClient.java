package com.github.simplesteph.grpc.greeting.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    public static void main(String[] args) {


        System.out.println("Hello I'm a gRPC client for Blog");

        BlogClient main = new BlogClient();
        main.run();

    }

    private void run(){

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClinet = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setAuthorId("Stephane")
                .setTitle("New blog!")
                .setContent("Hello world this is my first blog!")
                .build();

        CreateBlogResponse createResponse =  blogClinet.createBlog(
                CreateBlogRequest.newBuilder().setBlog(blog).build()
        );

        System.out.println("Received create blog resopnse");
        System.out.println(createResponse.toString());

        String blogId = createResponse.getBlog().getId();

        System.out.println("Reading blog...");

        ReadBlogResponse readBlogResponse = blogClinet.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());

        System.out.println(readBlogResponse.toString());

        // System.out.println("Reading blog with a non-existing id...");
        // ReadBlogResponse readBlogResonseNotFound = blogClinet.readBlog(ReadBlogRequest.newBuilder().setBlogId("5c287a7e987ad0cd1b5d3f25").build());

        System.out.println("Updating blog...");
        Blog newBlog = Blog.newBuilder()
                .setId(blogId)
                .setAuthorId("Changed Author")
                .setTitle("New blog (updated)!")
                .setContent("Hello world this is my first blog! Added more stuff!")
                .build();

        UpdateBlogResponse updateBlogResponse = blogClinet.updateBlog(
                UpdateBlogRequest.newBuilder().setBlog(newBlog).build()
        );

        System.out.println("updated blog");
        System.out.println(updateBlogResponse.toString());

        blogClinet.deleteBlog(
                DeleteBlogRequest.newBuilder().setBlogId(blogId).build()
        );

        System.out.println("Deleted the blog...");

//        // should be NOT_FOUND
//        blogClinet.deleteBlog(
//                DeleteBlogRequest.newBuilder().setBlogId(blogId).build()
//        );

        System.out.println("Listing the blogs...");

        blogClinet.listBlog(ListBlogRequest.newBuilder().build()).forEachRemaining(
                listBlogResponse -> System.out.println(listBlogResponse.getBlog().toString())
        );
    }

}
