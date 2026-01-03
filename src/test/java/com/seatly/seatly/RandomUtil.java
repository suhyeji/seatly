package com.seatly.seatly;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.global.Util;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RandomUtil {

  private static final Random RANDOM = new Random();
  private static final AtomicInteger SEQ = new AtomicInteger(1);

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

  // =========================
  // User
  // =========================
  public static User randomAdminUser() {
    return randomUser(UserRole.ADMIN);
  }

  public static User randomNormalUser() {
    return randomUser(UserRole.USER);
  }

  private static User randomUser(UserRole role) {
    int no = SEQ.getAndIncrement();

    User user = new User();
    user.setName(role.name() + "_" + no);
    user.setRole(role);
    user.setEmail(role.name().toLowerCase() + no + "@test.com");
    user.setPassword("pw_" + UUID.randomUUID());
    return user;
  }

  // =========================
  // StudyCafe
  // =========================
  public static StudyCafe randomStudyCafe() {
    int no = SEQ.getAndIncrement();

    StudyCafe cafe = new StudyCafe();
    cafe.setName("스터디카페_" + no);
    cafe.setAddress(randomAddress());
    cafe.setImageUrls(List.of(randomImageUrl()));
    cafe.setPhoneNumber("010-" + randomNumber(1000, 9999)
        + "-" + randomNumber(1000, 9999));
    cafe.setFacilities(randomFacilities());
    cafe.setOpeningHours(randomOpeningHours());
    cafe.setDescription("설명_" + UUID.randomUUID().toString().substring(0, 8));
    return cafe;
  }

  private static String randomAddress() {
    return switch (RANDOM.nextInt(3)) {
      case 0 -> "서울";
      case 1 -> "부산";
      default -> "대구";
    };
  }

  private static String randomImageUrl() {
    return "https://test.image/" + UUID.randomUUID() + ".png";
  }

  private static List<Facility> randomFacilities() {
    Facility[] values = Facility.values();
    return List.of(
        values[RANDOM.nextInt(values.length)],
        values[RANDOM.nextInt(values.length)]).stream().distinct().toList();
  }

  private static String randomOpeningHours() {
    int start = randomNumber(7, 10);
    int end = randomNumber(18, 24);
    return "오전 " + start + "시-오후 " + end + "시";
  }

  // =========================
  // Seat
  // =========================
  public static Seat randomSeat(StudyCafe cafe) {
    Seat seat = new Seat();
    seat.setName("좌석_" + SEQ.getAndIncrement());
    seat.setStudyCafe(cafe);
    seat.setStatus(SeatStatus.AVAILABLE);
    seat.setPosition("(" + randomNumber(1, 5) + "," + randomNumber(1, 5) + ")");
    seat.setUpdatedAt(Util.now());
    return seat;
  }

  // =========================
  // Seat
  // =========================
  public static Session randomSession(Seat seat, User user) {
    Session session = new Session();
    session.setSeat(seat);
    session.setUser(user);
    session.setStatus(SessionStatus.IN_USE);
    session.setStartTime(Util.now());
    return session;
  }

  // =========================
  // UserStudyCafeLink
  // =========================
  public static UserStudyCafeLink favoriteLink(User user, StudyCafe cafe) {
    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setUser(user);
    link.setStudyCafe(cafe);
    link.setLinkType(UserCafeLinkType.FAVORITE);
    return link;
  }

  public static UserStudyCafeLink adminLink(User user, StudyCafe cafe) {
    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setUser(user);
    link.setStudyCafe(cafe);
    link.setLinkType(UserCafeLinkType.ADMIN);
    return link;
  }

  // =========================
  // UserTimePass
  // =========================
  public static UserTimePass randomTimePass(StudyCafe cafe, User user) {
    long total = randomNumber(3600, 14400);
    long left = randomNumber(0, (int) total);

    UserTimePass tp = new UserTimePass();
    tp.setId(new UserTimePassId(cafe.getId(), user.getId()));
    tp.setStudyCafe(cafe);
    tp.setUser(user);
    tp.setLeftTime(left);
    tp.setTotalTime(total);
    return tp;
  }

  // =========================
  // Utils
  // =========================
  private static int randomNumber(int min, int max) {
    return RANDOM.nextInt(max - min + 1) + min;
  }

}
