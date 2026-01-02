package com.seatly.seatly.websocket;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SeatEventType;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.dto.seat.SeatEvent;
import com.seatly.seatly.service.SeatService;
import com.seatly.seatly.store.SessionStoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpiredKeyListener implements MessageListener {

  private final SeatWebSocketPublisher publisher;
  private final SeatService seatService;
  private final SessionStoreService sessionStoreService;

  private static final String SEAT_SESSION_KEY = "seat:session:";

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String key = message.toString();
    log.info("[REDIS-EXPIRED] key={}", key);

    if (!key.startsWith(SEAT_SESSION_KEY)) {
      return;
    }

    Long seatId = Long.parseLong(key.replace(SEAT_SESSION_KEY, ""));

    Session session = sessionStoreService.findBySeatId(seatId);
    if (session != null && session.getStatus() == SessionStatus.ASSIGNED) {

      Long studyCafeId = seatService.getSeatById(seatId).getStudyCafe().getId();

      publisher.publishToStudyCafe(
          studyCafeId,
          new SeatEvent(SeatEventType.HOLD_RELEASED, seatId));
    }
  }
}
