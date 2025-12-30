package com.seatly.seatly.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.seatly.seatly.dto.seat.SeatInfo;
import com.seatly.seatly.dto.seat.SeatPatch;
import com.seatly.seatly.dto.seat.SeatPost;

@Service
public class SeatService {

  public List<SeatInfo> getSeats(Long studyCafeId) {
    return null;
  }

  public void addSeat(Long studyCafeId, List<SeatPost> body) {
    // TODO: 관리자 계정 확인
  }

  public void updateSeats(Long studyCafeId, List<SeatPatch> body) {
    // TODO: 관리자 계정 확인
  }

  public void deleteSeat(Long studyCafeId, Long id) {
    // TODO: 관리자 계정 확인
  }

}
