package com.d1ff.grpc.client.file;

import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import com.d1ff.common.grpc.file.FileChunk;
import com.d1ff.common.grpc.file.FileGrpcServiceGrpc;
import com.d1ff.common.grpc.file.FileMetadata;
import com.d1ff.common.grpc.file.GetPresignedUrlRequest;
import com.d1ff.common.grpc.file.GetPresignedUrlResponse;
import com.d1ff.common.grpc.file.UploadFileRequest;
import com.d1ff.common.grpc.file.UploadFileResponse;
import com.d1ff.dto.response.FileUploadResponse;
import com.d1ff.dto.response.PresignedUrlResponse;
import com.d1ff.exceptions.minio.FailedToUploadMinio;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class FileGrpcClient {

    private static final int CHUNK_SIZE = 64 * 1024; // 64 KB

    @GrpcClient("file-service")
    private FileGrpcServiceGrpc.FileGrpcServiceStub asyncStub;

    @GrpcClient("file-service")
    private FileGrpcServiceGrpc.FileGrpcServiceBlockingStub blockingStub;

    public FileUploadResponse uploadFile(String bucket, MultipartFile file) {
        AtomicReference<UploadFileResponse> responseRef = new AtomicReference<>();
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<UploadFileResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(UploadFileResponse response) {
                responseRef.set(response);
            }

            @Override
            public void onError(Throwable t) {
                errorRef.set(t);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        try {
            StreamObserver<UploadFileRequest> requestObserver = asyncStub.uploadFile(responseObserver);

            FileMetadata metadata = FileMetadata.newBuilder()
                    .setBucket(bucket)
                    .setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown")
                    .setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .setFileSize(file.getSize())
                    .build();

            requestObserver.onNext(UploadFileRequest.newBuilder()
                    .setMetadata(metadata)
                    .build());

            try (InputStream inputStream = file.getInputStream()) {
                byte[] buffer = new byte[CHUNK_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    FileChunk chunk = FileChunk.newBuilder()
                            .setData(ByteString.copyFrom(buffer, 0, bytesRead))
                            .build();

                    requestObserver.onNext(UploadFileRequest.newBuilder()
                            .setChunk(chunk)
                            .build());
                }
            }

            requestObserver.onCompleted();
            latch.await();

            if (errorRef.get() != null) {
                log.error("gRPC upload failed for file {}: {}", file.getOriginalFilename(), errorRef.get().getMessage());
                throw new FailedToUploadMinio(file.getOriginalFilename());
            }

            UploadFileResponse response = responseRef.get();
            log.info("File {} uploaded via gRPC. Bucket: {}, Object: {}",
                    file.getOriginalFilename(), response.getBucketName(), response.getObjectName());

            return new FileUploadResponse(response.getBucketName(), response.getObjectName());

        } catch (FailedToUploadMinio e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload file {} via gRPC: {}", file.getOriginalFilename(), e.getMessage());
            throw new FailedToUploadMinio(file.getOriginalFilename());
        }
    }

    public PresignedUrlResponse getPresignedUrl(String bucketName, String objectName) {
        try {
            GetPresignedUrlRequest request = GetPresignedUrlRequest.newBuilder()
                    .setBucketName(bucketName)
                    .setObjectName(objectName)
                    .build();

            GetPresignedUrlResponse response = blockingStub.getPresignedUrl(request);
            return new PresignedUrlResponse(response.getUrl(), response.getExpiresInSeconds());
        } catch (StatusRuntimeException e) {
            log.error("Failed to get presigned URL via gRPC for {}/{}: {}", bucketName, objectName, e.getStatus());
            return null;
        }
    }
}
