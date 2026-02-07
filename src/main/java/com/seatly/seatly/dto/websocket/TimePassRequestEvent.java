package com.seatly.seatly.dto.websocket;

import com.seatly.seatly.domain.enums.WebSocketEventType;
import com.seatly.seatly.dto.timepass.TimePassRequest;

public record TimePassRequestEvent(
        WebSocketEventType type,
        TimePassRequest request) {
}
