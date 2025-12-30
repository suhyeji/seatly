package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.UserStudyCafeLink;

import java.util.Optional;

public interface UserStudyCafeLinkRepository
    extends JpaRepository<UserStudyCafeLink, Long> {

  Optional<UserStudyCafeLink> findByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);

  boolean existsByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);
}
