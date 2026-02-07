package com.seatly.seatly.dto.timepass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimePassRequest {

  private Long id;
  private Long userId;
  private Long studyCafeId;
  private Long time;

}
