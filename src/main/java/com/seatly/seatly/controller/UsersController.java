package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserTimeInfo;
import com.seatly.seatly.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

  private final UserService userService;

  @GetMapping
  public List<UserTimeInfo> getUsersTimeInfo(@RequestParam Long studyCafeId) {
    return userService.getUsersTimeInfo(studyCafeId);
  }

  @GetMapping(path = "/{userId}")
  public UserInfo getUserInfo(@PathVariable Long userId) {
    return userService.getUserInfo(userId);
  }

}
