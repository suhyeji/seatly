package com.seatly.seatly.service;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.login.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthService {

  public void login(LoginRequest request) {
    //
  }

  public void logout(HttpServletRequest request) {
    // TODO: 쿠키삭제
  }

}
