package com.seatly.seatly.dto.seat;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatInfo {

  private Long id;
  private String name;
  private Long studyCafeId;
  private SeatStatus status;
  private String position;

  public SeatInfo(Seat entity, Long studyCafeId) {
    this.id = entity.getId();
    this.name = entity.getName();
    this.studyCafeId = studyCafeId;
    this.status = entity.getStatus();
    this.position = entity.getPosition();
  }

}
