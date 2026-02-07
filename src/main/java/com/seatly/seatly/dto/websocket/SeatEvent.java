package com.seatly.seatly.dto.websocket;

import com.seatly.seatly.domain.enums.WebSocketEventType;

public record SeatEvent(
    WebSocketEventType type,
    Long seatId) {
}