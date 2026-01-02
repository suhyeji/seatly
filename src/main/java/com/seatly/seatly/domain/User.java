package com.seatly.seatly.domain;

import java.time.OffsetDateTime;

import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.global.Util;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(length = 255)
  private String email;

  @Column(length = 50)
  private String phone;

  @Column
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;

  @Column(nullable = false, updatable = false)
  private OffsetDateTime createdAt = Util.now();

}
