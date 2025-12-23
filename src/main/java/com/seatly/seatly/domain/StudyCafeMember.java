package com.seatly.seatly.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "study_cafe_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "study_cafe_id", "user_id" })
})
public class StudyCafeMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_cafe_id", nullable = false)
  private StudyCafe studyCafe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
