package com.seatly.seatly.global.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record ErrorResponse(
    String message,
    OffsetDateTime timestamp) {

  public static ErrorResponse of(String message) {
    return new ErrorResponse(message, OffsetDateTime.now(ZoneOffset.UTC));
  }
}
