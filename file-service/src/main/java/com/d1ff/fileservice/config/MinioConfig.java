package com.d1ff.fileservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfig {
    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(),
                             minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public CommandLineRunner initMinioBuckets(MinioClient minioClient) {
        return args -> {
            for (String bucketName : minioProperties.getBuckets().keySet()) {
                try {
                    boolean exists = minioClient.bucketExists(
                            BucketExistsArgs.builder().bucket(bucketName).build());
                    if (!exists) {
                        minioClient.makeBucket(
                                MakeBucketArgs.builder().bucket(bucketName).build());
                        log.info("Created MinIO bucket: {}", bucketName);
                    } else {
                        log.info("MinIO bucket already exists: {}", bucketName);
                    }
                } catch (Exception e) {
                    log.error("Failed to create MinIO bucket: {}. Error: {}", bucketName, e.getMessage());
                }
            }
        };
    }
}

