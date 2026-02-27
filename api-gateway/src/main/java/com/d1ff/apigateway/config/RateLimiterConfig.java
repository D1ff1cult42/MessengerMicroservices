package com.d1ff.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /**
     * Rate limiting по IP-адресу клиента.
     * Используется в RequestRateLimiter фильтре для auth-service.
     *
     * 5 запросов/сек (replenishRate), burst до 10 (burstCapacity).
     * Защита от брутфорса логина/регистрации.
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown"
        );
    }
}
