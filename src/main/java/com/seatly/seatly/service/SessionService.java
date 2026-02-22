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
import com.seatly.seatly.domain.enums.WebSocketEventType;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.websocket.SeatEvent;
import com.seatly.seatly.global.Util;
import com.seatly.seatly.global.exception.ForbiddenException;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;
import com.seatly.seatly.websocket.WebSocketPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final RedisService redisService;
  private final SessionStoreService storeService;
  private final UserStoreService userStoreService;
  private final SeatStoreService seatStoreService;
  private final UserTimePassStoreService userTimePassStoreService;

  private final WebSocketPublisher webSocketPublisher;

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
    sendWebSocketEventUser(userId, session.getSeat().getId(), WebSocketEventType.SEAT_USAGE_STARTED);

    return new SessionInfo(session);
  }

  @Transactional
  public void endSession(Long userId, boolean isAdmin, Long id) {
    if (!isAdmin && !id.equals(redisService.getSessionIdByUserId(userId))) {
      throw new ForbiddenException("사용자 정보와 세션 아이디가 일치하지 않습니다.");
    }

    storeService.findById(id).ifPresent(session -> {
      // TimePass 차감: IN_USE 상태이고 startTime이 존재하면 이용 시간만큼 leftTime 차감
      if (!session.getStatus().equals(SessionStatus.ASSIGNED) && session.getStartTime() != null) {
        Long studyCafeId = session.getSeat().getStudyCafe().getId();
        Long sessionUserId = session.getUser().getId();
        long usedSeconds = Duration.between(session.getStartTime(), Util.now()).getSeconds();
        UserTimePassId timePassId = new UserTimePassId(studyCafeId, sessionUserId);
        UserTimePass timePass = userTimePassStoreService.findById(timePassId);
        if (timePass != null) {
          long newLeftTime = timePass.getLeftTime() - usedSeconds;
          if (newLeftTime <= 0) {
            userTimePassStoreService.deleteByUserIdAndStudyCafeId(sessionUserId, studyCafeId);
          } else {
            timePass.setLeftTime(newLeftTime);
            userTimePassStoreService.save(timePass);
          }
        }
      }

      terminateSession(session);
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
    long studyCafeId = seat.getStudyCafe().getId();
    validateSession(userId, studyCafeId);

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
      sendWebSocketEventGlobal(studyCafeId, seatId, WebSocketEventType.SEAT_ASSIGNED);

      return new SessionInfo(session);
    } finally {
      redisService.unlockSeat(seatId);
    }
  }

  @Transactional
  public SessionInfo autoAssignSeat(Long userId, Long studyCafeId) {
    validateSession(userId, studyCafeId);

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
        sendWebSocketEventGlobal(seat.getStudyCafe().getId(), seatId, WebSocketEventType.SEAT_ASSIGNED);

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
  public void endByExpiration(Long sessionId) {
    Session session = storeService.findByIdOrThrow(sessionId);
    // 만료에 의한 종료: TimePass 전액 삭제
    Long studyCafeId = session.getSeat().getStudyCafe().getId();
    Long userId = session.getUser().getId();
    userTimePassStoreService.deleteByUserIdAndStudyCafeId(userId, studyCafeId);

    terminateSession(session);
  }

  /**
   * 세션 종료 공통 처리: 세션 삭제, Redis 삭제, WebSocket 알림 전송
   */
  private void terminateSession(Session session) {
    Long studyCafeId = session.getSeat().getStudyCafe().getId();
    Long seatId = session.getSeat().getId();
    Long userId = session.getUser().getId();

    WebSocketEventType eventType = session.getStatus().equals(SessionStatus.ASSIGNED)
        ? WebSocketEventType.SEAT_HOLD_RELEASED
        : WebSocketEventType.SEAT_USAGE_FINISHED;

    redisService.deleteSession(session.getId());
    storeService.delete(session);

    // Global - 좌석 이용 종료 알림 전송
    sendWebSocketEventGlobal(studyCafeId, seatId, eventType);
    // User - 좌석 이용 종료 알림 전송
    sendWebSocketEventUser(userId, seatId, eventType);
  }

  private void sendWebSocketEventUser(Long userId, Long seatId, WebSocketEventType type) {
    webSocketPublisher.publishSeatEventToUser(
        userId,
        new SeatEvent(type, seatId));
  }

  private void sendWebSocketEventGlobal(Long studyCafeId, Long seatId, WebSocketEventType type) {
    webSocketPublisher.publishSeatEventToStudyCafe(
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

  private void validateSession(Long userId, Long studyCafeId) {
    UserTimePass timePass = userTimePassStoreService.findById(new UserTimePassId(studyCafeId, userId));
    if (timePass == null || timePass.getLeftTime() <= 0) {
      throw new IllegalStateException("시간권이 존재하지 않거나 남은 시간이 없습니다.");
    }

    if (redisService.hasSessionByUserId(userId)) {
      throw new IllegalStateException("이미 이용 중인 세션이 존재합니다.");
    }
  }
}
