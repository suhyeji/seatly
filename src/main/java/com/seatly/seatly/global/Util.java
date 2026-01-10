package com.seatly.seatly.global;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Util {

  private static final String ACCESS_TOKEN = "accessToken";
  private static final String REFRESH_TOKEN = "refreshToken";

  public static OffsetDateTime now() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }

  public static ResponseCookie createAccessTokenCookie(String token) {
    return ResponseCookie.from(ACCESS_TOKEN, token)
        .httpOnly(true)
        // .secure(true) // https 사용 시
        .sameSite("Strict") // 필요시 Lax
        .path("/")
        .maxAge(Duration.ofHours(1))
        .build();
  }

  public static ResponseCookie createRefreshTokenCookie(String token) {
    return ResponseCookie.from(REFRESH_TOKEN, token)
        .httpOnly(true)
        // .secure(true)
        .sameSite("Strict")
        .path("/")
        .maxAge(Duration.ofDays(14))
        .build();
  }

  public static String getCookieAccessToken(HttpServletRequest request) {
    return getCookie(request, ACCESS_TOKEN);
  }

  public static String getCookieRefreshToken(HttpServletRequest request) {
    return getCookie(request, REFRESH_TOKEN);
  }

  public static String getCookie(HttpServletRequest request, String name) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (name.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  public static ResponseCookie deleteCookieAccessToken() {
    return deleteCookie(ACCESS_TOKEN);
  }

  public static ResponseCookie deleteCookieRefreshToken() {
    return deleteCookie(REFRESH_TOKEN);
  }

  public static ResponseCookie deleteCookie(String name) {
    return ResponseCookie.from(name, "")
        .path("/")
        .maxAge(0)
        .httpOnly(true)
        .build();
  }

}
