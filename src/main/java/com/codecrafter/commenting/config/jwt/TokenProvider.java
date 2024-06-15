package com.codecrafter.commenting.config.jwt;

import com.codecrafter.commenting.exception.AuthenticationFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@PropertySource("classpath:jwt.yml")
@Service
@Slf4j
public class TokenProvider {

    private final String secretKey;
    private final long expirationHours;
    private final String issuer;

    public TokenProvider(
        @Value("${secret-key}") String secretKey,
        @Value("${expiration-hours}") long expirationHours,
        @Value("${issuer}") String issuer
    ) {
        this.secretKey = secretKey;
        this.expirationHours = expirationHours;
        this.issuer = issuer;
    }

    public String createToken(String userSpecification) {
        return Jwts.builder()
            .signWith(new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName()))
            .setSubject(userSpecification)
            .setIssuer(issuer)
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setExpiration(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
            .compact();
    }

    public String getUserSpecificationFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return claims.getSubject();
    }

}