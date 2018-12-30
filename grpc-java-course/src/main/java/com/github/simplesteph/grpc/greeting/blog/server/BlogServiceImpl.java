package com.github.simplesteph.grpc.greeting.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        System.out.println("Creating blog...");

        Blog blog = request.getBlog();

        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        System.out.println("Inserting blog...");

        // we insert
        collection.insertOne(doc);

        System.out.println("Inserted blog...");

        // we retrieve the MongoDB generated ID
        String id = doc.getObjectId("_id").toString();

        System.out.println("Inserted blog id " + id);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

        System.out.println("Recieved Read blog request");

        String blogId = request.getBlogId();
        Document result = null;

        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch(Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        }

        if(result == null){
            System.out.println("Blog not found");
            // we don't have a match
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                    .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, sending a response");

            Blog blog = documentToBlog(result);

            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {

        System.out.println("Recieved Update blog request");

        Blog blog = request.getBlog();

        String blogId = blog.getId();
        Document result = null;

        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();
        } catch(Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        }

        if(result == null){
            System.out.println("Blog not found");
            // we don't have a match
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        } else {
            System.out.println("Blog found, replacing");

            Document replacement = new Document("author_id", blog.getAuthorId())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blogId));

            collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

            System.out.println("Replaced, sending a response");
            responseObserver.onNext(UpdateBlogResponse.newBuilder().setBlog(documentToBlog(replacement)).build());
            responseObserver.onCompleted();
        }

    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {

        System.out.println("Received Delete Blog Request");
        DeleteResult result =  null;

        try {
            result = collection.deleteOne(eq("_id", new ObjectId(request.getBlogId())));
        }catch (Exception e){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        }

        if(result.getDeletedCount() == 0){
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription("The blog with the corresponding id was not found")
                            .asRuntimeException()
            );
        } else {
            System.out.println("Blog was deleted.");
            responseObserver.onNext(DeleteBlogResponse.newBuilder().setBlogId(request.getBlogId()).build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {

        System.out.println("Received List Blog Request");

        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                ListBlogResponse.newBuilder().setBlog(documentToBlog(document)).build()
        ));

        responseObserver.onCompleted();
    }

    private Blog documentToBlog(Document document){

        return Blog.newBuilder()
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .setId(document.getObjectId("_id").toString())
                .build();
    }
}
