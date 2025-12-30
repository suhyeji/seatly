package com.seatly.seatly.dto;

import com.seatly.seatly.domain.enums.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatInfo {

  private Long id;
  private Long studyCafeId;
  private String position;
  private SeatStatus status;

}
