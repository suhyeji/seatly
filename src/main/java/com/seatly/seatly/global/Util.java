package com.seatly.seatly.global;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Util {

  public static OffsetDateTime now() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }

}
