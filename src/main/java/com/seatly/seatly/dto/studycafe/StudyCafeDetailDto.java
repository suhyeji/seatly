package com.seatly.seatly.dto.studycafe;

import java.util.List;

import com.seatly.seatly.domain.enums.Facility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyCafeDetailDto {

  private Long id;
  private String name;
  private String address;
  private List<String> imageUrls;
  private String phoneNumber;
  private List<Facility> facilities;
  private String openingHours;
  private String description;
}
