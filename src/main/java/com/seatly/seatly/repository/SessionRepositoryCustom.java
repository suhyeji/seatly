package com.seatly.seatly.repository;

import java.util.List;

import com.seatly.seatly.domain.Session;

public interface SessionRepositoryCustom {

  List<Session> findByStudyCafeId(Long studyCafeId);

}
