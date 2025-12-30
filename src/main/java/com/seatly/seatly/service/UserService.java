package com.seatly.seatly.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.user.UserInfo;
import com.seatly.seatly.dto.user.UserInfoDetail;
import com.seatly.seatly.dto.user.UserPasswordPut;
import com.seatly.seatly.dto.user.UserPatch;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.dto.user.UserTimeInfo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

  public UserInfoDetail signIn(UserPost userPost) {
    return new UserInfoDetail();
  }

  public UserInfoDetail getUserInfoDetail(HttpServletRequest request) {
    return new UserInfoDetail();
  }

  // 관리자만 가능
  public UserInfo getUserInfo(Long id) {
    return new UserInfo();
  }

  // 관리자만 가능
  public List<UserTimeInfo> getUsersTimeInfo(Long studyCafeId) {
    return new ArrayList<>();
  }

  public UserInfoDetail updateUserInfo(HttpServletRequest request, UserPatch userPatch) {
    return new UserInfoDetail();
  }

  public void updatePassword(HttpServletRequest request, UserPasswordPut passwordPut) {
    //
  }

  public void deleteUser(HttpServletRequest request) {
    // TODO: implement
  }

}
