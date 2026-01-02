package com.seatly.seatly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.auth.CustomUserDetails;
import com.seatly.seatly.domain.Seat;
import com.seatly.seatly.domain.Session;
import com.seatly.seatly.domain.StudyCafe;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.UserStudyCafeLink;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.studycafe.StudyCafeDetail;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummary;
import com.seatly.seatly.global.Util;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.SessionStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserStudyCafeLinkStoreService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyCafeControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  StudyCafeStoreService studyCafeStoreService;

  @Autowired
  UserStoreService userStoreService;

  @Autowired
  UserStudyCafeLinkStoreService linkStoreService;

  @Autowired
  SessionStoreService sessionStoreService;

  @Autowired
  SeatStoreService seatStoreService;

  @Autowired
  JwtTokenProvider jwtProvider;

  private CustomUserDetails adminUser;
  private String adminToken;
  private CustomUserDetails normalUser;
  private String normalToken;

  @BeforeEach
  void setUp() {
    User admin = new User();
    admin.setName("ADMIN");
    admin.setRole(UserRole.ADMIN);
    admin.setEmail("admin@seatly.com");
    admin.setPassword("admin_pw");
    userStoreService.save(admin);

    User user = new User();
    user.setName("USER");
    user.setRole(UserRole.USER);
    user.setEmail("user@seatly.com");
    user.setPassword("user_pw");
    userStoreService.save(user);

    adminUser = new CustomUserDetails(admin);
    adminToken = jwtProvider.createAccessToken(admin.getId(), admin.getName(), UserRole.ADMIN.name());
    normalUser = new CustomUserDetails(user);
    normalToken = jwtProvider.createAccessToken(user.getId(), user.getName(), UserRole.USER.name());
  }

  private static StudyCafe createCafeA() {
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

  private static StudyCafe createCafeB() {
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

  private static Seat createSeatA() {
    Seat seat = new Seat();
    seat.setName("의자 1");
    seat.setStudyCafe(createCafeA());
    seat.setStatus(SeatStatus.AVAILABLE);
    seat.setPosition("(1, 1");
    seat.setUpdatedAt(Util.now());
    return seat;
  }

  // 1. GET /api/study-cafes
  @Test
  void getStudySummaries_success() throws Exception {
    // given
    StudyCafe cafe1 = createCafeA();
    studyCafeStoreService.save(cafe1);

    StudyCafe cafe2 = createCafeB();
    studyCafeStoreService.save(cafe2);

    // when & then
    mockMvc.perform(get("/api/study-cafes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("스터디카페 A"))
        .andExpect(jsonPath("$[0].address").value("서울"))
        .andExpect(jsonPath("$[0].mainImageUrl")
            .value("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp1.png"))
        .andExpect(jsonPath("$[1].name").value("스터디카페 B"))
        .andExpect(jsonPath("$[1].address").value("부산"))
        .andExpect(jsonPath("$[1].mainImageUrl")
            .value("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp2.png"));
  }

  // 2. GET /api/study-cafes/{id}
  @Test
  void getStudyCafeDetail_success() throws Exception {
    StudyCafe cafe = createCafeA();
    studyCafeStoreService.save(cafe);

    // when
    MvcResult result = mockMvc.perform(get("/api/study-cafes/" + cafe.getId()))
        .andExpect(status().isOk())
        .andReturn();

    // then (DTO로 역직렬화해서 필드 단위 assert)
    String content = result.getResponse()
        .getContentAsString(StandardCharsets.UTF_8);

    StudyCafeDetail response = objectMapper.readValue(content, StudyCafeDetail.class);

    assertThat(response.getId()).isEqualTo(cafe.getId()); // 필드명이 다르면 수정
    assertThat(response.getName()).isEqualTo(cafe.getName());
    assertThat(response.getAddress()).isEqualTo(cafe.getAddress());
    assertThat(response.getImageUrls()).isEqualTo(cafe.getImageUrls());
    assertThat(response.getPhoneNumber()).isEqualTo(cafe.getPhoneNumber());
    assertThat(response.getFacilities()).isEqualTo(cafe.getFacilities());
    assertThat(response.getOpeningHours()).isEqualTo(cafe.getOpeningHours());
    assertThat(response.getDescription()).isEqualTo(cafe.getDescription());
  }

  // 3. GET /api/study-cafes/admin
  @Test
  void getAdminStudyCafeSummaries_admin_only() throws Exception {
    // given: 카페 2개
    StudyCafe cafeLinked = createCafeA();
    cafeLinked.setName("관리카페");
    studyCafeStoreService.save(cafeLinked);

    StudyCafe cafeNotLinked = createCafeB();
    cafeNotLinked.setName("다른카페");
    studyCafeStoreService.save(cafeNotLinked);

    // given: admin-user <-> cafeLinked ADMIN 링크 생성
    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setUser(userStoreService.findByIdOrNull(adminUser.getId()));
    link.setStudyCafe(cafeLinked);
    link.setLinkType(UserCafeLinkType.ADMIN);
    linkStoreService.save(link);

    // when
    MvcResult result = mockMvc.perform(
        get("/api/study-cafes/admin")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andReturn();

    // then: 리스트 역직렬화 후 검증
    List<StudyCafeSummary> response = objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        new TypeReference<List<StudyCafeSummary>>() {
        });

    assertThat(response).hasSize(1);
    assertThat(response.get(0).getId()).isEqualTo(cafeLinked.getId()); // 필드명 다르면 수정
    assertThat(response.get(0).getName()).isEqualTo("관리카페");
  }

  @Test
  void getAdminStudyCafeSummaries_not_admin_forbidden() throws Exception {
    mockMvc.perform(get("/api/study-cafes/admin")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isForbidden()); // 프로젝트 정책에 맞게 400/403 수정
  }

  // 4. GET /api/study-cafes/{id}/usage
  @Test
  void getStudyCafeUsage_success() throws Exception {
    // given
    StudyCafe cafe = createCafeA();
    studyCafeStoreService.save(cafe);

    // seat 3개 생성 (프로젝트 엔티티에 맞게 수정)
    Seat s1 = createSeatA();
    s1.setStudyCafe(cafe);
    s1.setName("의자 1");
    Seat s2 = createSeatA();
    s2.setStudyCafe(cafe);
    s2.setName("의자 2");
    Seat s3 = createSeatA();
    s3.setStudyCafe(cafe);
    s3.setName("의자 3");
    seatStoreService.save(s1);
    seatStoreService.save(s2);
    seatStoreService.save(s3);

    // 사용자
    User user1 = new User();
    user1.setName("USER1");
    user1.setRole(UserRole.USER);
    user1.setEmail("user1@seatly.com");
    user1.setPassword("user_pw1");
    userStoreService.save(user1);

    User user2 = new User();
    user2.setName("USER2");
    user2.setRole(UserRole.USER);
    user2.setEmail("user2@seatly.com");
    user2.setPassword("user_pw2");
    userStoreService.save(user2);

    // session 2개 생성 (프로젝트 엔티티/상태에 맞게 수정)
    Session se1 = new Session();
    se1.setSeat(s1);
    se1.setUser(user1);
    se1.setStatus(SessionStatus.IN_USE);
    se1.setStartTime(Util.now());
    sessionStoreService.save(se1);

    Session se2 = new Session();
    se2.setSeat(s2);
    se2.setUser(user2);
    se2.setStatus(SessionStatus.IN_USE);
    se2.setStartTime(Util.now());
    sessionStoreService.save(se2);

    // when & then
    mockMvc.perform(get("/api/study-cafes/" + cafe.getId() + "/usage"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(3))
        .andExpect(jsonPath("$.useCount").value(2));
  }

  // ===========================
  // POST /api/study-cafes
  // ===========================
  @Test
  void create_then_get_detail_value_match() throws Exception {
    StudyCafe cafe = createCafeA();
    StudyCafeDetailPost post = new StudyCafeDetailPost();
    post.setName(cafe.getName());
    post.setAddress(cafe.getAddress());
    post.setImages(cafe.getImageUrls());
    post.setPhoneNumber(cafe.getPhoneNumber());
    post.setFacilities(cafe.getFacilities());
    post.setOpeningHours(cafe.getOpeningHours());
    post.setDescription(cafe.getDescription());

    // 생성
    mockMvc.perform(post("/api/study-cafes")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isCreated());

    StudyCafe saved = studyCafeStoreService.findAll().get(0);

    // 조회 + 검증
    MvcResult result = mockMvc.perform(get("/api/study-cafes/" + saved.getId()))
        .andExpect(status().isOk())
        .andReturn();
    StudyCafeDetail response = objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        StudyCafeDetail.class);

    assertThat(response.getName()).isEqualTo(post.getName());
    assertThat(response.getAddress()).isEqualTo(post.getAddress());
    assertThat(response.getImageUrls()).isEqualTo(post.getImages());
    assertThat(response.getPhoneNumber()).isEqualTo(post.getPhoneNumber());
    assertThat(response.getFacilities()).isEqualTo(post.getFacilities());
    assertThat(response.getOpeningHours()).isEqualTo(post.getOpeningHours());
    assertThat(response.getDescription()).isEqualTo(post.getDescription());
  }

  // ===========================
  // PATCH
  // ===========================
  @Test
  void updateStudyCafe() throws Exception {
    StudyCafe cafe = new StudyCafe();
    cafe.setName("이전");
    studyCafeStoreService.save(cafe);

    StudyCafeDetailPost update = new StudyCafeDetailPost();
    update.setName("변경");

    mockMvc.perform(patch("/api/study-cafes/" + cafe.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk());

    StudyCafe updated = studyCafeStoreService.findByIdOrNull(cafe.getId());

    assertThat(updated.getName()).isEqualTo("변경");
  }

  // ===========================
  // 즐겨찾기
  // ===========================
  @Test
  void add_favorite_success() throws Exception {
    StudyCafe cafe = studyCafeStoreService.save(new StudyCafe());

    mockMvc.perform(post("/api/study-cafes/" + cafe.getId() + "/favorite")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isOk());
  }

  // ===========================
  // 관리자 전용 - 사용자 시간 삭제
  // ===========================
  @Test
  void delete_user_time_admin_only() throws Exception {
    StudyCafe cafe = studyCafeStoreService.save(new StudyCafe());

    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/users/2/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isOk());
  }

}
