package org.d1ff.messageservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProperties{
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Map<String, Duration> bucketExpirations = new HashMap<>();
}
