package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.StudyCafeMember;

import java.util.Optional;

public interface StudyCafeMemberRepository
    extends JpaRepository<StudyCafeMember, Long> {

  Optional<StudyCafeMember> findByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);

  boolean existsByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);
}
