package com.cipherinfratech.lms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class SsoJwtService {

    @Value("${sso.public-key}")
    private Resource publicKeyResource;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        String keyContent = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(keyContent);
        this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    /**
     * Try to parse token as RSA-signed SSO token.
     * Returns Claims if valid, null if not an RSA token or invalid.
     */
    public Claims validateSsoToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check expiration
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            return claims;
        } catch (Exception e) {
            // Not an RSA token or invalid — return null so existing filter can try
            return null;
        }
    }

    public String extractEmail(Claims claims) {
        return claims.getSubject();
    }
}