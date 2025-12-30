package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

  List<Seat> findByStudyCafeId(Long studyCafeId);

  List<Seat> findByStudyCafeIdAndStatus(
      Long studyCafeId,
      SeatStatus status);

  Optional<Seat> findByStudyCafeIdAndSeatNumber(
      Long studyCafeId,
      Integer seatNumber);

  long countByStudyCafeId(Long studyCafeId);

}
