package com.seatly.seatly.domain;

import java.time.OffsetDateTime;

import com.seatly.seatly.domain.enums.SeatStatus;

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
@Table(name = "seat", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "study_cafe_id", "seat_number" })
})
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_cafe_id", nullable = false)
  private StudyCafe studyCafe;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(nullable = false)
  private String position;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private SeatStatus status;

  @Column(nullable = false)
  private OffsetDateTime updatedAt;
}
