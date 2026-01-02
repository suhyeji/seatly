package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.global.exception.NotFoundException;
import com.seatly.seatly.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatStoreService {

  private final SeatRepository store;

  public Seat findByIdOrThrow(Long id) {
    return store.findById(id).orElseThrow(
        () -> new NotFoundException("Seat not found: " + id));
  }

  public List<Seat> getSeatsByStudyCafeId(Long studyCafeId) {
    return store.findByStudyCafeId(studyCafeId);
  }

  public long getSeatsCountByStudyCafeId(Long studyCafeId) {
    return store.countByStudyCafeId(studyCafeId);
  }

  public Seat getNullableSeatInfo(Long id) {
    return store.findById(id).orElse(null);
  }

  public void save(Seat seat) {
    store.save(seat);
  }

  public void deleteById(Long id) {
    store.deleteById(id);
  }

}
