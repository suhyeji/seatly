package com.seatly.seatly.service;

import org.springframework.stereotype.Service;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.login.LoginRequest;

@Service
public class AuthService {

  public void login(LoginRequest request) {
    //
  }

  public void logout(CustomUserDetails user) {
    // TODO: 쿠키삭제
  }

}
