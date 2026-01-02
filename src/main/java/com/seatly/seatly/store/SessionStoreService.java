package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionStoreService {

  private final SessionRepository store;

  public List<SessionInfo> findSessionInfosByStudyCafeId(Long studyCafeId) {
    return store.findSessionInfosByStudyCafeId(studyCafeId);
  }

  public List<SessionInfo> findSessionInfosByUserId(Long userId) {
    return store.findSessionInfosByUserId(userId);
  }

  public long getSessionCountByStudyCafeId(Long studyCafeId) {
    return store.countByStudyCafeId(studyCafeId);
  }

  @Transactional
  public Session save(Session session) {
    return store.save(session);
  }

}
