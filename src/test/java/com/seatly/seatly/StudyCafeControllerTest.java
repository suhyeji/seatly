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
import com.seatly.seatly.domain.UserTimePass;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.TimePass;
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
import com.seatly.seatly.store.UserTimePassStoreService;

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
  UserTimePassStoreService userTimePassStoreService;

  @Autowired
  JwtTokenProvider jwtProvider;

  private CustomUserDetails adminUser;
  private String adminToken;
  private CustomUserDetails normalUser;
  private String normalToken;

  @BeforeEach
  void setUp() {
    User admin = TestDataUtil.adminUser();
    userStoreService.save(admin);

    User user = TestDataUtil.normalUser();
    userStoreService.save(user);

    adminUser = new CustomUserDetails(admin);
    adminToken = jwtProvider.createAccessToken(admin.getId(), admin.getName(), UserRole.ADMIN.name());
    normalUser = new CustomUserDetails(user);
    normalToken = jwtProvider.createAccessToken(user.getId(), user.getName(), UserRole.USER.name());
  }

  // 1. GET /api/study-cafes
  @Test
  void getStudySummaries_success() throws Exception {
    // given
    StudyCafe cafe1 = TestDataUtil.cafeA();
    studyCafeStoreService.save(cafe1);

    StudyCafe cafe2 = TestDataUtil.cafeB();
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
    StudyCafe cafe = TestDataUtil.cafeA();
    studyCafeStoreService.save(cafe);

    // when
    MvcResult result = mockMvc.perform(get("/api/study-cafes/" + cafe.getId()))
        .andExpect(status().isOk())
        .andReturn();

    // then (DTO로 역직렬화해서 필드 단위 assert)
    StudyCafeDetail response = objectMapper.readValue(
        result.getResponse().getContentAsString(StandardCharsets.UTF_8),
        StudyCafeDetail.class);

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
    StudyCafe cafeLinked = TestDataUtil.cafeA();
    cafeLinked.setName("관리카페");
    studyCafeStoreService.save(cafeLinked);

    StudyCafe cafeNotLinked = TestDataUtil.cafeB();
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
    StudyCafe cafe = TestDataUtil.cafeA();
    studyCafeStoreService.save(cafe);

    // seat 3개 생성 (프로젝트 엔티티에 맞게 수정)
    Seat s1 = TestDataUtil.seat(cafe, "의자 1");
    Seat s2 = TestDataUtil.seat(cafe, "의자 2");
    Seat s3 = TestDataUtil.seat(cafe, "의자 3");
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
    StudyCafe cafe = TestDataUtil.cafeA();
    StudyCafeDetailPost post = new StudyCafeDetailPost();
    post.setName(cafe.getName());
    post.setAddress(cafe.getAddress());
    post.setImageUrls(cafe.getImageUrls());
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
    assertThat(response.getImageUrls()).isEqualTo(post.getImageUrls());
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
    StudyCafe cafe = TestDataUtil.cafeA();
    cafe.setName("이전");
    StudyCafe saveCafe = studyCafeStoreService.save(cafe);

    StudyCafeDetailPost update = new StudyCafeDetailPost();
    update.setId(saveCafe.getId());
    update.setName("변경");

    mockMvc.perform(patch("/api/study-cafes/" + cafe.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
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
    StudyCafe cafe = studyCafeStoreService.save(TestDataUtil.cafeA());

    mockMvc.perform(post("/api/study-cafes/" + cafe.getId() + "/favorite")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isOk());

    List<UserStudyCafeLink> links = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        normalUser.getId(),
        UserCafeLinkType.FAVORITE);

    assertThat(links).hasSize(1);

    UserStudyCafeLink link = links.get(0);
    assertThat(link.getStudyCafe().getId()).isEqualTo(cafe.getId());
    assertThat(link.getUser().getId()).isEqualTo(normalUser.getId());
    assertThat(link.getLinkType()).isEqualTo(UserCafeLinkType.FAVORITE);
  }

  @Test
  void delete_favorite_success() throws Exception {
    // given: 카페 + 즐겨찾기 미리 생성
    StudyCafe cafe = studyCafeStoreService.save(TestDataUtil.cafeA());

    UserStudyCafeLink link = new UserStudyCafeLink();
    link.setStudyCafe(cafe);
    link.setUser(userStoreService.findByIdOrNull(normalUser.getId()));
    link.setLinkType(UserCafeLinkType.FAVORITE);
    linkStoreService.save(link);

    // sanity check (사전 조건 검증)
    List<UserStudyCafeLink> before = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        normalUser.getId(),
        UserCafeLinkType.FAVORITE);
    assertThat(before).hasSize(1);

    // when: 즐겨찾기 삭제 요청
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/favorite")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isOk());

    // then: link 테이블에서 제거되었는지 검증
    List<UserStudyCafeLink> after = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        normalUser.getId(),
        UserCafeLinkType.FAVORITE);

    assertThat(after).isEmpty();
  }

  // ===========================
  // 관리자 전용 - 사용자 시간 삭제
  // ===========================
  @Test
  void delete_user_time_by_study_cafe_id_when_admin() throws Exception {
    // given
    User user = userStoreService.findByIdOrNull(normalUser.getId());

    // user_time_pass 미리 생성
    StudyCafe cafe = studyCafeStoreService.save(TestDataUtil.cafeA());
    UserTimePass timePass = new UserTimePass();
    timePass.setId(new UserTimePassId(cafe.getId(), user.getId()));
    timePass.setStudyCafe(cafe);
    timePass.setUser(user);
    timePass.setLeftTime(3600L);
    timePass.setTotalTime(7200L);
    userTimePassStoreService.save(timePass);

    // 다른 카페의 timePass 하나 더 생성
    StudyCafe otherCafe = studyCafeStoreService.save(TestDataUtil.cafeB());
    UserTimePass otherTimePass = new UserTimePass();
    otherTimePass.setId(new UserTimePassId(otherCafe.getId(), user.getId()));
    otherTimePass.setStudyCafe(otherCafe);
    otherTimePass.setUser(user);
    otherTimePass.setLeftTime(1000L);
    otherTimePass.setTotalTime(2000L);
    userTimePassStoreService.save(otherTimePass);

    // sanity check
    List<TimePass> before = userTimePassStoreService.getTimePassesByUserId(user.getId());

    assertThat(before).hasSize(2);
    assertThat(before.get(0).getUserId()).isEqualTo(user.getId());
    assertThat(before.get(0).getStudyCafeId()).isEqualTo(cafe.getId());
    assertThat(before.get(1).getUserId()).isEqualTo(user.getId());
    assertThat(before.get(1).getStudyCafeId()).isEqualTo(otherCafe.getId());

    // when
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId()
        + "/users/" + user.getId() + "/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isOk());

    // then: DB에서 실제 삭제되었는지 검증

    List<TimePass> after = userTimePassStoreService.getTimePassesByUserId(user.getId());
    assertThat(after).hasSize(1);
    assertThat(after.get(0).getUserId()).isEqualTo(user.getId());
    assertThat(after.get(0).getStudyCafeId()).isEqualTo(otherCafe.getId());

  }

  @Test
  void delete_user_time_forbidden_when_not_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(TestDataUtil.cafeA());

    // when & then
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/users/2/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isForbidden());
  }

}
