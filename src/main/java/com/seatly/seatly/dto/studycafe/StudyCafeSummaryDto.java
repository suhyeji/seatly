package com.seatly.seatly.dto.studycafe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyCafeSummaryDto {

  private Long id;
  private String name;
  private String mainImageUrl;
  private String address;

}
