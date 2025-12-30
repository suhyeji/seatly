package com.seatly.seatly.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTimePassInfo {

  private Long userId;
  private String userName;
  private Long studyCafeId;
  private Long leftTime;
  private Long totalTime;

}
