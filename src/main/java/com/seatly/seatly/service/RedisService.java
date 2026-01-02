package com.seatly.seatly.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String SEAT_LOCK_KEY = "seat:lock:";
  private static final String SEAT_SESSION_KEY = "seat:session:";
  private static final String USER_SESSION_KEY = "user:session:";

  private static final String LOCK = "LOCK";

  public boolean tryLockSeat(Long seatId) {
    Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(SEAT_LOCK_KEY + seatId, LOCK, Duration.ofSeconds(5));
    return Boolean.TRUE.equals(result);
  }

  public void unlockSeat(Long seatId) {
    redisTemplate.delete(SEAT_LOCK_KEY + seatId);
  }

  // 좌석 점유는 2분
  public void setSeatSession(Long seatId, Long sessionId) {
    redisTemplate.opsForValue().set(SEAT_SESSION_KEY + seatId, sessionId.toString(),
        Duration.ofMinutes(2));
  }

  public void updateSeatSessionTime(Long seatId, Long seconds) {
    redisTemplate.expire(SEAT_SESSION_KEY + seatId,
        Duration.ofSeconds(seconds));
  }

  public Long getSeatSessionId(Long seatId) {
    return Long.parseLong(redisTemplate.opsForValue()
        .get(SEAT_SESSION_KEY + seatId));
  }

  // 좌석 점유는 2분
  public void setUserSession(Long userId, Long sessionId) {
    redisTemplate.opsForValue().set(USER_SESSION_KEY + userId, sessionId.toString(),
        Duration.ofMinutes(2));
  }

  public void updateUserSessionTime(Long userId, Long seconds) {
    redisTemplate.expire(USER_SESSION_KEY + userId,
        Duration.ofSeconds(seconds));
  }

  public Long getUserSessionId(Long userId) {
    return Long.parseLong(redisTemplate.opsForValue()
        .get(USER_SESSION_KEY + userId));
  }

}
