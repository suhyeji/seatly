package com.seatly.seatly.store;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatStoreService {

  private final SeatRepository store;

  public Seat findByIdOrNull(Long id) {
    return store.findById(id).orElse(null);
  }

  public Seat findByIdOrThrow(Long id) {
    return store.findById(id).orElseThrow(
        () -> new NotFoundException("Seat not found: " + id));
  }

  public Optional<Seat> findById(Long id) {
    return store.findById(id);
  }

  public List<Seat> findAllByStudyCafeId(Long studyCafeId) {
    return store.findAllByStudyCafeId(studyCafeId);
  }

  public List<Seat> findAllAvailableByStudyCafeId(Long studyCafeId) {
    return store.findAllByStudyCafeIdAndStatus(
        studyCafeId,
        SeatStatus.AVAILABLE);
  }

  public long getCountByStudyCafeId(Long studyCafeId) {
    return store.countByStudyCafeId(studyCafeId);
  }

  public Seat save(Seat seat) {
    return store.save(seat);
  }

  public void deleteById(Long id) {
    store.deleteById(id);
  }

}
