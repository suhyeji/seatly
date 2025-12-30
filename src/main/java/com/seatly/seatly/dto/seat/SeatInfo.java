package com.seatly.seatly.dto.seat;

import com.seatly.seatly.domain.enums.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatInfo {

  private Long id;
  private String name;
  private SeatStatus status;
  private String position;

}
