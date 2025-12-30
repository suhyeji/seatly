package com.seatly.seatly.dto.studycafe;

import com.seatly.seatly.domain.StudyCafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyCafeSummary {

  private Long id;
  private String name;
  private String mainImageUrl;
  private String address;

  public StudyCafeSummary(StudyCafe entity) {
    this.id = entity.getId();
    this.name = entity.getName();
    this.mainImageUrl = entity.getImageUrls().get(0);
    this.address = entity.getAddress();
  }

}
