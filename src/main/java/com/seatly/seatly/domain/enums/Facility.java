package com.seatly.seatly.domain.enums;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Facility {
  WIFI("WIFI"),
  PRINTER("PRINTER"),
  OUTLET("OUTLET"),
  OPEN_24H("OPEN_24H"),
  CAFE("CAFE"),
  MEETING_ROOM("MEETING_ROOM"),
  LOCKER("LOCKER"),
  AIR_CONDITIONING("AIR_CONDITIONING");

  private final String value;

  Facility(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static Facility from(String value) {
    return Arrays.stream(values())
        .filter(v -> v.value.equals(value))
        .findFirst()
        .orElseThrow();
  }
}
