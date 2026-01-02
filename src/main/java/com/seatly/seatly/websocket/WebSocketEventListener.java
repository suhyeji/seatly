package com.seatly.seatly.websocket;

import java.nio.charset.StandardCharsets;

import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SeatEventType;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.dto.seat.SeatEvent;
import com.seatly.seatly.service.SeatService;
import com.seatly.seatly.store.SessionStoreService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

  private final SeatWebSocketPublisher publisher;
  private final SeatService seatService;
  private final SessionStoreService sessionStoreService;

  private static final String SEAT_SESSION_KEY = "seat:session:";

  @EventListener
  public void onKeyExpired(RedisKeyExpiredEvent<byte[]> event) {
    byte[] raw = event.getSource();
    String key = new String(raw, StandardCharsets.UTF_8);

    if (!key.startsWith(SEAT_SESSION_KEY))
      return;

    Long seatId = Long.parseLong(key.replace(SEAT_SESSION_KEY, ""));
    // DB에 IN_USE 세션이 있으면 → 이용 종료 아님 (스케줄러가 처리)
    // Session table에 seatId로 찾은 데이터의 status가 ASSIGNED이면 해당 좌석을 점유 해제
    Session session = sessionStoreService.findBySeatId(seatId);
    if (session != null && session.getStatus() == SessionStatus.ASSIGNED) {

      Long studyCafeId = seatService.getSeatById(seatId).getStudyCafe().getId();

      publisher.publishToStudyCafe(
          studyCafeId,
          new SeatEvent(SeatEventType.HOLD_RELEASED, seatId));
    }
  }
}
