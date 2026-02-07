package com.seatly.seatly.service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.enums.SeatEventType;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.seat.SeatEvent;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.global.Util;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;
import com.seatly.seatly.websocket.SeatWebSocketPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final RedisService redisService;
  private final SessionStoreService storeService;
  private final UserStoreService userStoreService;
  private final SeatStoreService seatStoreService;
  private final UserTimePassStoreService userTimePassStoreService;

  private final SeatWebSocketPublisher seatWebSocketPublisher;

  public List<SessionInfo> getSessions(Long studyCafeId) {
    return storeService.findSessionInfosByStudyCafeId(studyCafeId);
  }

  @Transactional
  public SessionInfo startSession(Long userId, Long id) {
    if (!id.equals(redisService.getSessionIdByUserId(userId))) {
      throw new ForbiddenException("사용자 정보와 세션 아이디가 일치하지 않습니다.");
    }
    Session session = storeService.findByIdOrThrow(id);
    session.setStatus(SessionStatus.IN_USE);
    session.setStartTime(Util.now());
    session = storeService.save(session);
    // redis에서 값 변경
    Long studyCafeId = session.getSeat().getStudyCafe().getId();
    UserTimePass timePass = userTimePassStoreService.findByIdOrThrow(
        new UserTimePassId(studyCafeId, userId));

    if (timePass.getLeftTime() <= 0) {
      throw new ForbiddenException("남은 시간이 존재하지 않습니다.");
    }
    Duration expire = Duration.ofSeconds(timePass.getLeftTime());

    redisService.startSession(id, userId, session.getSeat().getId(), expire);

    // User - 좌석 이용 시작 알림 전송
    sendWebSocketEventUser(userId, session.getSeat().getId(), SeatEventType.USAGE_STARTED);

    return new SessionInfo(session);
  }

  public void endSession(Long userId, boolean isAdmin, Long id) {
    if (!isAdmin && !id.equals(redisService.getSessionIdByUserId(userId))) {
      throw new ForbiddenException("사용자 정보와 세션 아이디가 일치하지 않습니다.");
    }
    storeService.findById(id).ifPresent(session -> {
      Long studyCafeId = session.getSeat().getStudyCafe().getId();
      Long seatId = session.getSeat().getId();
      Long sessionUserId = session.getUser().getId();

      redisService.deleteSession(session.getId());
      storeService.delete(session);

      // Global - 좌석 이용 종료 알림 전송
      sendWebSocketEventGlobal(studyCafeId, seatId, SeatEventType.USAGE_FINISHED);
      // User - 좌석 이용 종료 알림 전송
      sendWebSocketEventUser(sessionUserId, seatId, SeatEventType.USAGE_FINISHED);
    });
  }

  public List<Session> findExpiredInUseSessions() {
    OffsetDateTime now = Util.now();
    return storeService.getSessions().stream()
        .filter(session -> session.getStatus().equals(SessionStatus.IN_USE)
            && session.getStartTime().isBefore(now))
        .toList();
  }

  @Transactional
  public SessionInfo assignSeat(Long userId, Long seatId) {
    Seat seat = seatStoreService.findByIdOrThrow(seatId);
    if (SeatStatus.UNAVAILABLE.equals(seat.getStatus())) {
      throw new IllegalStateException("사용 불가능한 좌석입니다.");
    }

    if (!redisService.tryLockSeat(seatId) || redisService.hasSessionBySeatId(seatId)) {
      throw new IllegalStateException("이미 사용 중인 좌석입니다.");
    }

    try {
      User user = userStoreService.findByIdOrThrow(userId);
      Session session = createAssignedSession(user, seat);
      session = storeService.save(session);
      redisService.setAssignSession(session.getId(), userId, seatId);

      // Global - 좌석 점유 완료 알림 전송
      sendWebSocketEventGlobal(seat.getStudyCafe().getId(), seatId, SeatEventType.ASSIGNED);

      return new SessionInfo(session);
    } finally {
      redisService.unlockSeat(seatId);
    }
  }

  @Transactional
  public SessionInfo autoAssignSeat(Long userId, Long studyCafeId) {
    User user = userStoreService.findByIdOrThrow(userId);

    // DB에서 AVAILABLE 좌석 목록 조회 (id로 정렬)
    List<Seat> seats = seatStoreService.findAllAvailableByStudyCafeId(studyCafeId);
    seats.sort(Comparator.comparing(Seat::getId));

    // 앞에서부터 Redis 선점 시도
    for (Seat seat : seats) {
      Long seatId = seat.getId();
      if (redisService.hasSessionBySeatId(seatId) || !redisService.tryLockSeat(seatId)) {
        continue; // 이미 다른 요청이 선점
      }

      try {
        Session session = createAssignedSession(user, seat);
        session = storeService.save(session);
        redisService.setAssignSession(session.getId(), userId, seatId);

        // Global - 좌석 점유 완료 알림 전송
        sendWebSocketEventGlobal(seat.getStudyCafe().getId(), seatId, SeatEventType.ASSIGNED);

        return new SessionInfo(session);
      } catch (Exception e) {
        redisService.unlockSeat(seatId);
        throw e;
      }
    }

    // 배정 가능한 좌석이 하나도 없는 경우
    throw new NotFoundException("Available seat not found. studyCafeId=" + studyCafeId);
  }

  @Transactional
  public void finishSession(Long sessionId) {
    Session session = storeService.findByIdOrThrow(sessionId);
    // session 종료
    Long studyCafeId = session.getSeat().getStudyCafe().getId();
    Long seatId = session.getSeat().getId();
    Long userId = session.getUser().getId();

    storeService.delete(session);
    userTimePassStoreService.deleteByUserIdAndStudyCafeId(userId, studyCafeId);
    redisService.deleteSession(session.getId());

    SeatEventType type = SeatEventType.USAGE_FINISHED;
    if (session.getStatus().equals(SessionStatus.ASSIGNED)) {
      type = SeatEventType.HOLD_RELEASED;
    }

    // Global - 좌석 이용 종료 알림 전송
    sendWebSocketEventGlobal(studyCafeId, seatId, type);
    // User - 좌석 이용 종료 알림 전송
    sendWebSocketEventUser(userId, seatId, type);
  }

  private void sendWebSocketEventUser(Long userId, Long seatId, SeatEventType type) {
    seatWebSocketPublisher.publishToUser(
        userId,
        new SeatEvent(type, seatId));
  }

  private void sendWebSocketEventGlobal(Long studyCafeId, Long seatId, SeatEventType type) {
    seatWebSocketPublisher.publishToStudyCafe(
        studyCafeId,
        new SeatEvent(type, seatId));
  }

  private Session createAssignedSession(User user, Seat seat) {
    Session session = new Session();
    session.setUser(user);
    session.setSeat(seat);
    session.setStatus(SessionStatus.ASSIGNED);
    return session;
  }
}
