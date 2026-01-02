package com.seatly.seatly.domain;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.global.Util;
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
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<String> imageUrls;

  @Column(length = 20)
  private String phoneNumber;

  @Type(JsonType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private List<Facility> facilities;

  @Column(length = 100)
  private String openingHours;

  @Column(length = 50)
  private String description;

  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt = Util.now();

}
