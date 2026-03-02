package com.d1ff.grpc.client.chat;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "grpc.chat-client", name = "enabled", havingValue = "true")
public class ChatGrpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ChatGrpcClient chatGrpcClient() {
        return new ChatGrpcClient();
    }
}
