package com.seatly.seatly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.keys.UserTimePassId;

public interface UserTimePassRepository extends JpaRepository<UserTimePass, UserTimePassId> {
}
