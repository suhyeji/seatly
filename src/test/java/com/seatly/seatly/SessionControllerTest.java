package com.seatly.seatly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seatly.seatly.domain.User;
import com.seatly.seatly.domain.enums.Facility;
import com.seatly.seatly.domain.enums.SeatStatus;
import com.seatly.seatly.domain.enums.SessionStatus;
import com.seatly.seatly.domain.enums.UserRole;
import com.seatly.seatly.domain.keys.UserTimePassId;
import com.seatly.seatly.dto.TimePass;
import com.seatly.seatly.dto.seat.SeatInfo;
import com.seatly.seatly.dto.seat.SeatPost;
import com.seatly.seatly.dto.session.SessionInfo;
import com.seatly.seatly.dto.studycafe.StudyCafeDetailPost;
import com.seatly.seatly.dto.user.UserPost;
import com.seatly.seatly.global.security.JwtTokenProvider;
import com.seatly.seatly.store.SeatStoreService;
import com.seatly.seatly.store.StudyCafeStoreService;
import com.seatly.seatly.store.UserStoreService;
import com.seatly.seatly.store.UserTimePassStoreService;

import lombok.extern.slf4j.Slf4j;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class SessionControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper mapper;

  @Autowired
  JwtTokenProvider jwtProvider;

  @Autowired
  UserStoreService userStoreService;

  @Autowired
  StudyCafeStoreService studyCafeStoreService;

  @Autowired
  UserTimePassStoreService userTimePassStoreService;

  @Autowired
  SeatStoreService seatStoreService;

  private String userToken1;
  private String userToken2;
  private String adminToken;

  private Long userId1;
  private Long userId2;
  private Long studyCafeId1;
  private Long expireTime;

  private List<Long> seatIds;

  @BeforeAll
  void setup() throws Exception {
    Pair<Long, String> user1 = signUpUser();
    userId1 = user1.getFirst();
    userToken1 = user1.getSecond();
    Pair<Long, String> user2 = signUpUser();
    userId2 = user2.getFirst();
    userToken2 = user2.getSecond();
    signUpAdminUser();

    studyCafeId1 = createStudyCafe();
    addSeats();
    expireTime = addUserTimePass(userId1, studyCafeId1);
  }

  @AfterAll
  void clear() throws Exception {
    seatIds.forEach(seatStoreService::deleteById);
    userTimePassStoreService.deleteById(new UserTimePassId(studyCafeId1, userId1));
    studyCafeStoreService.deleteById(studyCafeId1);
    deleteUsers();
    deleteAdminUser();
  }

  @Test
  @Order(1)
  void testAssign() throws Exception {
    // 좌석 지정 할당
    SessionInfo sessionInfo = postAssignSeat(0);

    assertEquals(SessionStatus.ASSIGNED, sessionInfo.getStatus());
    assertEquals(studyCafeId1, sessionInfo.getStudyCafeId());
    assertEquals(seatIds.get(0), sessionInfo.getSeatId());
    assertEquals(userId1, sessionInfo.getUserId());

    // 동일 좌석 다시 선점 시도하면 실패
    mockMvc.perform(
        post("/api/sessions/assign")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1)
            .param("seatId", seatIds.get(0).toString()))
        .andExpect(status().isConflict());

    // UNAVAILABLE 선점 시도하면 실패
    mockMvc.perform(
        post("/api/sessions/assign")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1)
            .param("seatId", seatIds.get(1).toString()))
        .andExpect(status().isConflict());

    MvcResult getResult = mockMvc.perform(
        get("/api/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1)
            .param("studyCafeId", studyCafeId1.toString()))
        .andExpect(status().isOk())
        .andReturn();

    String getBody = getResult.getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    List<SessionInfo> sessionInfos = mapper.readValue(getBody,
        new TypeReference<List<SessionInfo>>() {
        });

    assertEquals(1, sessionInfos.size());
    assertEquals(sessionInfo, sessionInfos.get(0));

    // 세션 삭제
    mockMvc.perform(
        delete("/api/sessions/" + sessionInfo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isOk());
  }

  @Test
  @Order(2)
  void testAutoAssign() throws Exception {
    // 좌석 자동 할당
    SessionInfo sessionInfo = postAutoAssignSeat(userToken1);
    assertEquals(SessionStatus.ASSIGNED, sessionInfo.getStatus());
    assertEquals(studyCafeId1, sessionInfo.getStudyCafeId());
    assertEquals(seatIds.get(0), sessionInfo.getSeatId());
    assertEquals(userId1, sessionInfo.getUserId());

    SessionInfo sessionInfo2 = postAutoAssignSeat(userToken2);
    assertEquals(SessionStatus.ASSIGNED, sessionInfo2.getStatus());
    assertEquals(studyCafeId1, sessionInfo2.getStudyCafeId());
    // get(1)은 UNAVAILABLE 좌석이므로 2가 나와야함
    assertEquals(seatIds.get(2), sessionInfo2.getSeatId());
    assertEquals(userId2, sessionInfo2.getUserId());

    List<SessionInfo> sessionInfos = getSessions();
    assertEquals(2, sessionInfos.size());
    assertEquals(sessionInfo, sessionInfos.get(0));
    assertEquals(sessionInfo2, sessionInfos.get(1));

    // 세션 삭제
    mockMvc.perform(
        delete("/api/sessions/" + sessionInfo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isOk());

    // 다른 사용자의 세션 삭제 시도 실패
    mockMvc.perform(
        delete("/api/sessions/" + sessionInfo2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isForbidden());

    mockMvc.perform(
        delete("/api/sessions/" + sessionInfo2.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken2))
        .andExpect(status().isOk());
  }

  @Test
  @Order(3)
  void testStartSession() throws Exception {
    SessionInfo sessionInfo = postAssignSeat(0);
    sessionInfo = postStartSession(sessionInfo.getId());
    assertEquals(SessionStatus.IN_USE, sessionInfo.getStatus());
    assertEquals(studyCafeId1, sessionInfo.getStudyCafeId());
    assertEquals(seatIds.get(0), sessionInfo.getSeatId());
    assertEquals(userId1, sessionInfo.getUserId());

    // 세션 삭제
    mockMvc.perform(
        delete("/api/sessions/" + sessionInfo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isOk());
  }

  @Test
  void testSessionExpire() throws Exception {
    SessionInfo sessionInfo = postAssignSeat(0);
    postStartSession(sessionInfo.getId());
    Thread.sleep(Duration.ofSeconds(expireTime + 10));

    List<SessionInfo> sessionInfos = getSessions();
    assertEquals(0, sessionInfos.size());

    List<TimePass> timePasses = userTimePassStoreService.findAllByUserId(userId1);
    assertEquals(0, timePasses.size());
  }

  private SessionInfo postAssignSeat(Integer seatIdx) throws Exception {
    ResultActions assignResult = mockMvc.perform(
        post("/api/sessions/assign")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1)
            .param("seatId", seatIds.get(seatIdx).toString()))
        .andExpect(status().isOk());
    String body = assignResult.andReturn().getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, SessionInfo.class);
  }

  private SessionInfo postAutoAssignSeat(String userToken) throws Exception {
    ResultActions assignResult = mockMvc.perform(
        post("/api/sessions/auto-assign")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
            .param("studyCafeId", studyCafeId1.toString()))
        .andExpect(status().isOk());
    String body = assignResult.andReturn().getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, SessionInfo.class);
  }

  private SessionInfo postStartSession(Long sessionId) throws Exception {
    ResultActions result = mockMvc.perform(
        patch("/api/sessions/" + sessionId + "/start")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isOk());
    String body = result.andReturn().getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, SessionInfo.class);
  }

  Pair<Long, String> signUpUser() throws Exception {
    UserPost userPost = new UserPost();
    String email = RandomUtil.randomEmail();
    userPost.setEmail(email);
    userPost.setPassword(RandomUtil.randomPassword());
    String username = RandomUtil.randomName();
    userPost.setName(username);
    userPost.setPhone(RandomUtil.randomPhoneNumber());
    userPost.setRole(UserRole.USER);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());

    User user = userStoreService.findByEmailOrNull(email);
    assertNotNull(user);
    assertNotNull(user.getId());

    Long userId = user.getId();
    String userToken = jwtProvider.createAccessToken(user.getId(), username, UserRole.USER.name());

    return Pair.of(userId, userToken);
  }

  void signUpAdminUser() throws Exception {
    UserPost userPost = new UserPost();
    String email = RandomUtil.randomEmail();
    userPost.setEmail(email);
    userPost.setPassword(RandomUtil.randomPassword());
    String username = RandomUtil.randomName();
    userPost.setName(username);
    userPost.setPhone(RandomUtil.randomPhoneNumber());
    userPost.setRole(UserRole.ADMIN);

    mockMvc.perform(
        post("/api/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(userPost)))
        .andExpect(status().isCreated());

    User user = userStoreService.findByEmailOrNull(email);
    assertNotNull(user);
    assertNotNull(user.getId());

    adminToken = jwtProvider.createAccessToken(user.getId(), username, UserRole.ADMIN.name());
  }

  Long createStudyCafe() throws Exception {
    StudyCafeDetailPost studyCafe = new StudyCafeDetailPost();
    studyCafe.setName(RandomUtil.randomStudyCafeName());
    studyCafe.setAddress(RandomUtil.randomAddress());
    studyCafe.setPhoneNumber(RandomUtil.randomPhoneNumber());
    studyCafe.setFacilities(List.of(Facility.AIR_CONDITIONING, Facility.WIFI));
    studyCafe.setOpeningHours("08:00 - 23:00");

    MvcResult result = mockMvc.perform(post("/api/study-cafes")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(studyCafe)))
        .andExpect(status().isCreated())
        .andReturn();

    String body = result.getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, Long.class);
  }

  void addSeats() throws Exception {
    List<SeatPost> seatPosts = getSeatPosts();
    mockMvc.perform(post("/api/study-cafes/" + studyCafeId1 + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(seatPosts)))
        .andExpect(status().isCreated());
    setSeatIds();
  }

  Long addUserTimePass(Long userId, Long studyCafeId) throws Exception {
    Long time = 5L;
    mockMvc.perform(post("/api/users/" + userId + "/time")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON)
        .param("studyCafeId", studyCafeId.toString())
        .param("time", time.toString()))
        .andExpect(status().isOk());
    return time;
  }

  private List<SeatPost> getSeatPosts() {
    SeatPost seat1 = new SeatPost();
    seat1.setName("좌석 1");
    seat1.setStatus(SeatStatus.AVAILABLE);
    seat1.setPosition("(1, 1");

    SeatPost seat2 = new SeatPost();
    seat2.setName("좌석 2");
    seat2.setStatus(SeatStatus.UNAVAILABLE);
    seat2.setPosition("(2, 2");

    SeatPost seat3 = new SeatPost();
    seat3.setName("좌석 3");
    seat3.setStatus(SeatStatus.AVAILABLE);
    seat3.setPosition("(3, 3");

    return List.of(seat1, seat2, seat3);
  }

  private void setSeatIds() throws Exception {
    ResultActions result = mockMvc.perform(get("/api/study-cafes/" + studyCafeId1 + "/seats")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    String body = result.andReturn().getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    seatIds = mapper.readValue(body, new TypeReference<List<SeatInfo>>() {
    }).stream()
        .map(SeatInfo::getId)
        .sorted()
        .toList();
  }

  private List<SessionInfo> getSessions() throws Exception {
    MvcResult result = mockMvc.perform(
        get("/api/sessions")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1)
            .param("studyCafeId", studyCafeId1.toString()))
        .andExpect(status().isOk())
        .andReturn();
    String body = result.getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
    return mapper.readValue(body, new TypeReference<List<SessionInfo>>() {
    });
  }

  void deleteUsers() throws Exception {
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken1))
        .andExpect(status().isNoContent());
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken2))
        .andExpect(status().isNoContent());
  }

  void deleteAdminUser() throws Exception {
    mockMvc.perform(
        delete("/api/user")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
        .andExpect(status().isNoContent());
  }

}
