package com.seatly.seatly.domain.keys;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTimePassId implements Serializable {

  @Column(name = "study_cafe_id")
  private Long studyCafeId;

  @Column(name = "user_id")
  private Long userId;

}