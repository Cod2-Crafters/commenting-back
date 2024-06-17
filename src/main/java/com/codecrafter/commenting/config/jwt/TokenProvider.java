package com.codecrafter.commenting.config.jwt;

import com.codecrafter.commenting.exception.AuthenticationFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@PropertySource("classpath:jwt.yml")
@Component
@Slf4j
public class TokenProvider {

    private final String secretKey;
    private final long expirationHours;
    private final String issuer;
    private final Key key;

    public TokenProvider(
        @Value("${secret-key}") String secretKey,
        @Value("${expiration-hours}") long expirationHours,
        @Value("${issuer}") String issuer
    ) {
        this.secretKey          = secretKey;
        this.expirationHours    = expirationHours;
        this.issuer             = issuer;
        this.key                = Keys.hmacShaKeyFor(secretKey.getBytes());
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
}
