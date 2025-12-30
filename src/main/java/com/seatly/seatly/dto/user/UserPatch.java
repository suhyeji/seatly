package com.seatly.seatly.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatch {
  private String name;
  private String phone;
  private String imageUrl;
}
