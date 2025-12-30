package com.seatly.seatly.dto.user;

import com.seatly.seatly.domain.enums.UserRole;

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
  private String imageUrl;
  private UserRole role;

}
