package com.d1ff.realtimegateway.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Map;

public class UserHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserHandshakeInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes){
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            String userId = serverHttpRequest.getServletRequest().getHeader("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                attributes.put("userId", userId);
                return true;
            }
        }

        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token != null && !token.isBlank()) {
            String userId = extractUserIdFromJwt(token);
            if (userId != null && !userId.isBlank()) {
                attributes.put("userId", userId);
                return true;
            }
        }

        log.warn("WebSocket handshake rejected: missing X-User-Id header and no valid token query parameter");
        return false;
    }

    private String extractUserIdFromJwt(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid JWT format");
                return null;
            }
            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode claims = objectMapper.readTree(decodedPayload);
            return claims.has("userId") ? claims.get("userId").asText() : null;
        } catch (Exception e) {
            log.error("Error extracting userId from JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception){

    }
}
