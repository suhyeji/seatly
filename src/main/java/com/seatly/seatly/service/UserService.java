package com.seatly.seatly.service;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

  public UserInfo signIn(UserPost userPost) {
    return new UserInfo();
  }

  public UserInfo getUserInfo(HttpServletRequest request) {
    return new UserInfo();
  }

  public UserInfo updateUserInfo(HttpServletRequest request, UserPatch userPatch) {
    return new UserInfo();
  }

  public void updatePassword(HttpServletRequest request, UserPasswordPut passwordPut) {
    //
  }

  public void deleteUser(HttpServletRequest request) {
    // TODO: implement
  }

}
