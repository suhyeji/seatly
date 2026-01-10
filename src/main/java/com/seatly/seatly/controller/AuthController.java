package com.seatly.seatly.controller;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.dto.login.LoginRequest;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.global.Util;
import com.seatly.seatly.service.AuthService;
import com.seatly.seatly.service.UserService;
import com.seatly.seatly.store.UserStoreService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;
  private final UserService userService;
  private final UserStoreService userStoreService;

  @PostMapping("/login")
  public ResponseEntity<UserInfo> login(@RequestBody LoginRequest request) {
    Pair<Long, UserInfo> result = userService.login(request);
    Long userId = result.getFirst();
    UserInfo userInfo = result.getSecond();

    ResponseCookie accessCookie = service.createAccessTokenCookie(
        userId, userInfo.getName(), userInfo.getRole());
    ResponseCookie refreshCookie = service.createRefreshTokenCookie(userId);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(userInfo);
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refresh(HttpServletRequest request) {
    Long userId = service.validateRefreshTokenAndGetUserId(request);
    User user = userStoreService.findByIdOrThrow(userId);

    ResponseCookie accessCookie = service.createAccessTokenCookie(
        userId, user.getName(), user.getRole());
    ResponseCookie refreshCookie = service.createRefreshTokenCookie(userId);

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    service.deleteRefreshToken(request);

    ResponseCookie accessCookie = Util.deleteCookieAccessToken();
    ResponseCookie refreshCookie = Util.deleteCookieRefreshToken();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .build();
  }

}
