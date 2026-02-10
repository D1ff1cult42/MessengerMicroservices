package org.d1ff.apigateway.service.impl;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.d1ff.apigateway.config.RsaPropertiesConfig;
import org.d1ff.apigateway.service.interfaces.JwtService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final RsaPropertiesConfig rsaConfig;

    @Override
    public boolean validateToken(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(rsaConfig.getPublicKey());

            if(!signedJWT.verify(verifier)){
                log.warn("Invalid JWT signature");
                return false;
            }

            Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if(expTime == null){
                log.warn("JWT token has no expiration time");
                return false;
            }

            Timestamp expirationTime = new Timestamp(expTime.getTime());
            if(expirationTime.before(new Timestamp(System.currentTimeMillis()))){
                log.warn("JWT token has expired");
                return false;
            }
            return true;
        }catch(Exception e){
            log.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Map<String, Object> getClaims(String token){
        try{
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getClaims();
        }catch(Exception e){
            log.error("Error extracting claims from JWT token: {}", e.getMessage());
            return Map.of();
        }
    }

    @Override
    public String getUserId(String token){
        Map<String, Object> claims = getClaims(token);
        return (String) claims.get("userId");
    }

    @Override
    public String getEmail(String token){
        Map<String, Object> claims = getClaims(token);
        return (String) claims.get("sub");
    }

    @Override
    public String getRole(String token){
        Map<String, Object> claims = getClaims(token);
        return (String) claims.get("role");
    }
}
