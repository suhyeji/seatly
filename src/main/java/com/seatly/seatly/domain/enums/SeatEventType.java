package com.seatly.seatly.domain.enums;

public enum SeatEventType {
  ASSIGNED, // 좌석 점유 성공
  USAGE_STARTED, // 이용 시작
  USAGE_FINISHED, // 이용 종료
  HOLD_RELEASED // 좌석 점유 TTL 만료
}