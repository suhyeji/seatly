package com.seatly.seatly.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.seatly.seatly.dto.seat.SeatEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatWebSocketPublisher {

  private final SimpMessagingTemplate messagingTemplate;

  // 글로벌 브로드캐스트
  public void publishToStudyCafe(Long studyCafeId, SeatEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/study-cafe/" + studyCafeId,
        event);
  }

  // 개인 알림
  public void publishToUser(Long userId, SeatEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/user/" + userId + "/seat-events",
        event);
  }

}
