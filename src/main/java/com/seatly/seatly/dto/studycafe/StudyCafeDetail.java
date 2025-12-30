package com.seatly.seatly.dto.studycafe;

import java.util.List;

import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.enums.Facility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyCafeDetail {

  private Long id;
  private String name;
  private String address;
  private List<String> imageUrls;
  private String phoneNumber;
  private List<Facility> facilities;
  private String openingHours;
  private String description;

  public StudyCafeDetail(StudyCafe entity) {
    this.id = entity.getId();
    this.name = entity.getName();
    this.address = entity.getAddress();
    this.imageUrls = entity.getImageUrls();
    this.phoneNumber = entity.getPhoneNumber();
    this.facilities = entity.getFacilities();
    this.openingHours = entity.getOpeningHours();
    this.description = entity.getDescription();
  }

}
