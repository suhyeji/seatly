package com.seatly.seatly.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "time")
public class Time {

  @Id
  private Long studyCafeMemberId;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_cafe_member_id")
  private StudyCafeMember member;

  @Column(nullable = false)
  private LocalDateTime leftTime;
}
