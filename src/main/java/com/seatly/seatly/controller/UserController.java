package com.seatly.seatly.controller;

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
import com.seatly.seatly.dto.user.UserInfoDetail;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
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
  public UserInfoDetail getUserInfoDetail(@AuthenticationPrincipal CustomUserDetails user) {
    return userService.getUserInfoDetail(user.getId());
  }

  @PatchMapping
  public UserInfoDetail updateUserInfo(@AuthenticationPrincipal CustomUserDetails user,
      @RequestBody UserPatch userPatch) {
    return userService.updateUserInfo(user.getId(), userPatch);
  }

  @PutMapping
  public void updateUserPassword(@AuthenticationPrincipal CustomUserDetails user,
      @RequestBody UserPasswordPut passwordPut) {
    userService.updatePassword(user.getId(), passwordPut);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
    userService.deleteUser(user.getId());
  }

}
