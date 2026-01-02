package com.seatly.seatly.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.UserStoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final RedisService redisService;
  private final SessionStoreService storeService;
  private final UserStoreService userStoreService;
  private final SeatStoreService seatStoreService;

  public List<SessionInfo> getSessions() {
    return new ArrayList<>();
  }

  public SessionInfo startSession(Long id, SessionInfo body) {
    // session 상태 변경
    // TODO: 관리자 계정 확인 -> 관리자는 관리자의 studycafe seat의 세션만 종료할 수 있음
    // TODO: user 계정 확인 -> 본인 세션만 종료 가능해야함
    return null;
  }

  public void endSession(Long id) {
    // TODO: 관리자 계정 확인 -> 관리자는 관리자의 studycafe seat의 세션만 종료할 수 있음
    // TODO: user 계정 확인 -> 본인 세션만 종료 가능해야함
    // session에서 삭제
  }

  @Transactional
  public Long assignSeat(Long userId, Long seatId) {
    Boolean locked = redisService.tryLockSeat(seatId);

    if (Boolean.FALSE.equals(locked)) {
      throw new IllegalStateException("이미 사용 중인 좌석입니다.");
    }

    try {
      Session session = new Session();
      User user = userStoreService.findByIdOrThrow(seatId);
      Seat seat = seatStoreService.findByIdOrThrow(seatId);
      session.setUser(user);
      session.setSeat(seat);
      session.setStatus(SessionStatus.ASSIGNED);

      session = storeService.save(session);
      Long sessionId = session.getId();

      redisService.setSeatSession(seatId, sessionId);
      redisService.setUserSession(userId, sessionId);

      return sessionId;
    } finally {
      redisService.unlockSeat(seatId);
    }
  }

  public void autoAssignSession(Long userId, Long studyCafeId) {
    // studyCafeId로 좌석 목록 조회 (statud AVAILABLE)
    // 좌석 목록에서 session이 없는 좌석 중 숫자가 가장 작은 것 선택
    // session 생성
  }
}
