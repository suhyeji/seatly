package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionStoreService {

  private final SessionRepository store;

  public List<SessionInfo> findSessionInfosByUserId(Long userId) {
    return store.findSessionInfosByUserId(userId);
  }

  public List<Session> getEntitiesByStudyCafeId(Long studyCafeId) {
    return store.findByStudyCafeId(studyCafeId);
  }

}
