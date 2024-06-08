package com.codecrafter.commenting.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.Key;

@PropertySource("classpath:jwt.yml")
@Component
@Slf4j
public class JwtUtil {

    private Key key;

    private final String secretKey;
    private final long expirationHours;
    private final String issuer;

    public JwtUtil(
        @Value("${secret-key}") String secretKey,
        @Value("${expiration-hours}") long expirationHours,
        @Value("${issuer}") String issuer
    ) {
        this.secretKey = secretKey;
        this.expirationHours = expirationHours;
        this.issuer = issuer;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String getEmailFromToken(String token) {
        log.info("getEmailFromToken token : {}", token);
        log.info("getEmailFromToken secretKey : {}", secretKey);
        Claims claims = validateToken(token);
        return claims.getSubject();
    }
}

