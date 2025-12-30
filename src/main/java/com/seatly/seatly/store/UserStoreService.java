package com.seatly.seatly.store;

import org.springframework.stereotype.Service;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStoreService {

  private final UserRepository store;

  public User getNullableUserInfo(Long id) {
    return store.findById(id).orElse(null);
  }

  public User getNullableUserInfoByEmail(String email) {
    return store.findByEmail(email).orElse(null);
  }

}
