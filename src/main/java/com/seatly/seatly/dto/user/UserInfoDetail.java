package com.seatly.seatly.dto.user;

import java.util.List;

import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.session.SessionInfo;

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

  private List<Long> favoriteCafeIds;
  private List<SessionInfo> sessions;
  private List<TimePass> timePasses;

}
