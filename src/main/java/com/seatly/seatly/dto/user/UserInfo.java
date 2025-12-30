package com.seatly.seatly.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

  private String email;
  private String name;
  private String phone;
  private String imageUrl;

}
