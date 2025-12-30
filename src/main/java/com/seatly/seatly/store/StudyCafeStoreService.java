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

  public List<StudyCafe> getStudyCafes() {
    return store.findAll();
  }

  public StudyCafe getNullableStudyCafeInfo(Long id) {
    return store.findById(id).orElse(null);
  }

}
