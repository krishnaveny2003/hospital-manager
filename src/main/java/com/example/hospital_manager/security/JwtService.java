
package com.example.hospital_manager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        byte[] keyBytes = Base64.getDecoder()
            .decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            String username,
            String role) {

        Date now = new Date();

        Date expiry = new Date(
            now.getTime() + expiration
        );

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(getSigningKey())
            .compact();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return extractAllClaims(token)
            .get("role", String.class);
    }

    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractAllClaims(token);

            return claims.getSubject() != null
                && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException ex) {

            return false;
        }
    }

    public long getExpiration() {
        return expiration;
    }
}
