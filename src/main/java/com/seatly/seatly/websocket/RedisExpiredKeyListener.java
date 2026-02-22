package com.seatly.seatly.websocket;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.service.RedisService;
import com.seatly.seatly.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpiredKeyListener implements MessageListener {

  private final SessionService sessionService;

  private final RedisService redisService;

  private static final String SESSION_KEY = "session:";

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String key = message.toString();
    log.info("[REDIS-EXPIRED] key={}", key);

    // session: 시작하는 redis만 처리
    if (!key.startsWith(SESSION_KEY)) {
      return;
    }

    // 만료 이벤트를 타는 순간 이미 redis에서는 삭제된 상태 -> DB에서 조회
    Long sessionId = Long.parseLong(key.split(":")[1]);
    SessionStatus status = redisService.getSessionMetaStatusBySessionId(sessionId);
    Long seatId = redisService.getMetaSeatIdBySessionId(sessionId);

    if (status == null || seatId == null) {
      log.warn("[REDIS-EXPIRED] meta missing. sessionId={}", sessionId);
      return;
    }

    // metadata 삭제
    redisService.deleteSessionMeta(sessionId);

    // DB 삭제
    sessionService.endByExpiration(sessionId);
  }
}
