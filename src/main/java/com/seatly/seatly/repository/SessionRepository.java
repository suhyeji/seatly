package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface SessionRepository
    extends JpaRepository<Session, Long> {

  Optional<Session> findByUserIdAndStatus(
      Long userId,
      SessionStatus status);

  Optional<Session> findBySeatIdAndStatus(
      Long seatId,
      SessionStatus status);

  List<Session> findByUserId(Long userId);
}
