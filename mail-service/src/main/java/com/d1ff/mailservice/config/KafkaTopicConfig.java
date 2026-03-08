package com.d1ff.mailservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic emailConfirmedTopic() {
        return TopicBuilder.name("email-confirmed")
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
                .build();
    }

    @Bean
    public NewTopic emailConfirmationDltTopic() {
        return TopicBuilder.name("email-confirmation-dlt")
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .build();
    }
}
