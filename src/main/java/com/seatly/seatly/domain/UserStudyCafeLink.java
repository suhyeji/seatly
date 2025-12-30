package com.seatly.seatly.domain;

import java.time.LocalDateTime;

import com.seatly.seatly.domain.enums.UserCafeLinkType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_study_cafe_link", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "study_cafe_id", "user_id" })
})
public class UserStudyCafeLink {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_cafe_id", nullable = false)
  private StudyCafe studyCafe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserCafeLinkType linkType;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
