package com.seatly.seatly.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPost {

  private String email;
  private String password;
  private String name;
  private String phone;

}
