package org.d1ff.authservice.service.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.d1ff.authservice.config.RsaPropertiesConfig;
import org.d1ff.authservice.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${auth.access-token-expiration}")
    private Duration accessTokenExpiration;

    @Value("${auth.issuer}")
    private String issuer;

    private final RsaPropertiesConfig rsaPropertiesConfig;
    private RSASSASigner signer;

    @PostConstruct
    public void init() {
        signer = new RSASSASigner(rsaPropertiesConfig.getPrivateKey());
    }


    public String generateToken(User user) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp exp = new Timestamp(now.getTime() + accessTokenExpiration.toMillis());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer(issuer)
                .issueTime(now)
                .expirationTime(exp)
                .jwtID(UUID.randomUUID().toString())
                .claim("userId", user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("isActive", user.isActive())
                .claim("isVerified", user.isVerified())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("auth-key").build(),
                claimsSet);

        try {
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT token", e);
        }
    }
}
