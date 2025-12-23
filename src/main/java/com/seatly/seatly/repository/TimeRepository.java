package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.Time;

public interface TimeRepository
    extends JpaRepository<Time, Long> {
}
