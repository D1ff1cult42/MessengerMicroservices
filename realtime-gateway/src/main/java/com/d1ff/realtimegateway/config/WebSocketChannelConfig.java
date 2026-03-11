package com.d1ff.realtimegateway.config;

import com.d1ff.grpc.client.chat.ChatGrpcClient;
import com.d1ff.realtimegateway.interceptor.principal.UserPrincipalChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketChannelConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatGrpcClient chatGrpcClient;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(new UserPrincipalChannelInterceptor(chatGrpcClient));
    }
}
