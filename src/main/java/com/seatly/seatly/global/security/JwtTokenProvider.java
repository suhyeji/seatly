package com.seatly.seatly.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(
        jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /* ===================== 토큰 생성 ===================== */

  public String createAccessToken(Long email, String username, String role) {
    Date now = new Date();
    Date expiry = new Date(
        now.getTime() + jwtProperties.getAccessTokenExpireMs());

    return Jwts.builder()
        .setSubject(email.toString())
        .claim("role", role)
        .claim("username", username)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /* ===================== 토큰 검증 ===================== */

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /* ===================== 정보 추출 ===================== */

  public String getEmail(String token) {
    return String.valueOf(parseClaims(token).getSubject());
  }

  public String getUsername(String token) {
    return parseClaims(token).get("username", String.class);
  }

  public String getRole(String token) {
    return parseClaims(token).get("role", String.class);
  }

  /* ===================== 내부 ===================== */

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
