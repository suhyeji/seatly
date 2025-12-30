package com.seatly.seatly.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.seatly.seatly.domain.enums.SeatStatus;

@Getter
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

  @Column(nullable = false)
  private Integer seatNumber;

  @Column(nullable = false)
  private String position;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private SeatStatus status;

  @Column(nullable = false)
  private LocalDateTime updatedAt;
}
