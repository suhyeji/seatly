package com.seatly.seatly.store;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionStoreService {

  private final SessionRepository store;

  public List<Session> getSessions() {
    return store.findAll();
  }

  public Session findByIdOrNull(Long id) {
    return store.findById(id).orElse(null);
  }

  public Session findByIdOrThrow(Long id) {
    return store.findById(id).orElseThrow(
        () -> new NotFoundException("Session not found: " + id));
  }

  public Optional<Session> findById(Long id) {
    return store.findById(id);
  }

  public List<SessionInfo> findSessionInfosByStudyCafeId(Long studyCafeId) {
    return store.findSessionInfosByStudyCafeId(studyCafeId);
  }

  public List<SessionInfo> findSessionInfosByUserId(Long userId) {
    return store.findSessionInfosByUserId(userId);
  }

  public Session findBySeatId(Long seatId) {
    return store.findBySeatId(seatId).orElse(null);
  }

  public long getSessionCountByStudyCafeId(Long studyCafeId) {
    return store.countByStudyCafeId(studyCafeId);
  }

  @Transactional
  public Session save(Session session) {
    return store.save(session);
  }

  public void delete(Session session) {
    store.delete(session);
  }

  public void deleteById(Long id) {
    store.deleteById(id);
  }

}
