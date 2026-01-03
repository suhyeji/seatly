package com.seatly.seatly;

import java.util.List;

import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.global.Util;

public class TestDataUtil {

  private TestDataUtil() {
  }

  // =========================
  // User
  // =========================
  public static User adminUser() {
    User user = new User();
    user.setName("ADMIN");
    user.setRole(UserRole.ADMIN);
    user.setEmail("admin@seatly.com");
    user.setPassword("admin_pw");
    return user;
  }

  public static User normalUser() {
    User user = new User();
    user.setName("USER");
    user.setRole(UserRole.USER);
    user.setEmail("user@seatly.com");
    user.setPassword("user_pw");
    return user;
  }

  // =========================
  // StudyCafe
  // =========================
  public static StudyCafe cafeA() {
    StudyCafe cafe = new StudyCafe();
    cafe.setName("스터디카페 A");
    cafe.setAddress("서울");
    cafe.setImageUrls(List.of(
        "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp1.png"));
    cafe.setPhoneNumber("010-1111-1111");
    cafe.setFacilities(List.of(Facility.AIR_CONDITIONING, Facility.WIFI));
    cafe.setOpeningHours("오전 9시-오후 12시");
    cafe.setDescription("스터디카페 설명");
    return cafe;
  }

  public static StudyCafe cafeB() {
    StudyCafe cafe = new StudyCafe();
    cafe.setName("스터디카페 B");
    cafe.setAddress("부산");
    cafe.setImageUrls(List.of(
        "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp2.png"));
    cafe.setPhoneNumber("010-2222-2222");
    cafe.setFacilities(List.of(Facility.AIR_CONDITIONING, Facility.WIFI));
    cafe.setOpeningHours("오전 8시-오후 11시");
    cafe.setDescription("스터디카페 설명");
    return cafe;
  }

  // =========================
  // Seat
  // =========================
  public static Seat seat(StudyCafe cafe, String name) {
    Seat seat = new Seat();
    seat.setName(name);
    seat.setStudyCafe(cafe);
    seat.setStatus(SeatStatus.AVAILABLE);
    seat.setPosition("(1,1)");
    seat.setUpdatedAt(Util.now());
    return seat;
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
  public static UserTimePass timePass(Long studyCafeId, Long userId,
      long left, long total) {
    UserTimePass tp = new UserTimePass();
    tp.setId(new UserTimePassId(studyCafeId, userId));
    tp.setLeftTime(left);
    tp.setTotalTime(total);
    return tp;
  }
}
