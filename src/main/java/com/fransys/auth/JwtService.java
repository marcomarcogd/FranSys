package com.fransys.auth;

import com.fransys.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final AppProperties appProperties;
    private final Key signingKey;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
        byte[] keyBytes = Decoders.BASE64.decode(java.util.Base64.getEncoder()
                .encodeToString(appProperties.getJwt().getSecret().getBytes()));
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(SysUserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(appProperties.getJwt().getAccessTokenMinutes() * 60);
        return Jwts.builder()
                .issuer(appProperties.getJwt().getIssuer())
                .subject(userDetails.getUsername())
                .claims(Map.of(
                        "userId", userDetails.getUserId(),
                        "displayName", userDetails.getDisplayName(),
                        "roleCode", userDetails.getRoleCode()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
