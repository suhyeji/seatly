package com.seatly.seatly.dto.seat;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.enums.SeatStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatPost {

  private String name;
  private SeatStatus status;
  private String position;

  public Seat insert() {
    Seat entity = new Seat();
    entity.setName(name);
    entity.setStatus(status);
    entity.setPosition(position);
    return entity;
  }

}
