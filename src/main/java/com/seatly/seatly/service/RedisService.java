package com.seatly.seatly.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  private final Duration lockDuration = Duration.ofSeconds(5);
  private final Duration assignDuration = Duration.ofMinutes(2);

  private static final String SEAT_LOCK_KEY = "seat:lock:";
  private static final String SEAT_SESSION_KEY = "seat:session:";
  private static final String USER_SESSION_KEY = "user:session:";
  private static final String SESSION_KEY = "session:";
  private static final String LOCK = "LOCK";
  private static final String SEAT_ID = "seatId";
  private static final String USER_ID = "userId";

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

  // 좌석 점유는 2분
  public void setSession(Long sessionId, Long userId, Long seatId) {
    String sid = sessionId.toString();
    redisTemplate.opsForValue().set(SEAT_SESSION_KEY + seatId, sid, assignDuration);
    redisTemplate.opsForValue().set(USER_SESSION_KEY + userId, sid, assignDuration);

    String sessionKey = SESSION_KEY + sid;
    redisTemplate.opsForHash().put(sessionKey, SEAT_ID, seatId);
    redisTemplate.opsForHash().put(sessionKey, USER_ID, userId);
    redisTemplate.expire(sessionKey, assignDuration);
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
    return Long.parseLong(redisTemplate.opsForValue()
        .get(SEAT_SESSION_KEY + seatId));
  }

  public Long getSessionIdByUserId(Long userId) {
    return Long.parseLong(redisTemplate.opsForValue()
        .get(USER_SESSION_KEY + userId));
  }

  public Long getSeatIdBySessionId(Long sessionId) {
    return (Long) redisTemplate.opsForHash()
        .get(SESSION_KEY + sessionId, SEAT_ID);
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
  }

}
