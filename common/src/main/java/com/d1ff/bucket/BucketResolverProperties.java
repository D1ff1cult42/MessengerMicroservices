package com.d1ff.bucket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "bucket-resolver")
public class BucketResolverProperties {
    private Map<String, BucketConfig> buckets = new HashMap<>();
}
