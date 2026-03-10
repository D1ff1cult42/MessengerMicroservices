package com.d1ff.realtimegateway.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
   @Bean
    public NewTopic messageDeliveredTopic(){
       return TopicBuilder.name("message-delivered")
               .partitions(3)
               .replicas(1)
               .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
               .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "1")
               .config(TopicConfig.CLEANUP_POLICY_CONFIG, "delete")
               .build();
   }
   @Bean
   public NewTopic messageDeliveredDltTopic(){
       return TopicBuilder.name("message-delivered-dlt")
               .partitions(3)
               .replicas(1)
               .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
               .build();
   }
}
