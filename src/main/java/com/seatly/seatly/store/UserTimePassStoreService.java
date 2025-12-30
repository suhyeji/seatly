package com.seatly.seatly.store;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.repository.UserTimePassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTimePassStoreService {

  private final UserTimePassRepository store;

  public void deleteByTimePassId(UserTimePassId timePassId) {
    store.deleteById(timePassId);
  }

}
