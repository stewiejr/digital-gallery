package com.digitalgallery.security;

import com.digitalgallery.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private final String secret;
  private final long expirationMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expirationMinutes}") long expirationMinutes
  ) {
    this.secret = secret;
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(User user) {
    Instant now = Instant.now();
    Instant expiry = now.plusSeconds(expirationMinutes * 60);
    return Jwts.builder()
        .setSubject(user.getId().toString())
        .claim("email", user.getEmail())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiry))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUserId(String token) {
    return extractAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes;
    if (secret.startsWith("base64:")) {
      keyBytes = Decoders.BASE64.decode(secret.substring("base64:".length()));
    } else {
      keyBytes = secret.getBytes();
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
