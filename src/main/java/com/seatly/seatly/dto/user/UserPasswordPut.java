package com.seatly.seatly.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordPut {

  private String currentPassword;
  private String newPassword;

}
