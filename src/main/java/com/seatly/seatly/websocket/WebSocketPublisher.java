package com.seatly.seatly.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.seatly.seatly.dto.websocket.SeatEvent;
import com.seatly.seatly.dto.websocket.TimePassRequestEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketPublisher {

  private final SimpMessagingTemplate messagingTemplate;

  // 글로벌 브로드캐스트
  public void publishSeatEventToStudyCafe(Long studyCafeId, SeatEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/study-cafe/" + studyCafeId,
        event);
  }

  // 개인 알림
  public void publishSeatEventToUser(Long userId, SeatEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/user/" + userId + "/seat-events",
        event);
  }

  // 개인 알림
  public void publishTimePassReqEventToUser(Long userId, TimePassRequestEvent event) {
    messagingTemplate.convertAndSend(
        "/topic/user/" + userId + "/time-pass-request-events",
        event);
  }

}
