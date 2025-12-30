package com.seatly.seatly.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.seatly.seatly.domain.enums.SeatStatus;

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
  private LocalDateTime updatedAt;
}
