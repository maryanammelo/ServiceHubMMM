package com.servicehub.servicehub_api.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMillis;

    public JwtService(@Value("${servicehub.jwt.secret}") String secret,
                      @Value("${servicehub.jwt.expiration-minutes:60}") long expirationMinutes) {
        if (secret == null || secret.length() < 32) throw new IllegalArgumentException("SERVICEHUB_JWT_SECRET deve ter ao menos 32 caracteres");
        if (expirationMinutes <= 0) throw new IllegalArgumentException("A validade do token deve ser positiva");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = Math.multiplyExact(expirationMinutes, 60_000L);
    }
    public String gerar(String email, String role) {
        Date now = new Date();
        return Jwts.builder().subject(email).claim("role", role).issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis)).signWith(key).compact();
    }
    public String extrairEmail(String token) { return Jwts.parser().verifyWith((javax.crypto.SecretKey) key).build().parseSignedClaims(token).getPayload().getSubject(); }
    public long getExpirationSeconds() { return expirationMillis / 1000; }
}
