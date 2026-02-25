package com.d1ff.bucket;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "bucket-resolver", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(BucketResolverProperties.class)
public class BucketResolverAutoConfiguration {

    @Bean
    public BucketResolver bucketResolver(BucketResolverProperties properties) {
        return new BucketResolver(properties);
    }
}
