package com.seatly.seatly.dto.user;

import com.seatly.seatly.domain.User;

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

  public User patch(User entity) {
    if (name != null) {
      entity.setName(name);
    }
    if (phone != null) {
      entity.setPhone(phone);
    }
    if (imageUrl != null) {
      entity.setImageUrl(imageUrl);
    }
    return entity;
  }

}
