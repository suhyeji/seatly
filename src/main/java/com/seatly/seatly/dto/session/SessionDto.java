package com.seatly.seatly.dto.session;

import com.seatly.seatly.domain.enums.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionDto {

  private Long id;
  private int seatId;
  private SessionStatus status;
  private long startTime;

}
