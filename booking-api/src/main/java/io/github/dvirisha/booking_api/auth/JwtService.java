package io.github.dvirisha.booking_api.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationInMinutes;

    public JwtService(
            @Value("${spring.security.jwt.secret}") String secret,
            @Value("${spring.security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationInMinutes = expirationMinutes;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(expirationInMinutes))))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String toker) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(toker)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parseClaims(token)
                .getSubject();
    }

    public boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

}
