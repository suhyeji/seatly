package com.seatly.seatly.dto.session;

import java.time.OffsetDateTime;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SessionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionInfo {

  private Long id;
  private Long seatId;
  private Long studyCafeId;
  private Long userId;
  private SessionStatus status;
  private OffsetDateTime startTime;

  public SessionInfo(Session session) {
    this.id = session.getId();
    this.seatId = session.getSeat().getId();
    this.studyCafeId = session.getSeat().getStudyCafe().getId();
    this.userId = session.getUser().getId();
    this.status = session.getStatus();
    this.startTime = session.getStartTime();
  }

}
