package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.UserTimePass;

public interface UserTimePassRepository extends JpaRepository<UserTimePass, Long> {
}
