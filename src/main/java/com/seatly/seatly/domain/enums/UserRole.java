package com.seatly.seatly.domain.enums;

public enum UserRole {
  USER,
  ADMIN,
  ;

  public static UserRole byObject(Object role) {
    if (role instanceof UserRole userRole) {
      return userRole;
    }

    if (role instanceof String roleString) {
      try {
        return UserRole.valueOf(roleString.toUpperCase());
      } catch (Exception e) {
        return USER;
      }
    }

    return USER;
  }
}
