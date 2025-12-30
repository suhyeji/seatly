package com.seatly.seatly.dto.user;

import java.util.List;

import com.seatly.seatly.domain.enums.UserLoginType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.SessionInfo;
import com.seatly.seatly.dto.TimePass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  private Long id;
  private String email;
  private String name;
  private String phone;
  private String imageUrl;
  private UserLoginType lastLoginType;
  private List<Long> favoriteCafeIds;
  private List<SessionInfo> sessions;
  private List<TimePass> timePasses;
  private UserRole role;

}
