package org.d1ff.apigateway.service.interfaces;

import java.util.Map;

public interface JwtService {
    boolean validateToken(String token);

    Map<String, Object> getClaims(String token);

    String getUserId(String token);

    String getEmail(String token);

    String getRole(String token);
}
