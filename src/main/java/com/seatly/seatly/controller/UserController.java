package com.seatly.seatly.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public UserInfo signIn(@RequestBody UserPost userPost) {
    return userService.signIn(userPost);
  }

  @GetMapping
  public UserInfo getUserInfo(HttpServletRequest request) {
    return userService.getUserInfo(request);
  }

  @PatchMapping
  public UserInfo updateUserInfo(HttpServletRequest request,
      @RequestBody UserPatch userPatch) {
    return userService.updateUserInfo(request, userPatch);
  }

  @PutMapping
  public void updateUserPassword(HttpServletRequest request,
      @RequestBody UserPasswordPut passwordPut) {
    userService.updatePassword(request, passwordPut);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(HttpServletRequest request) {
    userService.deleteUser(request);
  }

}
