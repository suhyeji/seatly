package com.seatly.seatly.dto.session;

import com.seatly.seatly.domain.enums.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfo {

  private Long id;
  private Long seatId;
  private Long studyCafeId;
  private Long userId;
  private SessionStatus status;
  private Long startTime;

}
