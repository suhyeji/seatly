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

@Service
public class UserService {

  public UserInfoDetail signUp(UserPost userPost) {
    return new UserInfoDetail();
  }

  public UserInfoDetail getUserInfoDetail(String email) {
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

  public UserInfoDetail updateUserInfo(String email, UserPatch userPatch) {
    return new UserInfoDetail();
  }

  public void updatePassword(String email, UserPasswordPut passwordPut) {
    //
  }

  public void deleteUser(String email) {
    // TODO: implement
  }

}
