package com.seatly.seatly.dto.user;

import java.util.List;

import com.seatly.seatly.domain.enums.UserLoginType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.SessionInfo;
import com.seatly.seatly.dto.TimePass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDetail extends UserInfo {

  private UserLoginType lastLoginType;
  private List<Long> favoriteCafeIds;
  private List<SessionInfo> sessions;
  private List<TimePass> timePasses;
  private UserRole role;

}
