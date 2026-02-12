package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.global.exception.UnauthorizedException;
import com.seatly.seatly.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@RequestBody UserPost userPost) {
    userService.signUp(userPost);
  }

  @GetMapping
  public UserInfo getUserInfo(@AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    return userService.getUserInfo(user.getId());
  }

  @PatchMapping
  public void updateUserInfo(@AuthenticationPrincipal CustomUserDetails user,
      @RequestBody UserPatch userPatch) {
    validateUser(user);
    userService.updateUserInfo(user.getId(), userPatch);
  }

  @PutMapping("/password")
  public void updateUserPassword(@AuthenticationPrincipal CustomUserDetails user,
      @RequestBody UserPasswordPut passwordPut) {
    userService.updatePassword(user.getId(), passwordPut);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    userService.deleteUser(user.getId());
  }

  @GetMapping("/study-cafes/favorite")
  public List<Long> getFavoriteStudyCafeIds(@AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    return userService.getFavoriteStudyCafeIds(user.getId());
  }

  @GetMapping("/time-passes")
  public List<TimePass> getTimePasses(@AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    return userService.getTimePasses(user.getId());
  }

  @GetMapping("/sessions")
  public List<SessionInfo> getSessions(@AuthenticationPrincipal CustomUserDetails user) {
    validateUser(user);
    return userService.getSessions(user.getId());
  }

  private void validateUser(CustomUserDetails user) {
    if (user == null) {
      throw new UnauthorizedException("Authentication token is required.");
    }
  }

}
