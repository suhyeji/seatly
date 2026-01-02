package com.seatly.seatly.dto.seat;

import com.seatly.seatly.domain.enums.SeatEventType;

public record SeatEvent(
    SeatEventType type,
    Long seatId) {
}