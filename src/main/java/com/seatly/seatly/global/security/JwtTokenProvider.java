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

  public String createAccessToken(Long id, String username, String role) {
    Date now = new Date();
    Date expiry = new Date(
        now.getTime() + jwtProperties.getAccessTokenExpireMs());

    return Jwts.builder()
        .setSubject(id.toString())
        .claim("username", username)
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String createRefreshToken(Long id) {
    Date now = new Date();
    Date expiry = new Date(
        now.getTime() + getRefreshTokenExpireMs());

    return Jwts.builder()
        .setSubject(id.toString())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public long getRefreshTokenExpireMs() {
    return jwtProperties.getRefreshTokenExpireMs();
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

  public Long getId(String token) {
    return Long.valueOf(parseClaims(token).getSubject());
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
