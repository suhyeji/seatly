package com.seatly.seatly.domain.enums;

public enum WebSocketEventType {
  SEAT_ASSIGNED, // 좌석 점유 성공
  SEAT_USAGE_STARTED, // 이용 시작
  SEAT_USAGE_FINISHED, // 이용 종료
  SEAT_HOLD_RELEASED, // 좌석 점유 TTL 만료
  TIMEPASS_REQUEST, // 시간 요금 요청
  TIMEPASS_REQUEST_ACCEPTED, // 시간 요금 요청 수락
  TIMEPASS_REQUEST_REJECTED, // 시간 요금 요청 거부
  ;
}