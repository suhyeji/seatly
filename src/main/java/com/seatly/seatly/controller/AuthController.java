package com.seatly.seatly.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.login.LoginRequest;
import com.seatly.seatly.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public void login(@RequestBody LoginRequest request) {
    authService.login(request);
  }

  @PostMapping("/logout")
  public void logout(HttpServletRequest request) {
    authService.logout(request);
  }
}
