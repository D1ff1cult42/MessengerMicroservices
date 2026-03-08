package com.d1ff.authservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public DefaultErrorHandler errorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(15000L);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
        handler.addNotRetryableExceptions(
                IllegalArgumentException.class
        );

        log.info("Kafka DefaultErrorHandler configured with DLT and exponential backoff");
        return handler;
    }
}
