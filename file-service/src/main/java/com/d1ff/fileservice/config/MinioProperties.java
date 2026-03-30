package com.d1ff.fileservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProperties {
    private String endpoint;
    private String publicEndpoint;
    private String accessKey;
    private String secretKey;
    private long fileMaxSizeBytes;
    private Duration defaultPresignedUrlExpiration = Duration.ofHours(24);
    private Map<String, BucketProperties> buckets = new HashMap<>();

    @Data
    public static class BucketProperties {
        private Duration presignedUrlExpiration;
    }

    public Duration getPresignedUrlExpiration(String bucketName) {
        BucketProperties bucket = buckets.get(bucketName);
        if (bucket != null && bucket.getPresignedUrlExpiration() != null) {
            return bucket.getPresignedUrlExpiration();
        }
        return defaultPresignedUrlExpiration;
    }
}
