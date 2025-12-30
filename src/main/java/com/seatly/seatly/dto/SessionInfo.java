package com.seatly.seatly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {
  private Long id;
  private Long userId;
  private Long seatId;
  private Long startTime;
}
