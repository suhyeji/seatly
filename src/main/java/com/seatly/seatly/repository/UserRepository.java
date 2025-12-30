package com.seatly.seatly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

  public Optional<User> findByEmail(String email);

}
