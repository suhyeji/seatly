package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserTimePassInfo;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.global.exception.UnauthorizedException;
import com.seatly.seatly.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

  private final UserService userService;

  @GetMapping
  public List<UserTimePassInfo> getUsersTimeInfo(@AuthenticationPrincipal CustomUserDetails user,
      @RequestParam Long studyCafeId) {
    validateUser(user);
    validateAdmin(user);
    return userService.getUsersTimeInfo(studyCafeId);
  }

  @GetMapping(path = "/{id}")
  public UserInfo getUserInfo(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    validateUser(user);
    validateAdmin(user);
    return userService.getUserInfo(id);
  }

  private void validateUser(CustomUserDetails user) {
    if (user == null) {
      throw new UnauthorizedException("Authentication token is required.");
    }
  }

  private void validateAdmin(CustomUserDetails user) {
    if (!user.isAdmin()) {
      throw new ForbiddenException("관리자 권한이 필요합니다.");
    }
  }

}
