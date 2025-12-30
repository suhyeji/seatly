package com.seatly.seatly.domain;

import com.seatly.seatly.domain.keys.UserTimePassId;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_time_pass")
public class UserTimePass {

  @EmbeddedId
  private UserTimePassId id;

  @MapsId("studyCafeId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_cafe_id", nullable = false)
  private StudyCafe studyCafe;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Long leftTime; // 단위: seconds

  @Column(nullable = false)
  private Long totalTime; // 단위: seconds
}
