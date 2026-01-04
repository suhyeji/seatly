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

  public static String randomStudyCafeName() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    SecureRandom random = new SecureRandom();
    return "스터디카페" + chars.charAt(random.nextInt(chars.length()));
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

  public static String randomAddress() {
    String[] cities = {
        "서울특별시", "부산광역시", "대구광역시", "인천광역시",
        "광주광역시", "대전광역시", "울산광역시"
    };

    String[] districts = {
        "강남구", "서초구", "송파구", "마포구", "영등포구",
        "해운대구", "수성구", "남구", "북구"
    };

    String[] roads = {
        "테헤란로", "강남대로", "올림픽로", "월드컵북로",
        "중앙대로", "문화로", "산단로"
    };

    SecureRandom random = new SecureRandom();

    String city = cities[random.nextInt(cities.length)];
    String district = districts[random.nextInt(districts.length)];
    String road = roads[random.nextInt(roads.length)];
    int number = random.nextInt(200) + 1;

    return city + " " + district + " " + road + " " + number;
  }

  public static String randomPhoneNumber() {
    SecureRandom random = new SecureRandom();

    int mid = random.nextInt(9000) + 1000; // 1000~9999
    int last = random.nextInt(9000) + 1000; // 1000~9999

    return "010-" + mid + "-" + last;
  }

}
