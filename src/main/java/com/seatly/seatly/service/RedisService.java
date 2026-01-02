package com.seatly.seatly.service;

import java.time.Duration;

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
  private static final String LOCK = "LOCK";

  public boolean tryLockSeat(Long seatId) {
    Boolean result = redisTemplate.opsForValue()
        .setIfAbsent(SEAT_LOCK_KEY + seatId, LOCK, lockDuration);
    return Boolean.TRUE.equals(result);
  }

  public void unlockSeat(Long seatId) {
    redisTemplate.delete(SEAT_LOCK_KEY + seatId);
  }

  // 좌석 점유는 2분
  public void setSession(Long sessionId, Long userId, Long seatId) {
    redisTemplate.opsForValue().set(SEAT_SESSION_KEY + seatId,
        sessionId.toString(), assignDuration);
    redisTemplate.opsForValue().set(USER_SESSION_KEY + userId,
        sessionId.toString(), assignDuration);
  }

  public void updateSeatSessionTime(Long seatId, Long seconds) {
    redisTemplate.expire(SEAT_SESSION_KEY + seatId,
        Duration.ofSeconds(seconds));
  }

  public Long getSeatSessionId(Long seatId) {
    return Long.parseLong(redisTemplate.opsForValue()
        .get(SEAT_SESSION_KEY + seatId));
  }

  public void updateUserSessionTime(Long userId, Long seconds) {
    redisTemplate.expire(USER_SESSION_KEY + userId,
        Duration.ofSeconds(seconds));
  }

  public Long getUserSessionId(Long userId) {
    return Long.parseLong(redisTemplate.opsForValue()
        .get(USER_SESSION_KEY + userId));
  }

  public void deleteUserSession(Long userId) {
    redisTemplate.delete(USER_SESSION_KEY + userId);
  }

}
