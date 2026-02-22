package com.d1ff.apigateway.config;

import io.github.bucket4j.distributed.proxy.AsyncProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

@Configuration
@Getter
public class RsaPropertiesConfig {
    private final RSAPublicKey publicKey;

    public RsaPropertiesConfig(@Value("${jwt.public-key}") String publicKeyContent) {
        this.publicKey = parsePublicKey(publicKeyContent);
    }

    private RSAPublicKey parsePublicKey(String key) {
        try{
            String publicKeyPEM = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----","")
                    .replaceAll("\\s","");
            byte[] encoded = java.util.Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e){
            throw new IllegalStateException("Failed to parse RSA public key", e);
        }
    }
}
