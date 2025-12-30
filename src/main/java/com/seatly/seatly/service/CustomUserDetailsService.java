package com.seatly.seatly.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.store.UserStoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserStoreService userStoreService;

  @Override
  public UserDetails loadUserByUsername(String userId)
      throws UsernameNotFoundException {

    User user = userStoreService.findById(Long.valueOf(userId))
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

    return new CustomUserDetails(user);
  }

}
