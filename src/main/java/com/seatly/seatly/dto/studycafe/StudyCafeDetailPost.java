package com.seatly.seatly.dto.studycafe;

import java.util.List;

import com.seatly.seatly.domain.enums.Facility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class StudyCafeDetailPost {

  private Long id;
  private String name;
  private String address;
  private List<String> images;
  private String phoneNumber;
  private List<Facility> facilities;
  private String openingHours;
  private String description;

}
