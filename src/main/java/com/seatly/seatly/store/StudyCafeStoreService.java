package com.seatly.seatly.store;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.repository.StudyCafeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyCafeStoreService {

  private final StudyCafeRepository store;

  public List<StudyCafe> findAll() {
    return store.findAll();
  }

  public StudyCafe findByIdOrNull(Long id) {
    return store.findById(id).orElse(null);
  }

  public StudyCafe findByIdOrThrow(Long id) {
    return store.findById(id).orElseThrow();
  }

  public StudyCafe save(StudyCafe studyCafe) {
    return store.save(studyCafe);
  }

  public void deleteById(Long id) {
    store.deleteById(id);
  }

}
