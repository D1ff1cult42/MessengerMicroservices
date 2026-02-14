package org.d1ff.bucket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "bucket-resolver")
public class BucketResolverProperties {
    private Map<String, BucketConfig> buckets = new HashMap<>();
}
