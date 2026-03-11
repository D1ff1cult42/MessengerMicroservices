package com.d1ff.realtimegateway.interceptor.principal;


import com.d1ff.grpc.client.chat.ChatGrpcClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserPrincipalChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserPrincipalChannelInterceptor.class);
    private static final Pattern CHAT_TOPIC_PATTERN = Pattern.compile("^/topic/chat\\.([^.]+)\\.");
    private final ChatGrpcClient chatGrpcClient;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes == null) {
                log.warn("STOMP CONNECT rejected: no session attributes");
                throw new IllegalStateException("Authentication required");
            }
            String userId = (String) sessionAttributes.get("userId");
            if (userId == null || userId.isBlank()) {
                log.warn("STOMP CONNECT rejected: no userId in session");
                throw new IllegalStateException("Authentication required");
            }
            accessor.setUser(new StompPrincipal(userId));
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String userId = accessor.getUser() != null ? accessor.getUser().getName() : null;

            if (destination != null && userId != null) {
                Matcher matcher = CHAT_TOPIC_PATTERN.matcher(destination);
                if (matcher.find()) {
                    String chatId = matcher.group(1);
                     if (!chatGrpcClient.isUserExistsInChat(UUID.fromString(userId), UUID.fromString(chatId))) {
                         log.warn("SUBSCRIBE rejected: user {} is not a participant of chat {}", userId, chatId);
                         throw new IllegalStateException("Access denied to chat " + chatId);
                     }
                    log.debug("User {} subscribing to chat {}", userId, chatId);
                }
            }
        }

        return message;
    }
}
