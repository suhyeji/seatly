package com.seatly.seatly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimePass {

  private Long studyCafeId;
  private Long userId;
  private Long leftTime; // 단위: seconds
  private Long totalTime; // 단위: seconds

}
