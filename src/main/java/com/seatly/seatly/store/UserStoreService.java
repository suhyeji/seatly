package com.seatly.seatly.store;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.seatly.seatly.domain.User;
import com.seatly.seatly.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStoreService {

  private final UserRepository store;

  public User findByIdOrNull(Long id) {
    return store.findById(id).orElse(null);
  }

  public User findByIdOrThrow(Long id) {
    return store.findById(id).orElseThrow(
        () -> new UsernameNotFoundException("User not found: " + id));
  }

  public Optional<User> findById(Long id) {
    return store.findById(id);
  }

  public User findByEmailOrNull(String email) {
    return store.findByEmail(email).orElse(null);
  }

  @Transactional
  public User save(User model) {
    return store.save(model);
  }

  public void deleteById(Long id) {
    store.deleteById(id);
  }

}
