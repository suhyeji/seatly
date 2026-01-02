package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

  List<Seat> findAllByStudyCafeId(Long studyCafeId);

  List<Seat> findAllByStudyCafeIdAndStatus(
      Long studyCafeId,
      SeatStatus status);

  Optional<Seat> findByStudyCafeIdAndName(
      Long studyCafeId,
      String name);

  long countByStudyCafeId(Long studyCafeId);

}
