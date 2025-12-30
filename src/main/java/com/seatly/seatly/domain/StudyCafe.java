package com.seatly.seatly.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.seatly.seatly.domain.enums.Facility;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "study_cafe")
public class StudyCafe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 100, nullable = false)
  private String name;

  @Column(length = 255)
  private String address;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<String> imageUrls;

  @Column(length = 20)
  private String phoneNumber;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<Facility> facilities;

  @Column(length = 100)
  private String openingHours;

  @Column(length = 50)
  private String description;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
