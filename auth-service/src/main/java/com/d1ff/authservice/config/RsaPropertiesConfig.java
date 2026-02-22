package com.d1ff.authservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@ConfigurationProperties(prefix = "jwt")
@Getter

public class RsaPropertiesConfig {

private RSAPrivateKey privateKey;

    public RsaPropertiesConfig(String privateKey) {
        this.privateKey = parsePrivateKey(privateKey);
    }

    private RSAPrivateKey parsePrivateKey(String key) {
        try {
            String privateKeyPEM = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse RSA private key", e);
        }
    }
}
