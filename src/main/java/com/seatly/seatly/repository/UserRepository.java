package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
