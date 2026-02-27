package com.d1ff.realtimegateway.interceptor.principal;


import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Map;

public class UserPrincipalChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if(sessionAttributes != null){
                String userId = (String)sessionAttributes.get("userId");
                if(userId != null){
                    accessor.setUser(new StompPrincipal(userId));
                }
            }
        }
        return message;
    }
}
