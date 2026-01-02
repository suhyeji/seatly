package com.seatly.seatly.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.SeatEventType;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.dto.seat.SeatEvent;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.websocket.SeatWebSocketPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final RedisService redisService;
  private final SessionStoreService storeService;
  private final UserStoreService userStoreService;
  private final SeatStoreService seatStoreService;

  private final SeatWebSocketPublisher seatWebSocketPublisher;

  public List<SessionInfo> getSessions(Long studyCafeId) {
    return storeService.findSessionInfosByStudyCafeId(studyCafeId);
  }

  public List<Session> findExpiredInUseSessions(LocalDateTime now) {
    return storeService.getSessions().stream()
        .filter(session -> session.getStatus() == SessionStatus.IN_USE
            && session.getStartTime().isBefore(now))
        .toList();
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
  public Session assignSeat(Long userId, Long seatId) {
    if (!redisService.tryLockSeat(seatId)) {
      throw new IllegalStateException("이미 사용 중인 좌석입니다.");
    }

    try {
      User user = userStoreService.findByIdOrThrow(seatId);
      Seat seat = seatStoreService.findByIdOrThrow(seatId);

      Session session = createAssignedSession(user, seat);
      session = storeService.save(session);
      redisService.setSession(session.getId(), userId, seatId);
      return session;
    } finally {
      redisService.unlockSeat(seatId);
    }
  }

  @Transactional
  public Session autoAssignSeat(Long userId, Long studyCafeId) {
    User user = userStoreService.findByIdOrThrow(userId);

    // DB에서 AVAILABLE 좌석 목록 조회 (id로 정렬)
    List<Seat> seats = seatStoreService.findAllAvailableByStudyCafeId(studyCafeId);
    seats.sort(Comparator.comparing(Seat::getId));

    // 앞에서부터 Redis 선점 시도
    for (Seat seat : seats) {
      Long seatId = seat.getId();
      if (!redisService.tryLockSeat(seatId)) {
        continue; // 이미 다른 요청이 선점
      }

      try {
        Session session = createAssignedSession(user, seat);
        session = storeService.save(session);
        redisService.setSession(session.getId(), userId, seatId);
        return session;
      } catch (Exception e) {
        redisService.unlockSeat(seatId);
        throw e;
      }
    }

    // 배정 가능한 좌석이 하나도 없는 경우
    throw new NotFoundException("Available seat not found. studyCafeId=" + studyCafeId);
  }

  private Session createAssignedSession(User user, Seat seat) {
    Session session = new Session();
    session.setUser(user);
    session.setSeat(seat);
    session.setStatus(SessionStatus.ASSIGNED);
    return session;
  }

  @Transactional
  public void finishSession(Session session) {
    // session 종료
    Long studyCafeId = session.getSeat().getStudyCafe().getId();
    Long seatId = session.getSeat().getId();
    Long userId = session.getUser().getId();

    storeService.delete(session);

    redisService.getSeatSessionId(seatId);
    redisService.deleteUserSession(userId);

    seatWebSocketPublisher.publishToStudyCafe(
        studyCafeId,
        new SeatEvent(SeatEventType.USAGE_FINISHED, seatId));

    seatWebSocketPublisher.publishToUser(
        userId,
        new SeatEvent(SeatEventType.USAGE_FINISHED, seatId));
  }
}
