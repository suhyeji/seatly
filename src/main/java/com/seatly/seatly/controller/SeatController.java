package com.seatly.seatly.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seatly.seatly.dto.seat.SeatInfo;
import com.seatly.seatly.dto.seat.SeatPatch;
import com.seatly.seatly.dto.seat.SeatPost;
import com.seatly.seatly.service.SeatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/study-cafes/{studyCafeId}/seats")
@RequiredArgsConstructor
public class SeatController {

  private final SeatService seatService;

  @GetMapping
  public List<SeatInfo> getSeats(@PathVariable Long studyCafeId) {
    return seatService.getSeats(studyCafeId);
  }

  @PostMapping
  public void addSeat(@PathVariable Long studyCafeId, @RequestBody List<SeatPost> body) {
    seatService.addSeat(studyCafeId, body);
  }

  @PatchMapping
  public void updateSeats(@PathVariable Long studyCafeId, @RequestBody List<SeatPatch> body) {
    seatService.updateSeats(studyCafeId, body);
  }

  @DeleteMapping("/{id}")
  public void deleteSeat(@PathVariable Long studyCafeId, @PathVariable Long id) {
    seatService.deleteSeat(studyCafeId, id);
  }

}
