package com.seatly.seatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SessionStatus;

public interface SessionRepository
    extends JpaRepository<Session, Long>, SessionRepositoryCustom {

  Optional<Session> findByUserIdAndStatus(
      Long userId,
      SessionStatus status);

  Optional<Session> findBySeatIdAndStatus(
      Long seatId,
      SessionStatus status);

  List<Session> findByUserId(Long userId);

  List<Session> findByStudyCafeId(Long studyCafeId);
}
