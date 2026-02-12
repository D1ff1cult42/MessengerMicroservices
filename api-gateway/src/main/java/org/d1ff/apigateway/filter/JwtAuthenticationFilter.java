package org.d1ff.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.d1ff.apigateway.service.interfaces.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("Processing request for path: {}", path);

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found for path: {}, passing to next filter", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            log.warn("Invalid JWT token for path: {}", path);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        try {
            String userId = jwtService.getUserId(token);
            String email = jwtService.getEmail(token);
            String role = jwtService.getRole(token);

            HeaderMapRequestWrapper wrappedRequest = new HeaderMapRequestWrapper(request);
            wrappedRequest.addHeader("X-User-Id", userId != null ? userId : "");
            wrappedRequest.addHeader("X-User-Email", email != null ? email : "");
            wrappedRequest.addHeader("X-User-Role", role != null ? role : "");

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, List.of(new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER")))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT validated successfully for user: {} (ID: {})", email, userId);

            filterChain.doFilter(wrappedRequest, response);
        } catch (Exception e) {
            log.error("Error extracting user info from token: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Failed to extract user information\"}");
        }
    }

    private static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String> headerMap = new HashMap<>();

        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = headerMap.get(name);
            if (headerValue != null) {
                return headerValue;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            String headerValue = headerMap.get(name);
            if (headerValue != null) {
                values.add(headerValue);
            }
            return Collections.enumeration(values);
        }
    }
}

