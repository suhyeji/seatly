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
import com.seatly.seatly.domain.enums.UserCafeLinkType;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.studycafe.StudyCafeDetail;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.studycafe.StudyCafeSummary;
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
    User admin = RandomUtil.randomAdminUser();
    userStoreService.save(admin);

    User user = RandomUtil.randomNormalUser();
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
    StudyCafe cafe1 = RandomUtil.randomStudyCafe();
    studyCafeStoreService.save(cafe1);

    StudyCafe cafe2 = RandomUtil.randomStudyCafe();
    studyCafeStoreService.save(cafe2);

    // when & then
    mockMvc.perform(get("/api/study-cafes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value(cafe1.getName()))
        .andExpect(jsonPath("$[0].address").value(cafe1.getAddress()))
        .andExpect(jsonPath("$[0].mainImageUrl").value(cafe1.getImageUrls().get(0)))
        .andExpect(jsonPath("$[1].name").value(cafe2.getName()))
        .andExpect(jsonPath("$[1].address").value(cafe2.getAddress()))
        .andExpect(jsonPath("$[1].mainImageUrl").value(cafe2.getImageUrls().get(0)));
  }

  // 2. GET /api/study-cafes/{id}
  @Test
  void getStudyCafeDetail_success() throws Exception {
    StudyCafe cafe = RandomUtil.randomStudyCafe();
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
    StudyCafe cafeLinked = RandomUtil.randomStudyCafe();
    studyCafeStoreService.save(cafeLinked);

    StudyCafe cafeNotLinked = RandomUtil.randomStudyCafe();
    studyCafeStoreService.save(cafeNotLinked);

    // given: admin-user <-> cafeLinked ADMIN 링크 생성
    User user = userStoreService.findByIdOrNull(adminUser.getId());
    UserStudyCafeLink link = RandomUtil.adminLink(user, cafeLinked);
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
    assertThat(response.get(0).getName()).isEqualTo(cafeLinked.getName());
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
    StudyCafe cafe = RandomUtil.randomStudyCafe();
    studyCafeStoreService.save(cafe);

    // seat 3개 생성 (프로젝트 엔티티에 맞게 수정)
    Seat s1 = RandomUtil.randomSeat(cafe);
    Seat s2 = RandomUtil.randomSeat(cafe);
    Seat s3 = RandomUtil.randomSeat(cafe);
    seatStoreService.save(s1);
    seatStoreService.save(s2);
    seatStoreService.save(s3);

    // 사용자
    User user1 = RandomUtil.randomNormalUser();
    userStoreService.save(user1);
    User user2 = RandomUtil.randomNormalUser();
    userStoreService.save(user2);

    // session 2개 생성 (프로젝트 엔티티/상태에 맞게 수정)
    Session se1 = RandomUtil.randomSession(s1, user1);
    sessionStoreService.save(se1);
    Session se2 = RandomUtil.randomSession(s2, user2);
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
    StudyCafe cafe = RandomUtil.randomStudyCafe();
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

    assertThat(response.getName()).isEqualTo(saved.getName());
    assertThat(response.getAddress()).isEqualTo(saved.getAddress());
    assertThat(response.getImageUrls()).isEqualTo(saved.getImageUrls());
    assertThat(response.getPhoneNumber()).isEqualTo(saved.getPhoneNumber());
    assertThat(response.getFacilities()).isEqualTo(saved.getFacilities());
    assertThat(response.getOpeningHours()).isEqualTo(saved.getOpeningHours());
    assertThat(response.getDescription()).isEqualTo(saved.getDescription());
  }

  // ===========================
  // PATCH
  // ===========================
  @Test
  void updateStudyCafe() throws Exception {
    StudyCafe cafe = RandomUtil.randomStudyCafe();
    StudyCafe saveCafe = studyCafeStoreService.save(cafe);

    StudyCafeDetailPost update = new StudyCafeDetailPost();
    update.setName("변경");
    update.setId(saveCafe.getId());

    mockMvc.perform(patch("/api/study-cafes/" + cafe.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk());

    StudyCafe updated = studyCafeStoreService.findByIdOrNull(cafe.getId());
    assertThat(updated.getName()).isEqualTo(update.getName());
  }

  // ===========================
  // 즐겨찾기
  // ===========================
  @Test
  void add_favorite_success() throws Exception {
    User user = userStoreService.save(RandomUtil.randomNormalUser());
    String token = jwtProvider.createAccessToken(user.getId(), user.getName(), UserRole.USER.name());
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    mockMvc.perform(post("/api/study-cafes/" + cafe.getId() + "/favorite")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());

    List<UserStudyCafeLink> links = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        user.getId(),
        UserCafeLinkType.FAVORITE);

    assertThat(links).hasSize(1);

    UserStudyCafeLink link = links.get(0);
    assertThat(link.getStudyCafe().getId()).isEqualTo(cafe.getId());
    assertThat(link.getUser().getId()).isEqualTo(user.getId());
    assertThat(link.getLinkType()).isEqualTo(UserCafeLinkType.FAVORITE);
  }

  @Test
  void delete_favorite_success() throws Exception {
    // given: 카페 + 즐겨찾기 미리 생성
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());
    studyCafeStoreService.save(cafe);
    User user = RandomUtil.randomNormalUser();
    userStoreService.save(user);
    String token = jwtProvider.createAccessToken(user.getId(), user.getName(), UserRole.USER.name());

    UserStudyCafeLink link = RandomUtil.favoriteLink(user, cafe);
    linkStoreService.save(link);

    // sanity check (사전 조건 검증)
    List<UserStudyCafeLink> before = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        user.getId(),
        UserCafeLinkType.FAVORITE);
    assertThat(before).hasSize(1);

    // when: 즐겨찾기 삭제 요청
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/favorite")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());

    // then: link 테이블에서 제거되었는지 검증
    List<UserStudyCafeLink> after = linkStoreService.getUserStudyCafeLinkByUserIdAndLinkType(
        user.getId(),
        UserCafeLinkType.FAVORITE);

    assertThat(after).isEmpty();
  }

  // ===========================
  // 관리자 전용 - 사용자 시간 삭제
  // ===========================
  @Test
  void delete_user_time_by_study_cafe_id_when_admin() throws Exception {
    // given
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());
    studyCafeStoreService.save(cafe);
    User user = RandomUtil.randomNormalUser();
    userStoreService.save(user);

    // user_time_pass 미리 생성
    UserTimePass timePass = RandomUtil.randomTimePass(cafe, user);
    userTimePassStoreService.save(timePass);

    // 다른 카페의 timePass 하나 더 생성
    StudyCafe otherCafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());
    UserTimePass otherTimePass = RandomUtil.randomTimePass(otherCafe, user);
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
    StudyCafe cafe = studyCafeStoreService.save(RandomUtil.randomStudyCafe());

    // when & then
    mockMvc.perform(delete("/api/study-cafes/" + cafe.getId() + "/users/2/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + normalToken))
        .andExpect(status().isForbidden());
  }

}
