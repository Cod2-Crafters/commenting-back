package com.codecrafter.commenting.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@PropertySource("classpath:jwt.yml")
@Component
@Slf4j
public class TokenProvider {

    private final String secretKey;
    private final long expirationHours;
    private final String issuer;
    private final Key key;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenProvider(
        @Value("${secret-key}") String secretKey,
        @Value("${expiration-hours}") long expirationHours,
        @Value("${issuer}") String issuer,
        RedisTemplate<String, String> redisTemplate) {
        this.secretKey          = secretKey;
        this.expirationHours    = expirationHours;
        this.issuer             = issuer;
        this.key                = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.redisTemplate      = redisTemplate;
    }

    public String createToken(String userSpecification) {
        return Jwts.builder()
            .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))
            .setSubject(userSpecification)
            .setIssuer(issuer)
            .setIssuedAt(Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant()))
            .setExpiration(Date.from(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(expirationHours).toInstant()))
            .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public void invalidateToken(String token) {
        long expirationMillis = Jwts.parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration()
            .getTime();

        long currentMillis = System.currentTimeMillis();
        long ttl = expirationMillis - currentMillis;

        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(token, "invalid", Duration.ofMillis(ttl));
    }

    private boolean isTokenBlacklisted(String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(token) != null;
    }
}
