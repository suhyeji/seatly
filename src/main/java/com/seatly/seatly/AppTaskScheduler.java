package com.seatly.seatly;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.service.SessionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppTaskScheduler {

  private final SessionService sessionService;

  @Scheduled(fixedDelay = 10000)
  public void finishExpiredSessions() {
    List<Session> expired = sessionService.findExpiredInUseSessions(LocalDateTime.now());

    for (Session session : expired) {
      sessionService.finishSession(session);
    }
  }

}
