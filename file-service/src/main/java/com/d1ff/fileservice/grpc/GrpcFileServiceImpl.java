package com.d1ff.fileservice.grpc;

import com.d1ff.common.grpc.file.*;
import com.d1ff.fileservice.config.MinioProperties;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.UUID;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class GrpcFileServiceImpl extends FileGrpcServiceGrpc.FileGrpcServiceImplBase {

    private final MinioProperties minioProperties;
    private final MinioClient minioClient;

    @Qualifier("presignedMinioClient")
    private final MinioClient presignedMinioClient;

    @Override
    public StreamObserver<UploadFileRequest> uploadFile(StreamObserver<UploadFileResponse> responseObserver) {
        return new StreamObserver<>() {
            private FileMetadata metadata;
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void onNext(UploadFileRequest request) {
                if (request.hasMetadata()) {
                    metadata = request.getMetadata();
                    log.info("Received upload metadata: bucket={}, filename={}, size={}",
                            metadata.getBucket(), metadata.getFilename(), metadata.getFileSize());
                } else if (request.hasChunk()) {
                    try {
                        request.getChunk().getData().writeTo(buffer);
                    } catch (Exception e) {
                        log.error("Error writing chunk to buffer: {}", e.getMessage());
                        responseObserver.onError(Status.INTERNAL
                                .withDescription("Error processing file chunk")
                                .asRuntimeException());
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                log.error("Upload stream error: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                try {
                    if (metadata == null) {
                        responseObserver.onError(Status.INVALID_ARGUMENT
                                .withDescription("File metadata was not provided")
                                .asRuntimeException());
                        return;
                    }

                    String bucket = metadata.getBucket();
                    String filename = metadata.getFilename();

                    if (buffer.size() > minioProperties.getFileMaxSizeBytes()) {
                        responseObserver.onError(Status.INVALID_ARGUMENT
                                .withDescription("File size exceeds the maximum allowed limit of "
                                        + minioProperties.getFileMaxSizeBytes() + " bytes")
                                .asRuntimeException());
                        return;
                    }

                    if (!minioProperties.getBuckets().containsKey(bucket)) {
                        responseObserver.onError(Status.INVALID_ARGUMENT
                                .withDescription("Unknown bucket: " + bucket)
                                .asRuntimeException());
                        return;
                    }

                    String extension = filename.substring(filename.lastIndexOf("."));
                    String uniqueFilename = UUID.randomUUID() + extension;

                    byte[] data = buffer.toByteArray();
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucket)
                                    .object(uniqueFilename)
                                    .stream(new ByteArrayInputStream(data), data.length, -1)
                                    .contentType(metadata.getContentType().isEmpty()
                                            ? "application/octet-stream"
                                            : metadata.getContentType())
                                    .build());

                    log.info("File {} uploaded via gRPC to bucket {} as {}",
                            filename, bucket, uniqueFilename);

                    UploadFileResponse response = UploadFileResponse.newBuilder()
                            .setBucketName(bucket)
                            .setObjectName(uniqueFilename)
                            .build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    log.error("Failed to upload file via gRPC: {}", e.getMessage());
                    responseObserver.onError(Status.INTERNAL
                            .withDescription("Failed to upload file: " + e.getMessage())
                            .asRuntimeException());
                }
            }
        };
    }

    @Override
    public void getPresignedUrl(GetPresignedUrlRequest request,
                                StreamObserver<GetPresignedUrlResponse> responseObserver) {
        try {
            String bucketName = request.getBucketName();
            String objectName = request.getObjectName();

            Duration expiration = minioProperties.getPresignedUrlExpiration(bucketName);

            String url = presignedMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry((int) expiration.toSeconds())
                            .build());

            log.info("Generated presigned URL via gRPC for {}/{}", bucketName, objectName);

            GetPresignedUrlResponse response = GetPresignedUrlResponse.newBuilder()
                    .setUrl(url)
                    .setExpiresInSeconds(expiration.toSeconds())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to generate presigned URL via gRPC for {}/{}: {}",
                    request.getBucketName(), request.getObjectName(), e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to generate presigned URL: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
