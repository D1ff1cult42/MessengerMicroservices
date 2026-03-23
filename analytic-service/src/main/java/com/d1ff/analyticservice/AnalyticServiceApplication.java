package com.d1ff.analyticservice;

import com.d1ff.analyticservice.config.AnalyticConfigurationProperties;
import com.d1ff.bucket.BucketResolverProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AnalyticConfigurationProperties.class)
public class AnalyticServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticServiceApplication.class, args);
    }
}
