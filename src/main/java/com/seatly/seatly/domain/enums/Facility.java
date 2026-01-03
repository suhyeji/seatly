package com.seatly.seatly.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Facility {
  WIFI,
  PRINTER,
  OUTLET,
  OPEN_24H,
  CAFE,
  MEETING_ROOM,
  LOCKER,
  AIR_CONDITIONING;

  @JsonValue
  public String getValue() {
    return this.name();
  }

  @JsonCreator
  public static Facility from(String value) {
    return Facility.valueOf(value);
  }
}
