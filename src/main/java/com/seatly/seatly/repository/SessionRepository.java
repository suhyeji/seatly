package com.seatly.seatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.dto.session.SessionInfo;

import io.lettuce.core.dynamic.annotation.Param;

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

  @Query("""
          select new com.seatly.seatly.domain.session.dto.SessionInfo(
              s.id,
              seat.id,
              seat.studyCafe.id,
              s.user.id,
              s.status,
              s.startTime
          )
          from Session s
          join s.seat seat
          where s.user.id = :userId
      """)
  List<SessionInfo> findSessionInfosByUserId(
      @Param("userId") Long userId);
}
