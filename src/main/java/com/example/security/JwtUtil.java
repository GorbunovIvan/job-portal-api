package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${spring.security.jwt.secret}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private Integer jwtExpiration;  // In minutes

    public String generateToken(@NonNull UserDetails userDetails) {

        log.info("Generating new token for: {}", userDetails.getUsername());

        var issuedAt = new Date();
        var expiration = Date.from(issuedAt.toInstant().plus(Duration.ofMinutes(jwtExpiration)));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        log.info("Validating token for: {}", userDetails.getUsername());

        if (isTokenExpired(token)) {
            log.info("Token is expired (for: '{}')", userDetails.getUsername());
            return false;
        }

        String username = extractUsernameFromToken(token);

        var isValid = username.equals(userDetails.getUsername());
        log.info("Token is {} for: {}", (isValid ? "valid" : "not valid"), userDetails.getUsername());

        return isValid;
    }

    public String extractUsernameFromToken(String token) {
        log.info("Extracting username from token");
        return getTokenPayload(token)
                .getSubject();
    }

    private boolean isTokenExpired(String token) {
        try {
            return getTokenPayload(token)
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getTokenPayload(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignKey() {
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}
