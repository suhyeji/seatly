package com.seatly.seatly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.enums.UserCafeLinkType;

public interface UserStudyCafeLinkRepository
    extends JpaRepository<UserStudyCafeLink, Long> {

  Optional<UserStudyCafeLink> findByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);

  boolean existsByStudyCafeIdAndUserId(
      Long studyCafeId,
      Long userId);

  List<UserStudyCafeLink> findAllByUserId(Long userId);

  List<UserStudyCafeLink> findAllByUserIdAndLinkType(
      Long userId,
      UserCafeLinkType linkType);

}
