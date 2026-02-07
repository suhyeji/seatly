package com.seatly.seatly.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.enums.SessionStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  private final Duration lockDuration = Duration.ofSeconds(5);
  private final Duration assignDuration = Duration.ofMinutes(2);

  // 좌석에 대한 락 (동시성 제어용)
  private static final String SEAT_LOCK_KEY = "seat:lock:";

  // 좌석 ID로 세션 ID 조회 (String: sessionId)
  private static final String SEAT_SESSION_KEY = "seat:session:";

  // 사용자 ID로 세션 ID 조회 (String: sessionId)
  private static final String USER_SESSION_KEY = "user:session:";

  // 세션 상세 정보 저장 (Hash: seatId, userId 등)
  private static final String SESSION_KEY = "session:";

  // 세션 만료 처리를 위한 메타 정보 (Hash: seatId, status)
  private static final String SESSION_KEY_META = "session:meta:";

  private static final String LOCK = "LOCK";
  private static final String SEAT_ID = "seatId";
  private static final String USER_ID = "userId";
  private static final String STATUS = "status";

  // 리프레시 토큰 저장 (String: refreshToken)
  private static final String REFRESH_TOKEN_KEY = "refreshToken:";

  public boolean tryLockSeat(Long seatId) {
    Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(SEAT_LOCK_KEY + seatId, LOCK, lockDuration);
    return Boolean.TRUE.equals(result);
  }

  public void unlockSeat(Long seatId) {
    redisTemplate.delete(SEAT_LOCK_KEY + seatId);
  }

  public boolean hasSessionByUserId(Long userId) {
    return Boolean.TRUE.equals(
        redisTemplate.hasKey(USER_SESSION_KEY + userId));
  }

  public boolean hasSessionBySeatId(Long seatId) {
    return Boolean.TRUE.equals(
        redisTemplate.hasKey(SEAT_SESSION_KEY + seatId));
  }

  public void startSession(Long sessionId, Long userId, Long seatId, Duration expire) {
    // 좌석 점유 상태였던 세션을 IN_USE로 변경 및 이용권 시간만큼 ttl 갱신
    setSession(sessionId, userId, seatId, SessionStatus.IN_USE, expire);
  }

  // 좌석 점유는 2분
  public void setAssignSession(Long sessionId, Long userId, Long seatId) {
    setSession(sessionId, userId, seatId, SessionStatus.ASSIGNED, assignDuration);
  }

  private void setSession(Long sessionId, Long userId, Long seatId, SessionStatus status, Duration expire) {
    String sid = sessionId.toString();
    String sessionKey = SESSION_KEY + sid;

    if (status.equals(SessionStatus.ASSIGNED)) {
      redisTemplate.opsForHash().put(sessionKey, SEAT_ID, seatId);
      redisTemplate.opsForHash().put(sessionKey, USER_ID, userId);
    }

    redisTemplate.opsForValue().set(SEAT_SESSION_KEY + seatId, sid, expire);
    redisTemplate.opsForValue().set(USER_SESSION_KEY + userId, sid, expire);

    // meta redis: expire + 10분 -> sessionKey expire 됐을 때 삭제
    String sessionMetaKey = SESSION_KEY_META + sid;
    redisTemplate.opsForHash().put(sessionMetaKey, SEAT_ID, seatId);
    redisTemplate.opsForHash().put(sessionMetaKey, STATUS, status);

    redisTemplate.expire(sessionKey, expire);
    redisTemplate.expire(sessionMetaKey, expire.plus(Duration.ofMinutes(10)));
  }

  public void extendSessionTimeByUserId(Long userId, Long seconds) {
    extendTtl(USER_SESSION_KEY + userId, seconds);
    Long sessionId = getSessionIdByUserId(userId);
    if (sessionId != null) {
      extendTtl(SESSION_KEY + sessionId, seconds);
    }
    Long seatId = getSeatIdBySessionId(sessionId);
    if (seatId != null) {
      extendTtl(SEAT_SESSION_KEY + seatId, seconds);
    }
  }

  private void extendTtl(String key, Long seconds) {
    Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
    if (ttl == null || ttl < 0) {
      ttl = 0L;
    }
    redisTemplate.expire(key, Duration.ofSeconds(ttl + seconds));
  }

  public Long getSessionIdBySeatId(Long seatId) {
    String result = redisTemplate.opsForValue().get(SEAT_SESSION_KEY + seatId);
    if (result != null) {
      return Long.parseLong(result);
    }
    return null;
  }

  public Long getSessionIdByUserId(Long userId) {
    String result = redisTemplate.opsForValue().get(USER_SESSION_KEY + userId);
    if (result != null) {
      return Long.parseLong(result);
    }
    return null;
  }

  public Long getSeatIdBySessionId(Long sessionId) {
    return (Long) redisTemplate.opsForHash()
        .get(SESSION_KEY + sessionId, SEAT_ID);
  }

  public Long getMetaSeatIdBySessionId(Long sessionId) {
    return (Long) redisTemplate.opsForHash()
        .get(SESSION_KEY_META + sessionId, SEAT_ID);
  }

  public SessionStatus getSessionMetaStatusBySessionId(Long sessionId) {
    return (SessionStatus) redisTemplate.opsForHash()
        .get(SESSION_KEY_META + sessionId, STATUS);
  }

  public void deleteSessionMeta(Long sessionId) {
    redisTemplate.delete(SESSION_KEY_META + sessionId);
  }

  public void deleteSession(Long sessionId) {
    String key = SESSION_KEY + sessionId;
    Object seatId = redisTemplate.opsForHash().get(key, SEAT_ID);
    Object userId = redisTemplate.opsForHash().get(key, USER_ID);

    if (seatId != null) {
      redisTemplate.delete(SEAT_SESSION_KEY + seatId);
    }
    if (userId != null) {
      redisTemplate.delete(USER_SESSION_KEY + userId);
    }
    redisTemplate.delete(key);

    // session meta 삭제
    deleteSessionMeta(sessionId);
  }

  public void saveRefreshToken(Long userId, String refreshToken, long expireMs) {
    redisTemplate.opsForValue().set(
        REFRESH_TOKEN_KEY + userId,
        refreshToken,
        Duration.ofMillis(expireMs));
  }

  public String getRefreshToken(Long userId) {
    return redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY + userId);
  }

  public void deleteRefreshToken(Long userId) {
    redisTemplate.delete(REFRESH_TOKEN_KEY + userId);
  }

}
