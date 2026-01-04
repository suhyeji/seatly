package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserTimePassInfo;
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
    if (!user.isAdmin()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
    }
    return userService.getUsersTimeInfo(studyCafeId);
  }

  @GetMapping(path = "/{id}")
  public UserInfo getUserInfo(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id) {
    if (!user.isAdmin()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
    }
    return userService.getUserInfo(id);
  }

  @PostMapping("/{id}/time")
  public void addUserTimePass(@AuthenticationPrincipal CustomUserDetails user,
      @PathVariable Long id,
      @RequestParam Long studyCafeId,
      @RequestParam Long time) {
    if (!user.isAdmin()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
    }
    userService.addUserTimePass(id, studyCafeId, time);
  }

}
