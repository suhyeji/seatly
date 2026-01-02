package com.seatly.seatly;

import java.security.SecureRandom;
import java.util.UUID;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RandomUtil {

  public static String randomEmail() {
    return "test+" + UUID.randomUUID() + "@example.com";
  }

  public static String randomPassword() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    int length = 12;
    SecureRandom random = new SecureRandom();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
  }

  public static String randomName() {
    String[] lastNames = {
        "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임"
    };
    String[] firstNames = {
        "서준", "민준", "도윤", "예준", "시우",
        "서연", "지우", "하윤", "지민", "채원"
    };
    SecureRandom random = new SecureRandom();
    return lastNames[random.nextInt(lastNames.length)]
        + firstNames[random.nextInt(firstNames.length)];
  }

  public static String randomPhoneNumber() {
    SecureRandom random = new SecureRandom();

    int mid = random.nextInt(9000) + 1000; // 1000~9999
    int last = random.nextInt(9000) + 1000; // 1000~9999

    return "010-" + mid + "-" + last;
  }

}
