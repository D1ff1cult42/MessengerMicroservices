package com.d1ff.apigateway.filter;

import com.d1ff.apigateway.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    private static final List<String> OPEN_PATHS = List.of(
            "/actuator", "/api/auth", "/api/mail", "/swagger", "/swagger-ui", "/v3/api-docs",
            "/webjars", "/docs", "/fallback", "/health", "/springwolf"
    );

    private static final List<String> PROTECTED_PATHS = List.of("/ws");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.debug("Processing request for path: {}", path);

        if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // For WebSocket handshake, try to extract token from query parameter
            if (PROTECTED_PATHS.stream().anyMatch(path::startsWith)) {
                String tokenParam = exchange.getRequest().getQueryParams().getFirst("token");
                if (tokenParam != null && !tokenParam.isBlank()) {
                    authHeader = "Bearer " + tokenParam;
                } else {
                    log.warn("No Bearer token found for protected path: {}", path);
                    return unauthorizedResponse(exchange, "Authentication required");
                }
            } else {
                log.debug("No Bearer token found for path: {}, passing to next filter", path);
                return chain.filter(exchange);
            }
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        try {
            String userId = jwtService.getUserId(token);
            String email = jwtService.getEmail(token);
            String role = jwtService.getRole(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Email", email != null ? email : "")
                    .header("X-User-Role", role != null ? role : "")
                    .build();

            log.debug("JWT validated successfully for user: {} (ID: {})", email, userId);

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Failed to extract user information");
        }
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = ("{\"error\": \"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

