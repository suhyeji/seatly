package com.seatly.seatly.dto.seat;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatPatch {

  private Long id;
  private String name;
  private SeatStatus status;
  private String position;

  public Seat patch(Seat entity) {
    if (name != null) {
      entity.setName(name);
    }
    if (status != null) {
      entity.setStatus(status);
    }
    if (position != null) {
      entity.setPosition(position);
    }
    return entity;
  }

}
