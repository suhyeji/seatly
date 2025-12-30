package com.seatly.seatly.dto.studycafe;

import java.util.List;

import com.seatly.seatly.domain.StudyCafe;
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

  public StudyCafe insert() {
    StudyCafe entity = new StudyCafe();
    entity.setName(name);
    entity.setAddress(address);
    entity.setImageUrls(images);
    entity.setPhoneNumber(phoneNumber);
    entity.setFacilities(facilities);
    entity.setOpeningHours(openingHours);
    entity.setDescription(description);
    return entity;
  }

  public StudyCafe update(StudyCafe entity) {

    if (name != null) {
      entity.setName(name);
    }
    if (address != null) {
      entity.setAddress(address);
    }
    if (images != null) {
      entity.setImageUrls(images);
    }
    if (phoneNumber != null) {
      entity.setPhoneNumber(phoneNumber);
    }
    if (facilities != null) {
      entity.setFacilities(facilities);
    }
    if (openingHours != null) {
      entity.setOpeningHours(openingHours);
    }
    if (description != null) {
      entity.setDescription(description);
    }

    return entity;
  }

}
