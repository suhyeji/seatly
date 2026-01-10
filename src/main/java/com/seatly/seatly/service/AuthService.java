package com.seatly.seatly.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.global.Util;
import com.seatly.seatly.global.exception.InvalidTokenException;
import com.seatly.seatly.global.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenProvider jwtProvider;
  private final RedisService redisService;

  public ResponseCookie createAccessTokenCookie(Long userId,
      String username, UserRole role) {
    String token = jwtProvider.createAccessToken(
        userId, username, role.name());
    return Util.createAccessTokenCookie(token);
  }

  public ResponseCookie createRefreshTokenCookie(Long userId) {
    String token = jwtProvider.createRefreshToken(userId);
    redisService.saveRefreshToken(userId, token,
        jwtProvider.getRefreshTokenExpireMs());
    return Util.createRefreshTokenCookie(token);
  }

  public Long validateRefreshTokenAndGetUserId(HttpServletRequest request) {
    String refreshToken = Util.getCookieRefreshToken(request);

    if (!jwtProvider.validateToken(refreshToken)) {
      throw new InvalidTokenException();
    }

    Long userId = jwtProvider.getId(refreshToken);
    String savedToken = redisService.getRefreshToken(userId);

    if (!refreshToken.equals(savedToken)) {
      throw new InvalidTokenException();
    }
    return userId;
  }

  public void deleteRefreshToken(HttpServletRequest request) {
    String accessToken = Util.getCookieAccessToken(request);
    if (accessToken != null && jwtProvider.validateToken(accessToken)) {
      Long userId = jwtProvider.getId(accessToken);
      redisService.deleteRefreshToken(userId);
    }
  }

}
